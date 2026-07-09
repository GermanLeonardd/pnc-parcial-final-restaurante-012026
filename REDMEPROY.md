# Sistema de Pedidos de Restaurante — Backend

API REST para la gestión de pedidos de una cadena de restaurantes con múltiples sucursales, desarrollada con **Spring Boot 4.1.0**, **Java 21**, **PostgreSQL** y autenticación **JWT**, siguiendo una arquitectura **N-Capas**.

## 🚀 Cómo levantar el proyecto

### Requisitos previos

- Java 21
- PostgreSQL corriendo localmente (o accesible por red)
- Gradle Wrapper (incluido en el proyecto, no requiere instalación aparte)

### Configuración de la base de datos

Crear una base de datos en PostgreSQL:

```sql
CREATE DATABASE pedidos_db;
```

Por defecto, la aplicación se conecta usando estas variables de entorno (con valores por defecto si no se definen):

| Variable | Valor por defecto |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `pedidos_db` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `JWT_SECRET` | clave por defecto (cambiarla en producción) |
| `JWT_ACCESS_EXPIRATION` | `900000` (15 minutos) |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7 días) |

Puedes sobreescribirlas como variables de entorno o editando `src/main/resources/application.yaml`.

### Pasos para ejecutar

1. Clonar el repositorio:
```bash
   git clone https://github.com/GermanLeonardd/pnc-parcial-final-restaurante-012026.git
   cd pnc-parcial-final-restaurante-012026
```

2. Ejecutar la aplicación con el wrapper de Gradle:
```bash
   ./gradlew bootRun
```
En Windows:
```bash
   gradlew.bat bootRun
```

3. La API quedará disponible en:

http://localhost:8080

Al iniciar por primera vez, un `DataSeeder` carga automáticamente datos de prueba (sucursales, mesas, productos y un usuario por rol) si la base de datos está vacía.

### Usuarios de prueba (cargados automáticamente al iniciar)

| Usuario | Password | Rol | Sucursal |
|---|---|---|---|
| `admin` | `Admin123*` | ADMINISTRADOR | — |
| `encargado.centro` | `Encargado123*` | ENCARGADO_TURNO | Sucursal Centro |
| `encargado.norte` | `Encargado123*` | ENCARGADO_TURNO | Sucursal Norte |
| `cliente1` | `Cliente123*` | CLIENTE | — |

### Autenticación

- `POST /api/auth/login` → recibe `username` y `password`, devuelve un `accessToken` (expira en 15 min) y un `refreshToken` (expira en 7 días).
- `POST /api/auth/refresh` → renueva el `accessToken` a partir de un `refreshToken` válido. El refresh token usado queda revocado y se emite uno nuevo (rotación de tokens).

Las peticiones a endpoints protegidos deben incluir el header:

## 🏗️ Arquitectura (N-Capas)

El proyecto sigue una separación estricta en tres capas principales:

- **Presentación (`controller`)**: expone los endpoints REST, valida las peticiones de entrada (`@Valid`) y aplica autorización por rol mediante `@PreAuthorize`. No contiene lógica de negocio.
- **Lógica de Negocio (`service`)**: contiene las reglas del dominio (creación de pedidos, cálculo de totales, emisión y validación de tokens, y la validación de autorización por sucursal). Es la única capa que decide qué operaciones están permitidas más allá del rol básico.
- **Acceso a Datos (`repository`)**: interfaces `JpaRepository` que abstraen la persistencia en PostgreSQL, sin lógica de negocio.

Capas de soporte transversal:
- `security`: generación y validación de JWT, adaptación de `Usuario` a `UserDetails`, filtro de autenticación por token.
- `dto`: objetos de transferencia (request/response) que evitan exponer las entidades JPA directamente en la API.
- `model`: entidades JPA del dominio (Usuario, Restaurante, Mesa, Producto, Pedido, DetallePedido, RefreshToken).
- `exception`: excepciones de dominio y su manejo centralizado (`GlobalExceptionHandler`).
- `config`: configuración de seguridad (`SecurityConfig`) y carga de datos de prueba (`DataSeeder`).

## 🔐 Roles y autorización

| Rol | Permisos |
|---|---|
| **ADMINISTRADOR** | Acceso total: gestiona restaurantes, mesas, usuarios y pedidos de todas las sucursales. |
| **ENCARGADO_TURNO** | Gestiona pedidos y mesas, únicamente de la sucursal a la que pertenece. |
| **CLIENTE** | Solo puede crear, ver y cancelar sus propios pedidos. |

## ⚖️ Regla de negocio implementada: Autorización por atributo (sucursal)

La autorización por rol (`@PreAuthorize("hasRole(...)")`) **no es suficiente** para diferenciar entre encargados de distintas sucursales, ya que todos comparten el mismo rol `ENCARGADO_TURNO`.

Por eso, en la capa de lógica de negocio (`PedidoService` y `MesaService`), antes de permitir que un **Encargado de turno** confirme, modifique o cancele un pedido o una mesa, el sistema compara:

Si no coinciden, se lanza `AccesoDenegadoSucursalException`, que el `GlobalExceptionHandler` traduce en una respuesta **HTTP 403 Forbidden**.

- El rol **ADMINISTRADOR** está exento de esta validación (acceso total).
- El rol **CLIENTE** tiene una validación equivalente: solo puede operar sobre pedidos donde él es el cliente asociado (`pedido.cliente.id == usuarioAutenticado.id`).

Esta lógica vive exclusivamente en la capa de servicio, manteniendo los controllers limpios y respetando la separación de responsabilidades de la arquitectura N-Capas.