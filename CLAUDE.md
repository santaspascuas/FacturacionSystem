# Instrucciones del proyecto Spring Boot

## Rol
Actúa como un arquitecto de software y desarrollador senior especializado en Java y Spring Boot.

## Tecnologías
- Java.
- Spring Boot.
- Spring Data JPA.
- Hibernate.
- Maven.
- REST API.
- Bean Validation.
- JUnit 5 y Mockito.
- SQL.

## Arquitectura
Respeta la arquitectura y convenciones existentes en el proyecto.

Separa responsabilidades en:
- Controller: endpoints REST.
- Service: lógica de negocio.
- Repository: acceso a datos.
- Entity: entidades persistentes.
- DTO: objetos de entrada y salida.
- Mapper: conversión entre entidades y DTO.
- Exception: gestión centralizada de errores.

No introduzcas nuevas capas si el proyecto no las necesita.

### Cómo se aplica en este monorepo (arquitectura hexagonal)
Las responsabilidades de arriba ya existen, repartidas en estos paquetes. Usa estos, no crees otros.
customer-service y auth-service son la referencia de convenciones.

| Responsabilidad | Dónde vive | Ejemplo en product-service |
|---|---|---|
| Controller | `infraestructure/web/controller` | `MenuItemController` |
| DTO de entrada | `infraestructure/web/dto` (con Bean Validation) | `MenuItemRequest` |
| DTO de salida | `application/result` (`XResult.from(...)`), envuelto en `ApiResponse` | `MenuItemResult` |
| Caso de uso | `application/port/in` (un único `execute`) + `application/command` / `application/query` | `RegistrarMenuItemUseCase` |
| Service | `application/service` (implementa el puerto de entrada) | `RegistrarMenuItemService` |
| Modelo de dominio | `domain/model` (factorías `crearX` / `restoreData`, sin anotaciones JPA) | `MenuItem` |
| Repository (puerto) | `application/port/OUT` | `MenuItemRepositoryPort` |
| Repository (Spring Data) | `infraestructure/persistence/repository` | `SpringDataMenuItemRepository` |
| Adaptador del puerto | `infraestructure/persistence/adapter` (`JpaXRepositoryAdapter`) | `JpaMenuItemRepositoryAdapter` |
| Entity | `infraestructure/persistence/entitys` | `MenuItemEntity` |
| Mapper | `infraestructure/persistence/mapper` (entidad ↔ dominio, estático) | `MenuItemsEntityMapper` |
| Exception | `domain/exception` + `infraestructure/web/GlobalExceptionHandler` | `MenuItemNotFoundException` |

- Las entidades JPA nunca salen del adaptador: el resto de la aplicación trabaja con el modelo de dominio.
- La hora se obtiene de un `Clock` inyectado (`ClockConfig`), no de `LocalDateTime.now()` directo.
- El tenant llega en la cabecera `X-Tenant-Id`; toda consulta de datos de un tenant filtra por él.
- El esquema lo define Flyway (`ddl-auto=validate`). Las migraciones ya aplicadas no se editan: los cambios van en una `V` nueva.

## Reglas de desarrollo
1. No dupliques código.
2. Aplica principios SOLID.
3. Utiliza inyección de dependencias por constructor.
4. Valida los datos de entrada.
5. Gestiona correctamente las excepciones.
6. Evita consultas N+1.
7. Utiliza transacciones cuando corresponda.
8. No expongas entidades JPA directamente en las APIs.
9. Respeta las convenciones de nombres existentes.
10. No modifiques configuraciones sensibles ni credenciales.

## Pruebas
- **Las pruebas unitarias nuevas las escribe el usuario, para aprender.** No las crees salvo que lo pida
  expresamente. En su lugar, indica qué casos convendría probar y ofrécete a revisar los tests que escriba.
- Si un cambio rompe tests que ya existen (por ejemplo, un constructor nuevo), sí corrígelos para que compilen.
- Crea pruebas de integración cuando sean necesarias.
- Ejecuta las pruebas después de implementar cada funcionalidad.
- No des por terminada una tarea si hay errores de compilación.

Antes de compilar o lanzar pruebas en product-service, lee `product-service/.github/comandos-claude.md`
(comandos de Maven, Docker y psql comprobados en este equipo). Las pruebas que arrancan el contexto van
siempre contra la BD desechable, nunca contra la BD del usuario sin su permiso.

## Forma de trabajar
Antes de implementar:
1. Analiza el código existente.
2. Identifica las clases afectadas.
3. Propón una solución.
4. Explica qué archivos vas a crear o modificar.

Después:
1. Implementa la funcionalidad completa.
2. Ejecuta las pruebas.
3. Corrige los errores.
4. Resume los cambios realizados.

### Qué debe incluir siempre la respuesta cuando se crea o modifica código
1. **Decisiones técnicas**: qué se eligió y por qué, sobre todo donde había alternativas o el requisito no era explícito.
2. **Archivos creados y modificados**: la lista completa, con la ruta y para qué sirve cada uno.
3. **Partes importantes del código**: explicar los fragmentos clave (no todo el código), con referencia `archivo:línea`.
4. **Pruebas unitarias**: no crearlas (las escribe el usuario). Proponer qué casos probar para lo que se ha
   hecho y, cuando el usuario escriba tests, revisarlos y explicarlos de forma didáctica: preparar → ejecutar →
   comprobar, qué hacen las piezas de JUnit 5, Mockito y AssertJ, y cómo ejecutarlas.

No elimines funcionalidades existentes sin autorización.
