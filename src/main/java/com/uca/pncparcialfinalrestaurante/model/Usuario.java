package com.uca.pncparcialfinalrestaurante.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombreCompleto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    /**
     * Sucursal del usuario. Obligatorio para ENCARGADO_TURNO
     * (clave para la regla de negocio: autorización por atributo - Opción B).
     * Null para ADMINISTRADOR y CLIENTE.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "creado_en")
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();
}