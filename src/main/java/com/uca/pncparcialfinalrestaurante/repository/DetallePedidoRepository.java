package com.uca.pncparcialfinalrestaurante.repository;

import com.uca.pncparcialfinalrestaurante.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}