package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.request.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.PedidoResponse;
import com.uca.pncparcialfinalrestaurante.security.UserDetailsImpl;
import com.uca.pncparcialfinalrestaurante.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponse> crear(
            @Valid @RequestBody PedidoRequest request,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        return ResponseEntity.ok(pedidoService.crear(request, usuarioAutenticado));
    }

    @GetMapping("/mios")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<PedidoResponse>> misPedidos(@AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        return ResponseEntity.ok(pedidoService.listarMisPedidos(usuarioAutenticado.getUsuarioId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/sucursal/{restauranteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<List<PedidoResponse>> listarPorSucursal(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(pedidoService.listarPorSucursal(restauranteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        // Visibilidad diferenciada por rol (validada dentro del service)
        return ResponseEntity.ok(pedidoService.obtenerConValidacion(id, usuarioAutenticado));
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<PedidoResponse> confirmar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        return ResponseEntity.ok(pedidoService.confirmar(id, usuarioAutenticado));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PedidoResponse> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        // ADMIN: cualquiera | ENCARGADO_TURNO: solo su sucursal | CLIENTE: solo el suyo (validado en el service)
        return ResponseEntity.ok(pedidoService.cancelar(id, usuarioAutenticado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<PedidoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam com.uca.pncparcialfinalrestaurante.model.EstadoPedido estado,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado, usuarioAutenticado));
    }
}