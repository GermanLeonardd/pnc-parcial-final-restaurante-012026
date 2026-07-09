package com.uca.pncparcialfinalrestaurante.dto.request;

import com.uca.pncparcialfinalrestaurante.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        // Obligatorio solo si rol = ENCARGADO_TURNO
        Long restauranteId
) {
}