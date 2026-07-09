package com.uca.pncparcialfinalrestaurante.service;

import com.uca.pncparcialfinalrestaurante.dto.request.ProductoRequest;
import com.uca.pncparcialfinalrestaurante.dto.response.ProductoResponse;
import com.uca.pncparcialfinalrestaurante.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalrestaurante.model.Producto;
import com.uca.pncparcialfinalrestaurante.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = Producto.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .disponible(true)
                .build();
        return toResponse(productoRepository.save(producto));
    }

    public List<ProductoResponse> listarDisponibles() {
        return productoRepository.findByDisponibleTrue().stream().map(this::toResponse).toList();
    }

    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductoResponse obtener(Long id) {
        return toResponse(buscarPorId(id));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarPorId(id);
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        return toResponse(producto);
    }

    @Transactional
    public void cambiarDisponibilidad(Long id, boolean disponible) {
        Producto producto = buscarPorId(id);
        producto.setDisponible(disponible);
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepository.delete(buscarPorId(id));
    }

    private Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }


    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getDisponible());
    }
}