package com.uca.pncparcialfinalrestaurante.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MesaRequest(
        @NotNull(message = "El número de mesa es obligatorio")
        Integer numero,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacidad,

        @NotNull(message = "El restaurante es obligatorio")
        Long restauranteId
) {
}