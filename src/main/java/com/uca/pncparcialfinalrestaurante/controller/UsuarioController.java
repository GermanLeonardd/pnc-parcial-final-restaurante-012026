package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.request.UsuarioRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.UsuarioResponse;
import com.uca.pncparcialfinalrestaurante.model.Rol;
import com.uca.pncparcialfinalrestaurante.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.crear(request));
    }

    /**
     * Registro público: cualquier persona puede crear su cuenta,
     * pero siempre queda forzada como CLIENTE (no puede autoasignarse ADMIN ni ENCARGADO).
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrarCliente(@Valid @RequestBody UsuarioRequest request) {
        UsuarioRequest forzadoComoCliente = new UsuarioRequest(
                request.username(), request.password(), request.nombreCompleto(), Rol.CLIENTE, null);
        return ResponseEntity.ok(usuarioService.crear(forzadoComoCliente));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtener(id));
    }
}