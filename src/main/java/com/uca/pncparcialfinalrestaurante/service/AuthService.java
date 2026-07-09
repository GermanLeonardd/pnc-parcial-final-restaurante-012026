package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.LoginRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.RefreshTokenRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.AuthResponse;
import com.uca.pncparcialfinalrestaurante.exception.InvalidRefreshTokenException;
import com.uca.pncparcialfinalrestaurante.model.RefreshToken;
import com.uca.pncparcialfinalrestaurante.model.Usuario;
import com.uca.pncparcialfinalrestaurante.repository.RefreshTokenRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidRefreshTokenException("Usuario no encontrado"));

        String accessToken = jwtService.generateAccessToken(usuario.getUsername(), usuario.getRol().name());
        String refreshTokenStr = jwtService.generateRefreshToken(usuario.getUsername());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .usuario(usuario)
                .expiraEn(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationMs() / 1000))
                .revocado(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenStr);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token no encontrado"));

        if (Boolean.TRUE.equals(storedToken.getRevocado())) {
            throw new InvalidRefreshTokenException("Refresh token revocado");
        }

        if (storedToken.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token expirado");
        }

        String username = jwtService.extractUsername(request.refreshToken());
        if (jwtService.isTokenExpired(request.refreshToken())) {
            throw new InvalidRefreshTokenException("Refresh token expirado");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidRefreshTokenException("Usuario no encontrado"));

        String newAccessToken = jwtService.generateAccessToken(usuario.getUsername(), usuario.getRol().name());

        // Rotación de refresh token: se revoca el anterior y se emite uno nuevo
        storedToken.setRevocado(true);
        refreshTokenRepository.save(storedToken);

        String newRefreshTokenStr = jwtService.generateRefreshToken(usuario.getUsername());
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .usuario(usuario)
                .expiraEn(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationMs() / 1000))
                .revocado(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshTokenStr);
    }
}