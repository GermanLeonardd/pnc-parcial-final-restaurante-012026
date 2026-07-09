package com.uca.pncparcialfinalrestaurante.controller;

import com.uca.pncparcialfinalrestaurante.dto.request.RestauranteRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.RestauranteResponse;
import com.uca.pncparcialfinalrestaurante.service.RestauranteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RestauranteResponse> crear(@Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.ok(restauranteService.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<List<RestauranteResponse>> listar() {
        return ResponseEntity.ok(restauranteService.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ENCARGADO_TURNO')")
    public ResponseEntity<RestauranteResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.obtener(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<RestauranteResponse> actualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.ok(restauranteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        restauranteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}