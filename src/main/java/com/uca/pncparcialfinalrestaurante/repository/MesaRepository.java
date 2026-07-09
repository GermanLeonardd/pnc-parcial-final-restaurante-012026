package com.uca.pncparcialfinalrestaurante.repository;

import com.uca.pncparcialfinalrestaurante.model.EstadoMesa;
import com.uca.pncparcialfinalrestaurante.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesaRepository extends JpaRepository<Mesa, Long> {

    List<Mesa> findByRestauranteId(Long restauranteId);

    List<Mesa> findByRestauranteIdAndEstado(Long restauranteId, EstadoMesa estado);
}