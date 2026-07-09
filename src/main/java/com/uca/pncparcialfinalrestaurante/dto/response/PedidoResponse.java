package com.uca.pncparcialfinalrestaurante.dto.response;

import com.uca.pncparcialfinalrestaurante.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        Long mesaId,
        Integer mesaNumero,
        Long restauranteId,
        EstadoPedido estado,
        List<ItemPedidoResponse> items,
        BigDecimal total,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {
}