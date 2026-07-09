package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.RestauranteRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.RestauranteResponse;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.model.Restaurante;
import com.uca.pncparcialfinalrestaurante.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    @Transactional
    public RestauranteResponse crear(RestauranteRequest request) {
        Restaurante restaurante = Restaurante.builder()
                .nombre(request.nombre())
                .direccion(request.direccion())
                .ciudad(request.ciudad())
                .telefono(request.telefono())
                .build();
        return toResponse(restauranteRepository.save(restaurante));
    }

    public List<RestauranteResponse> listar() {
        return restauranteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public RestauranteResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public RestauranteResponse actualizar(Long id, RestauranteRequest request) {
        Restaurante restaurante = buscarPorId(id);
        restaurante.setNombre(request.nombre());
        restaurante.setDireccion(request.direccion());
        restaurante.setCiudad(request.ciudad());
        restaurante.setTelefono(request.telefono());
        return toResponse(restaurante);
    }

    @Transactional
    public void eliminar(Long id) {
        Restaurante restaurante = buscarPorId(id);
        restauranteRepository.delete(restaurante);
    }

    public Restaurante buscarPorId(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado con id: " + id));
    }

    private RestauranteResponse toResponse(Restaurante r) {
        return new RestauranteResponse(r.getId(), r.getNombre(), r.getDireccion(), r.getCiudad(), r.getTelefono());
    }
}