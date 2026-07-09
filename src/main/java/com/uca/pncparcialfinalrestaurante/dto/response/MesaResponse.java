package com.uca.pncparcialfinalrestaurante.dto.response;

import com.uca.pncparcialfinalrestaurante.model.EstadoMesa;

public record MesaResponse(
        Long id,
        Integer numero,
        Integer capacidad,
        EstadoMesa estado,
        Long restauranteId,
        String restauranteNombre
) {
}