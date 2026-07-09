# Bitácora de Prompts — Sistema de Pedidos de Restaurante

> Herramienta de IA usada en todo el proyecto: **Claude (Anthropic)**

---


**Prompt:**
"Actúa como arquitecto de software Java. Necesito la configuración inicial de un proyecto Spring Boot con arquitectura N-capas (presentación, lógica de negocio, acceso a datos) para un sistema de pedidos de restaurante con múltiples sucursales. Necesito el build.gradle con las dependencias necesarias (Web, Security, JPA, PostgreSQL, JWT, Lombok, Validation), el application.yaml con configuración de base de datos y JWT, y las entidades JPA: Usuario, Restaurante, Mesa, Producto, Pedido y DetallePedido."

**Qué generó la IA:**
Estructura de `build.gradle` con dependencias, `application.yaml` con configuración de PostgreSQL y JWT, y las 9 entidades JPA del modelo (Usuario, Rol, Restaurante, Mesa, EstadoMesa, EstadoPedido, Producto, Pedido, DetallePedido).

**Qué se corrigió manualmente y por qué:**
La propuesta inicial usaba Maven (`pom.xml`) y el paquete genérico `com.restaurante.pedidos`. Se corrigió para adaptarla al repositorio real del proyecto, que usa **Gradle**, **Spring Boot 4.1.0**, **Java 21** y el paquete `com.uca.pncparcialfinalrestaurante` generado por Spring Initializr. Se le pidió a la IA revisar el repositorio de GitHub antes de regenerar el código para asegurar consistencia.

---


**Prompt:**
"Sobre el proyecto con las entidades ya creadas, necesito los repositorios JPA (JpaRepository) para cada entidad, con métodos de consulta personalizados (buscar usuario por username, mesas por restaurante, pedidos por cliente y por sucursal), y los DTOs de request/response para autenticación, Usuario, Restaurante, Mesa, Producto y Pedido usando record de Java y validaciones con jakarta.validation."

**Qué generó la IA:**
6 interfaces `JpaRepository`, incluyendo el método clave `findByMesa_Restaurante_Id` en `PedidoRepository` (usado luego para la regla de autorización por sucursal), y 15 DTOs (records) organizados en `dto/request` y `dto/response`.

**Qué se corrigió manualmente y por qué:**
Ninguna corrección funcional; se validó que los DTOs respetaran el paquete base del proyecto real.

---


**Prompt:**
"Necesito implementar autenticación con JWT usando jjwt 0.12.6: un JwtService que genere y valide Access Tokens (15 min) y Refresh Tokens (7 días), una entidad RefreshToken persistida en base de datos, un UserDetailsServiceImpl que adapte Usuario a UserDetails, un JwtAuthFilter que valide el token del header Authorization, y un SecurityConfig con sesión stateless y rutas públicas /api/auth/**."

**Qué generó la IA:**
`JwtService`, entidad `RefreshToken` + repositorio, `UserDetailsImpl`, `UserDetailsServiceImpl`, `JwtAuthFilter` y `SecurityConfig`.

**Qué se corrigió manualmente y por qué:**
El método `authenticationProvider.setUserDetailsService(userDetailsService)` no compilaba. Se investigó y se determinó que en la versión de Spring Security incluida en Spring Boot 4.1.0, `DaoAuthenticationProvider` **eliminó el setter** y ahora exige pasar el `UserDetailsService` por **constructor** (`new DaoAuthenticationProvider(userDetailsService)`). Se corrigió el bean manualmente.

---


**Prompt:**
"Necesito un AuthService que autentique con AuthenticationManager, genere Access y Refresh Token y persista el Refresh Token; un método de renovación que valide que el refresh token exista, no esté revocado ni expirado y rote el token; manejo de excepciones para credenciales inválidas; y un AuthController con /api/auth/login y /api/auth/refresh."

**Qué generó la IA:**
`AuthService` (login + refresh con rotación de refresh token), `AuthController`, excepciones personalizadas (`ResourceNotFoundException`, `InvalidRefreshTokenException`, `AccesoDenegadoSucursalException`) y `GlobalExceptionHandler`.

**Qué se corrigió manualmente y por qué:**
Se detectó que la clase `AccesoDenegadoSucursalException` había quedado sin su constructor con parámetro `String message` en el archivo real (solo la declaración vacía de la clase), lo que provocaba el error "Expected 0 arguments but found 1" al usarla en `MesaService` y `PedidoService`. Se corrigió agregando el constructor faltante.

---

**Prompt:**
"Necesito la capa de lógica de negocio para Restaurante, Mesa y Pedido. El punto crítico es implementar la regla de negocio de autorización por atributo: un ENCARGADO_TURNO solo puede confirmar, modificar o cancelar pedidos y mesas de su propia sucursal, comparando el restauranteId del usuario autenticado contra el restauranteId del recurso, lanzando AccesoDenegadoSucursalException si no coinciden. También necesito un CommandLineRunner con datos de prueba (2 sucursales, mesas, productos, y un usuario por rol)."

**Qué generó la IA:**
`RestauranteService`, `MesaService`, `PedidoService` (con el método `validarAccesoSegunRol`, núcleo de la regla de negocio), `UsuarioService` y `DataSeeder` con datos y credenciales de prueba.

**Qué se corrigió manualmente y por qué:**
Ninguna corrección funcional a la lógica en sí; se verificó manualmente que la comparación de `restauranteId` cubriera el caso `null` (usuario sin sucursal asignada) antes de comparar, para evitar `NullPointerException`.

---

**Prompt:**
"Necesito completar la capa de presentación: habilitar @EnableMethodSecurity, crear ProductoService/ProductoController, y los controllers de Restaurante, Mesa, Pedido y Usuario usando @PreAuthorize con hasRole(...). En PedidoController, el endpoint de consulta individual debe aplicar visibilidad diferenciada por rol reutilizando la validación del PedidoService, y exponer el usuario autenticado con @AuthenticationPrincipal."

**Qué generó la IA:**
`ProductoService`, `ProductoController`, `RestauranteController`, `MesaController`, `PedidoController`, `UsuarioController`, y el método `obtenerConValidacion` para agregar a `PedidoService`.

**Qué se corrigió manualmente y por qué:**
Al copiar el código, el método `obtenerConValidacion` (que pertenece a `PedidoService`) se pegó por error dentro de `ProductoService.java`, generando un error de tipos (`Required type: Pedido, Provided: Producto`) porque `buscarPorId` en esa clase devuelve `Producto`. Se identificó el error por el mensaje del IDE y se corrigió moviendo el método al archivo correcto.

---

## Notas generales de uso de IA

- Toda la arquitectura fue generada iterativamente, por etapas, para poder revisar y validar cada capa antes de avanzar.
- Varios errores surgieron no por lógica incorrecta de la IA, sino por **desajustes de versión** (Spring Boot 4.1.0 / Spring Security reciente) y por **errores humanos al copiar/pegar** el código en el archivo equivocado — ambos se documentan como aprendizaje en `REFLEXION.md`.