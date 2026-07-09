package com.uca.pncparcialfinalrestaurante.dto.response;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Boolean disponible
) {
}