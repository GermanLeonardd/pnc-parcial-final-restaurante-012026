package com.uca.pncparcialfinalrestaurante.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
        @NotNull(message = "La mesa es obligatoria")
        Long mesaId,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid
        List<ItemPedidoRequest> items
) {
}