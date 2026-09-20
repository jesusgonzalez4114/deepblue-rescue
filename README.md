# DeepBlue Rescue

Laboratorio práctico de persistencia con **Java 21**, **Spring Boot 4**, **Spring Data JPA**, **Hibernate**, **Flyway**, **PostgreSQL** y **Testcontainers**.

Jesus Gonzalez-2023214046

Anuar Hatum-2023214056

## Descripción

DeepBlue Rescue es la capa de persistencia de una plataforma para organizaciones dedicadas al rescate y rehabilitación de fauna marina. El sistema modela el recorrido completo de un animal rescatado: desde que un centro registra un caso, pasando por su expediente médico, hasta los tratamientos realizados por distintos especialistas durante su rehabilitación.

Este proyecto cubre exclusivamente la capa de persistencia (entidades JPA, repositories, migraciones Flyway y pruebas de integración). No incluye controllers, API REST, servicios, DTOs, seguridad ni frontend.

## Modelo de datos

```
RescueCenter
     |
     | 1:N
     v
RescueCase
     |
     | 1:1
     v
Animal
     |
     +──────── 1:1 ──────── MedicalRecord
     |
     +──────── 1:N ──────── Treatment
                                |
                                | N:1
                                v
                           Specialist
                                |
                                | N:M
                                v
                           Expertise
```

### Tablas

| Tabla | Tipo | Descripción |
|---|---|---|
| `rescue_centers` | Entidad | Centros de recuperación de fauna marina |
| `rescue_cases` | Entidad | Casos de rescate abiertos por un centro |
| `animals` | Entidad | Animal rescatado, asociado a un caso |
| `medical_records` | Entidad | Expediente médico de un animal |
| `specialists` | Entidad | Especialistas que atienden animales |
| `expertise` | Entidad | Catálogo de áreas de experiencia |
| `specialist_expertise` | Asociativa (N:M) | Relación entre especialistas y sus áreas de experiencia |
| `treatments` | Entidad | Tratamientos realizados a un animal por un especialista |

## Relaciones

- **RescueCenter 1:N RescueCase** — FK `rescue_center_id` en `rescue_cases`.
- **RescueCase 1:1 Animal** — FK `rescue_case_id` en `animals`, con `UNIQUE` para forzar el 1:1.
- **Animal 1:1 MedicalRecord** — FK `animal_id` en `medical_records`, con `UNIQUE` para forzar el 1:1.
- **Specialist N:M Expertise** — tabla intermedia `specialist_expertise` con PK compuesta `(specialist_id, expertise_id)`.
- **Animal 1:N Treatment** y **Specialist 1:N Treatment** — FKs `animal_id` y `specialist_id` en `treatments`.

## Requisitos previos

- Java 21
- Docker Desktop (o Docker Engine) corriendo — necesario para Testcontainers
- Maven (o usar el wrapper `mvnw` / `mvnw.cmd` incluido en el proyecto)



Todos los tests están en `PersistenceIntegrationTest`, y corren contra un contenedor real de PostgreSQL levantado automáticamente por Testcontainers — Docker debe estar corriendo antes de ejecutar este comando.

## Flyway

Flyway es el único responsable de crear y evolucionar el esquema de la base de datos. Se configura con `ddl-auto: validate` en vez de `create` o `update`, de modo que Hibernate únicamente **valida** que las entidades JPA coincidan con las tablas ya creadas por las migraciones, sin poder modificarlas.

Migraciones incluidas:

- `V1__create_schema.sql` — crea las 8 tablas, sus PK, FK, UNIQUE, CHECK e índices.
- `V2__insert_expertise_catalog.sql` — inserta el catálogo inicial de áreas de experiencia (Marine Reptiles, Marine Mammals, Marine Birds, Trauma, Rehabilitation, Toxicology).
- `V3__add_tracking_device_to_animal.sql` — agrega la columna opcional `tracking_device_code` a `animals`, con constraint `UNIQUE` (permite múltiples `NULL`, pero no valores repetidos).

## Testcontainers

Los tests de integración no usan H2 ni mocks: se ejecutan contra un contenedor real de PostgreSQL (`postgres:18-alpine`), levantado y destruido automáticamente por Testcontainers en cada corrida, gracias a `@Testcontainers` y `@ServiceConnection`. Esto permite comprobar constraints reales (UNIQUE, FK, CHECK) que un motor en memoria como H2 no siempre reproduce con la misma fidelidad que PostgreSQL.

## Query Methods implementados

| Repository | Método | Descripción |
|---|---|---|
| `RescueCenterRepository` | `findByCode(String code)` | Busca un centro por su código |
| `RescueCaseRepository` | `findByCaseCode(String caseCode)` | Busca un caso por su código |
| `RescueCaseRepository` | `findByStatusOrderByRescueDateAsc(RescueStatus status)` | Casos por estado, ordenados por fecha |
| `RescueCaseRepository` | `findByRescueCenterCode(String code)` | Casos de un centro, navegando la relación |
| `RescueCaseRepository` | `findByRescueDateAfterOrderByRescueDateDesc(LocalDate date)` | Casos posteriores a una fecha, del más reciente al más antiguo |
| `AnimalRepository` | `findByAnimalCode(String animalCode)` | Busca un animal por su código |
| `AnimalRepository` | `findByCommonNameContainingIgnoreCase(String commonName)` | Animales cuyo nombre común contiene un texto |
| `AnimalRepository` | `findByRescueCaseStatus(RescueStatus status)` | Animales cuyo caso tiene determinado estado |
| `AnimalRepository` | `findByRescueCaseRescueCenterCode(String centerCode)` | Animales de un centro, navegando dos relaciones |
| `ExpertiseRepository` | `findByNameIgnoreCase(String name)` | Busca una expertise ignorando mayúsculas |
| `TreatmentRepository` | `findByAnimalIdOrderByPerformedAtAsc(Long animalId)` | Tratamientos de un animal, en orden cronológico |

## Consultas JPQL implementadas (`@Query`)

| Repository | Método | Descripción |
|---|---|---|
| `SpecialistRepository` | `findActiveByExpertise(String expertiseName)` | Especialistas activos con determinada experiencia |
| `TreatmentRepository` | `findByPerformedAtBetween(LocalDateTime start, LocalDateTime end)` | Tratamientos realizados entre dos fechas |
| `TreatmentRepository` | `findByAnimalRescueCaseRescueCenterCode(String centerCode)` | Tratamientos de animales de un centro, navegando 3 entidades |
| `TreatmentRepository` | `findBySpecialistExpertise(String expertiseName)` | Tratamientos realizados por especialistas con determinada experiencia (N:M) |
| `AnimalRepository` | `findInRehabilitationTreatedByExpertise(RescueStatus status, String expertiseName)` | Animales en determinado estado, tratados por especialistas con determinada experiencia (reto sin guía) |

## Reglas del laboratorio respetadas

- `ddl-auto: validate` — Flyway crea el esquema, Hibernate solo valida.
- Sin H2 — todas las pruebas corren contra PostgreSQL real vía Testcontainers.
- Sin SQL nativo en los repositories — todas las consultas personalizadas usan JPQL.
- Sin Lombok `@Data` sobre las entidades.
