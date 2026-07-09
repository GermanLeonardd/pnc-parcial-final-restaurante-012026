package com.uca.pncparcialfinalrestaurante.repository;

import com.uca.pncparcialfinalrestaurante.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteId(Long clienteId);

    /**
     * Pedidos cuya mesa pertenece a una sucursal específica.
     * Clave para la regla de negocio de autorización por atributo (Opción B):
     * permite validar si el pedido pertenece a la sucursal del Encargado de turno.
     */
    List<Pedido> findByMesa_Restaurante_Id(Long restauranteId);
}