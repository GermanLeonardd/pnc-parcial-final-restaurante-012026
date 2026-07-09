package com.uca.pncparcialfinalrestaurante.repository;

import com.uca.pncparcialfinalrestaurante.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}