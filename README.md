# Petistaan-PetMS

Pet statistics microservice for the [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS)
pet management system. Exposes a single aggregation endpoint that breaks down the pet
population by category (Domestic / Wild), gender, and species — in one query, no N+1.

> **To run the full system** (all services + MySQL + Eureka + Config Server), see the
> [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) hub repo.
> The `docker-compose.yml` lives there.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Running Locally](#running-locally)
- [Branch Strategy](#branch-strategy)
- [Part of Petistaan-MS](#part-of-petistaan-ms)

---

## Overview

PetMS is a standalone Spring Boot microservice responsible for:

- Querying the pet population across the JOINED inheritance hierarchy (`pet_table`,
  `domestic_pet_table`, `wild_pet_table`)
- Aggregating counts grouped by category (DOMESTIC / WILD), gender (M / F), and
  pet type (Bird, Cat, Dog, Fish, Rabbit) in a **single JPQL query**
- Returning a deeply nested statistics DTO — no post-processing loops, no N+1

This service is read-only and stateless. It shares the `petistaan` MySQL database
with OwnerMS but owns no write operations.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Persistence | Spring Data JPA · Hibernate · MySQL |
| Build | Maven 3.9.x (Maven Wrapper) |
| Boilerplate reduction | Lombok |

---

## API Reference

Base URL: `http://localhost:8082`

| Method | Endpoint | Response | Description |
|---|---|---|---|
| `GET` | `/pets/stats` | `200 PetStatisticsDTO` | Full pet population breakdown |

### Sample response — GET /pets/stats

```json
{
  "total": 73,
  "category": {
    "DOMESTIC": {
      "total": 37,
      "gender": {
        "M": {
          "total": 23,
          "type": {
            "Bird": 5,
            "Cat": 4,
            "Dog": 5,
            "Fish": 4,
            "Rabbit": 5
          }
        },
        "F": {
          "total": 14,
          "type": {
            "Bird": 3,
            "Cat": 3,
            "Dog": 3,
            "Fish": 2,
            "Rabbit": 3
          }
        }
      }
    },
    "WILD": {
      "total": 36,
      "gender": {
        "M": {
          "total": 21,
          "type": {
            "Bird": 3,
            "Cat": 7,
            "Dog": 3,
            "Fish": 3,
            "Rabbit": 5
          }
        },
        "F": {
          "total": 15,
          "type": {
            "Bird": 6,
            "Cat": 2,
            "Dog": 1,
            "Fish": 2,
            "Rabbit": 4
          }
        }
      }
    }
  }
}
```

### Error response shape

```json
{
  "message": "Request method 'POST' is not supported",
  "httpStatus": "METHOD_NOT_ALLOWED",
  "value": 405,
  "now": "2025-09-01T14:32:00"
}
```

---

## Project Structure

```
src/main/java/com/abhishek/
├── controller/
│   ├── advice/
│   │   └── ExceptionControllerHandler.java   # 405 / 500 global handler
│   └── PetController.java                    # GET /pets/stats
├── dto/
│   ├── PetStatisticsDTO.java                 # Top-level: total + category map
│   ├── PetCategoryStatisticsDTO.java         # Per-category: total + gender map
│   ├── PetGenderStatisticsDTO.java           # Per-gender: total + EnumMap<PetType, Long>
│   └── ErrorDTO.java                         # Uniform error body (record)
├── entity/
│   ├── Base.java                             # @MappedSuperclass — auto-increment id
│   ├── Pet.java                              # Abstract — JOINED inheritance on pet_table
│   ├── DomesticPet.java                      # Final leaf — domestic_pet_table
│   └── WildPet.java                          # Final leaf — wild_pet_table
├── enums/
│   ├── Gender.java                           # M, F
│   └── PetType.java                          # Bird, Cat, Dog, Fish, Rabbit
├── repository/
│   └── PetRepository.java                    # Single GROUP BY JPQL with TYPE() discriminator
├── service/
│   ├── impl/
│   │   └── PetServiceImpl.java               # Flat Object[] → nested DTO transformation
│   └── PetService.java
└── PetistaanPetMsApplication.java            # @SpringBootApplication entry point
```

---

## Configuration

All sensitive values are externalized. Copy `.env.example` to `.env` and fill in your values:

```dotenv
MY_SQL_URL=jdbc:mysql://localhost:3306/petistaan
MY_SQL_USERNAME=root
MY_SQL_PASSWORD=yourpassword
```

`application.properties` resolves these at runtime via `${MY_SQL_URL}` etc. The `.env`
file is gitignored and never committed.

Key properties:

| Property | Value |
|---|---|
| `server.port` | `8082` |
| `spring.jpa.hibernate.ddl-auto` | `update` |
| `spring.jpa.show-sql` | `true` |

`database.sql` in `src/main/resources/` is a **DDL reference only** — the schema is
managed by Hibernate at startup.

---

## Running Locally

**Prerequisites:** Java 25, MySQL running and accessible.

> To spin up the full environment in one command, use the `docker-compose.yml` in the
> [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) hub repo.

To run this service in isolation:

```bash
# 1. Clone the repo
git clone https://github.com/AbhishekSharma-99/Petistaan-PetMS.git
cd Petistaan-PetMS

# 2. Set up environment
cp .env.example .env
# Edit .env with your MySQL credentials

# 3. Run
./mvnw spring-boot:run
```

Service starts on `http://localhost:8082`.

---

## Branch Strategy

```
main  ●──────────────────────────────────────────● (merge via PR)
       \                                         /
dev     ●──●──●──●──●──●──●──●──●──●──●──●─────
```

- `main` — stable, production-ready commits only; no direct pushes
- `dev` — integration branch; all feature work merges here first
- `feature/*` — short-lived branches off `dev` for individual concerns

---

## Part of Petistaan-MS

Petistaan-PetMS is one service in the broader Petistaan microservices ecosystem:

| Service | Port | Responsibility |
|---|---|---|
| [Petistaan-EurekaServer](https://github.com/AbhishekSharma-99/Petistaan-EurekaServer) | 8761 | Service discovery |
| [Petistaan-ConfigServer](https://github.com/AbhishekSharma-99/Petistaan-ConfigServer) | 8888 | Centralized config |
| [Petistaan-APIGateway](https://github.com/AbhishekSharma-99/Petistaan-APIGateway) | 8080 | Single entry point |
| [Petistaan-OwnerMS](https://github.com/AbhishekSharma-99/Petistaan-OwnerMS) | 8081 | Owner + pet management |
| **Petistaan-PetMS** | **8082** | **Pet statistics** |
| [Petistaan-MailMS](https://github.com/AbhishekSharma-99/Petistaan-MailMS) | 8083 | Email dispatch |

See [Petistaan-MS](https://github.com/AbhishekSharma-99/Petistaan-MS) for system
architecture, build order, and Docker Compose setup.
