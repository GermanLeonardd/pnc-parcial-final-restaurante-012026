package com.uca.pncparcialfinalrestaurante.config;

import com.uca.pncparcialfinalrestaurante.model.*;
import com.uca.pncparcialfinalrestaurante.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RestauranteRepository restauranteRepository;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return; // ya hay datos, no volver a sembrar
        }

        // --- Restaurantes ---
        Restaurante sucursalCentro = restauranteRepository.save(Restaurante.builder()
                .nombre("Sucursal Centro")
                .direccion("Av. Principal 123")
                .ciudad("San Salvador")
                .telefono("2222-1111")
                .build());

        Restaurante sucursalNorte = restauranteRepository.save(Restaurante.builder()
                .nombre("Sucursal Norte")
                .direccion("Calle Norte 456")
                .ciudad("San Salvador")
                .telefono("2222-2222")
                .build());

        // --- Mesas ---
        mesaRepository.save(Mesa.builder().numero(1).capacidad(4).estado(EstadoMesa.DISPONIBLE).restaurante(sucursalCentro).build());
        mesaRepository.save(Mesa.builder().numero(2).capacidad(2).estado(EstadoMesa.DISPONIBLE).restaurante(sucursalCentro).build());
        mesaRepository.save(Mesa.builder().numero(1).capacidad(6).estado(EstadoMesa.DISPONIBLE).restaurante(sucursalNorte).build());

        // --- Productos ---
        productoRepository.save(Producto.builder().nombre("Pupusa Revuelta").descripcion("Queso, frijol y chicharrón").precio(new BigDecimal("1.00")).disponible(true).build());
        productoRepository.save(Producto.builder().nombre("Gaseosa 500ml").descripcion("Bebida carbonatada").precio(new BigDecimal("1.50")).disponible(true).build());
        productoRepository.save(Producto.builder().nombre("Yuca frita").descripcion("Con curtido y salsa").precio(new BigDecimal("3.50")).disponible(true).build());

        // --- Usuarios ---
        usuarioRepository.save(Usuario.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin123*"))
                .nombreCompleto("Administrador General")
                .rol(Rol.ADMINISTRADOR)
                .restaurante(null)
                .activo(true)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("encargado.centro")
                .password(passwordEncoder.encode("Encargado123*"))
                .nombreCompleto("Encargado Sucursal Centro")
                .rol(Rol.ENCARGADO_TURNO)
                .restaurante(sucursalCentro)
                .activo(true)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("encargado.norte")
                .password(passwordEncoder.encode("Encargado123*"))
                .nombreCompleto("Encargado Sucursal Norte")
                .rol(Rol.ENCARGADO_TURNO)
                .restaurante(sucursalNorte)
                .activo(true)
                .build());

        usuarioRepository.save(Usuario.builder()
                .username("cliente1")
                .password(passwordEncoder.encode("Cliente123*"))
                .nombreCompleto("Cliente de Prueba")
                .rol(Rol.CLIENTE)
                .restaurante(null)
                .activo(true)
                .build());

        System.out.println("✅ Datos semilla cargados: 2 restaurantes, 3 mesas, 3 productos, 4 usuarios.");
    }
}