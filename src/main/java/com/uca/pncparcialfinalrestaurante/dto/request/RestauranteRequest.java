package com.uca.pncparcialfinalrestaurante.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RestauranteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion,

        @NotBlank(message = "La ciudad es obligatoria")
        String ciudad,

        String telefono
) {
}