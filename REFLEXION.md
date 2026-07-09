# Reflexión — Sistema de Pedidos de Restaurante

## 1. ¿Qué partes generó bien la IA sin necesidad de corrección?

La IA generó correctamente desde el primer intento la mayor parte de la capa de datos y DTOs: las entidades JPA con sus relaciones (`@ManyToOne`, `@OneToMany`), los repositorios con métodos de consulta derivados (como `findByMesa_Restaurante_Id`), y los DTOs con `record` y validaciones de `jakarta.validation`. También el diseño general de la arquitectura N-capas (controller → service → repository) y la separación de responsabilidades se mantuvo consistente en todas las etapas sin necesidad de ajustes.

## 2. ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en temas de seguridad?

El error más relevante en temas de seguridad fue que la IA propuso inicialmente `authenticationProvider.setUserDetailsService(...)`, un patrón de configuración de Spring Security ya no compatible con la versión incluida en Spring Boot 4.1.0, donde el `UserDetailsService` debe inyectarse por constructor. Esto no representa un riesgo de seguridad en sí (era un error de compilación, no una vulnerabilidad), pero evidencia que la IA puede generar código basado en versiones anteriores de una librería si no se le da contexto explícito de la versión exacta del proyecto.

Otro punto de atención (no un error, pero una decisión que se validó manualmente) fue asegurar que la comparación de `restauranteId` en la regla de autorización por sucursal manejara correctamente el caso en que el usuario no tuviera sucursal asignada (`null`), para evitar que una comparación mal hecha permitiera acceso indebido en vez de lanzar una excepción.

## 3. ¿Cómo detectaron esos errores y cómo los corrigieron?

Los errores de compilación se detectaron directamente en el IDE (IntelliJ), que marcaba los métodos como no resueltos o con tipos incompatibles. En el caso de `DaoAuthenticationProvider`, se investigó la documentación oficial y el historial de cambios de Spring Security para confirmar que el método había sido removido intencionalmente en favor del constructor, y se corrigió el bean acorde a la nueva API. En el caso del método mal ubicado (`obtenerConValidacion` pegado en `ProductoService` en vez de `PedidoService`), el propio compilador señaló el conflicto de tipos (`Required type: Pedido, Provided: Producto`), lo que permitió ubicar y mover el código al archivo correcto.

## 4. ¿Cómo explicarían el mecanismo de autorización por sucursal a un compañero?

Le diría que la autorización por rol sola (`@PreAuthorize("hasRole('ENCARGADO_TURNO')")`) no es suficiente en este sistema, porque todos los encargados comparten el mismo rol pero pertenecen a sucursales distintas. Por eso, además de verificar el rol, cada usuario `Usuario` tiene un campo `restaurante` que indica a qué sucursal pertenece. Cuando un ENCARGADO_TURNO intenta confirmar, modificar o cancelar un pedido, el sistema no se detiene en "¿tiene el rol correcto?", sino que además compara el `restauranteId` del usuario autenticado contra el `restauranteId` de la sucursal donde está la mesa de ese pedido (se obtiene navegando `pedido → mesa → restaurante → id`). Si no coinciden, se lanza una excepción `AccesoDenegadoSucursalException` que se traduce en una respuesta HTTP 403 (Forbidden). El ADMINISTRADOR está exento de esta validación porque tiene acceso total, y el CLIENTE tiene su propia regla equivalente: solo puede operar sobre pedidos donde él mismo sea el cliente asociado.