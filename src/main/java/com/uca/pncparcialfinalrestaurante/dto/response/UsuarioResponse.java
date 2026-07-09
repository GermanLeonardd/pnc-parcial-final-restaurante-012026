package com.uca.pncparcialfinalrestaurante.dto.response;

import com.uca.pncparcialfinalrestaurante.model.Rol;

public record UsuarioResponse(
        Long id,
        String username,
        String nombreCompleto,
        Rol rol,
        Long restauranteId,
        String restauranteNombre,
        Boolean activo
) {
}