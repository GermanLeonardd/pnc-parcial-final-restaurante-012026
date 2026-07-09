package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.UsuarioRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.UsuarioResponse;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.model.Restaurante;
import com.uca.pncparcialfinalrestaurante.model.Rol;
import com.uca.pncparcialfinalrestaurante.model.Usuario;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RestauranteService restauranteService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        Restaurante restaurante = null;
        if (request.rol() == Rol.ENCARGADO_TURNO) {
            if (request.restauranteId() == null) {
                throw new IllegalArgumentException("El ENCARGADO_TURNO debe tener un restaurante asignado");
            }
            restaurante = restauranteService.buscarPorId(request.restauranteId());
        }

        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nombreCompleto(request.nombreCompleto())
                .rol(request.rol())
                .restaurante(restaurante)
                .activo(true)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getUsername(),
                u.getNombreCompleto(),
                u.getRol(),
                u.getRestaurante() != null ? u.getRestaurante().getId() : null,
                u.getRestaurante() != null ? u.getRestaurante().getNombre() : null,
                u.getActivo()
        );
    }
}