package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.request.MesaRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.MesaResponse;
import com.uca.pncparcialfinalrestaurante.model.EstadoMesa;
import com.uca.pncparcialfinalrestaurante.security.UserDetailsImpl;
import com.uca.pncparcialfinalrestaurante.service.MesaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MesaResponse> crear(@Valid @RequestBody MesaRequest request) {
        return ResponseEntity.ok(mesaService.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<MesaResponse>> listarTodas() {
        return ResponseEntity.ok(mesaService.listarTodas());
    }

    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<List<MesaResponse>> listarPorRestaurante(@PathVariable Long restauranteId) {
        return ResponseEntity.ok(mesaService.listarPorRestaurante(restauranteId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<MesaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mesaService.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<MesaResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoMesa estado,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        return ResponseEntity.ok(mesaService.actualizarEstado(id, estado, usuarioAutenticado));
    }
}
