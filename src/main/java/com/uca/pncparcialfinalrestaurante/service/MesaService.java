package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.MesaRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.MesaResponse;
import com.uca.pncparcialfinalrestaurante.exception.AccesoDenegadoSucursalException;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.model.EstadoMesa;
import com.uca.pncparcialfinalrestaurante.model.Mesa;
import com.uca.pncparcialfinalrestaurante.model.Restaurante;
import com.uca.pncparcialfinalrestaurante.repository.MesaRepository;
import com.uca.pncparcialfinalrestaurante.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;
    private final RestauranteService restauranteService;

    @Transactional
    public MesaResponse crear(MesaRequest request) {
        Restaurante restaurante = restauranteService.buscarPorId(request.restauranteId());
        Mesa mesa = Mesa.builder()
                .numero(request.numero())
                .capacidad(request.capacidad())
                .estado(EstadoMesa.DISPONIBLE)
                .restaurante(restaurante)
                .build();
        return toResponse(mesaRepository.save(mesa));
    }

    public List<MesaResponse> listarPorRestaurante(Long restauranteId) {
        return mesaRepository.findByRestauranteId(restauranteId).stream().map(this::toResponse).toList();
    }

    public List<MesaResponse> listarTodas() {
        return mesaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public MesaResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public MesaResponse actualizarEstado(Long id, EstadoMesa nuevoEstado, UserDetailsImpl usuarioAutenticado) {
        Mesa mesa = buscarPorId(id);
        verificarAccesoSucursal(usuarioAutenticado, mesa.getRestaurante().getId());
        mesa.setEstado(nuevoEstado);
        return toResponse(mesa);
    }

    public Mesa buscarPorId(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con id: " + id));
    }

    /**
     * REGLA DE NEGOCIO (Opción B - Autorización por atributo):
     * Un ENCARGADO_TURNO solo puede operar sobre mesas/pedidos de SU propia sucursal.
     * El ADMINISTRADOR no tiene esta restricción (se valida antes de llamar aquí, o se omite la llamada).
     */
    public void verificarAccesoSucursal(UserDetailsImpl usuarioAutenticado, Long restauranteIdDelRecurso) {
        boolean esEncargado = usuarioAutenticado.getUsuario().getRol().name().equals("ENCARGADO_TURNO");
        if (esEncargado) {
            Long restauranteIdUsuario = usuarioAutenticado.getRestauranteId();
            if (restauranteIdUsuario == null || !restauranteIdUsuario.equals(restauranteIdDelRecurso)) {
                throw new AccesoDenegadoSucursalException(
                        "No tienes permiso sobre recursos de una sucursal distinta a la tuya");
            }
        }
    }

    private MesaResponse toResponse(Mesa m) {
        return new MesaResponse(m.getId(), m.getNumero(), m.getCapacidad(), m.getEstado(),
                m.getRestaurante().getId(), m.getRestaurante().getNombre());
    }
}