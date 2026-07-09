package com.uca.pncparcialfinalrestaurante.dto.response;

public record RestauranteResponse(
        Long id,
        String nombre,
        String direccion,
        String ciudad,
        String telefono
) {
}