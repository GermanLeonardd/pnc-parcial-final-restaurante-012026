package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.ItemPedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.request.PedidoRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.ItemPedidoResponse;
import com.uca.pncparcialfinalrestaurante.dto.response.PedidoResponse;
import com.uca.pncparcialfinalrestaurante.exception.AccesoDenegadoSucursalException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.model.*;
import com.uca.pncparcialfinalrestaurante.repository.DetallePedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.PedidoRepository;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import com.uca.pncparcialfinalrestaurante.repository.UsuarioRepository;
import com.uca.pncparcialfinalrestaurante.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MesaService mesaService;

    @Transactional
    public PedidoResponse crear(PedidoRequest request, UserDetailsImpl usuarioAutenticado) {
        Mesa mesa = mesaService.buscarPorId(request.mesaId());
        Usuario cliente = usuarioRepository.findById(usuarioAutenticado.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .estado(EstadoPedido.PENDIENTE)
                .build();
        pedido = pedidoRepository.save(pedido);

        for (ItemPedidoRequest item : request.items()) {
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + item.productoId()));

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.cantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            pedido.getDetalles().add(detallePedidoRepository.save(detalle));
        }

        return toResponse(pedido);
    }

    public List<PedidoResponse> listarMisPedidos(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> listarPorSucursal(Long restauranteId) {
        return pedidoRepository.findByMesa_Restaurante_Id(restauranteId).stream().map(this::toResponse).toList();
    }

    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PedidoResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public PedidoResponse confirmar(Long id, UserDetailsImpl usuarioAutenticado) {
        Pedido pedido = buscarPorId(id);
        validarAccesoSegunRol(pedido, usuarioAutenticado);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedido.setActualizadoEn(LocalDateTime.now());
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cancelar(Long id, UserDetailsImpl usuarioAutenticado) {
        Pedido pedido = buscarPorId(id);
        validarAccesoSegunRol(pedido, usuarioAutenticado);
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setActualizadoEn(LocalDateTime.now());
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevoEstado, UserDetailsImpl usuarioAutenticado) {
        Pedido pedido = buscarPorId(id);
        validarAccesoSegunRol(pedido, usuarioAutenticado);
        pedido.setEstado(nuevoEstado);
        pedido.setActualizadoEn(LocalDateTime.now());
        return toResponse(pedido);
    }

    /**
     * REGLA DE NEGOCIO (Opción B):
     * - ADMINISTRADOR: acceso total, sin restricción.
     * - ENCARGADO_TURNO: solo pedidos de SU sucursal (compara restauranteId del usuario
     *   contra el restauranteId de la mesa asociada al pedido).
     * - CLIENTE: solo sus propios pedidos.
     */
    private void validarAccesoSegunRol(Pedido pedido, UserDetailsImpl usuarioAutenticado) {
        Rol rol = usuarioAutenticado.getUsuario().getRol();

        switch (rol) {
            case ADMINISTRADOR -> { /* acceso total, no se valida nada */ }
            case ENCARGADO_TURNO -> {
                Long restauranteIdPedido = pedido.getMesa().getRestaurante().getId();
                Long restauranteIdUsuario = usuarioAutenticado.getRestauranteId();
                if (restauranteIdUsuario == null || !restauranteIdUsuario.equals(restauranteIdPedido)) {
                    throw new AccesoDenegadoSucursalException(
                            "No puedes gestionar pedidos de una sucursal distinta a la tuya");
                }
            }
            case CLIENTE -> {
                if (!pedido.getCliente().getId().equals(usuarioAutenticado.getUsuarioId())) {
                    throw new AccesoDenegadoSucursalException("No puedes gestionar pedidos de otro cliente");
                }
            }
        }
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    public PedidoResponse obtenerConValidacion(Long id, UserDetailsImpl usuarioAutenticado) {
        Pedido pedido = buscarPorId(id);
        validarAccesoSegunRol(pedido, usuarioAutenticado);
        return toResponse(pedido);
    }

    private PedidoResponse toResponse(Pedido p) {
        List<ItemPedidoResponse> items = p.getDetalles().stream()
                .map(d -> new ItemPedidoResponse(
                        d.getProducto().getId(),
                        d.getProducto().getNombre(),
                        d.getCantidad(),
                        d.getPrecioUnitario(),
                        d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                )).toList();

        BigDecimal total = items.stream()
                .map(ItemPedidoResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(
                p.getId(),
                p.getCliente().getId(),
                p.getCliente().getNombreCompleto(),
                p.getMesa().getId(),
                p.getMesa().getNumero(),
                p.getMesa().getRestaurante().getId(),
                p.getEstado(),
                items,
                total,
                p.getCreadoEn(),
                p.getActualizadoEn()
        );
    }
}