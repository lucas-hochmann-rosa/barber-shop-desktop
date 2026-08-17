# 💈 Barbershop

<p align="center">
  <a href="https://github.com/lucas-hochmann-rosa/barber-shop-suite">
    <img src="https://img.shields.io/badge/GitHub-barber--shop--suite-181717?style=for-the-badge&logo=github">
  </a>
  <a href="https://www.linkedin.com/in/lucas-hochmann-rosa">
    <img src="https://img.shields.io/badge/LinkedIn-Lucas_Hochmann_Rosa-0A66C2?style=for-the-badge&logo=linkedin">
  </a>
  <a href="#-tech-stack">
    <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  </a>
  <a href="#-tech-stack">
    <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  </a>
  <a href="#-tech-stack">
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center"><a href="README.md">🇧🇷 Português</a> · 🇺🇸 English</p>

> Barbershop operational management system structured as a four-module monorepo: a shared business rules core in Java (`core`), a desktop application in Java Swing (`desktop`), a modern web interface in HTML, CSS and JS (`web`), and a Spring Boot REST API back-end (`api`).

---

## ⚡ Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite

# 2. Spin up MySQL via Docker
docker compose up -d

# 3. Compile and run all automated tests
mvn clean test

# 4. Run Spring Boot REST API
mvn spring-boot:run -pl api
# or: java -jar api/target/barber-shop-api-1.0-SNAPSHOT.jar (port 8080)

# 5. Run the Desktop application (Java Swing)
mvn package -pl desktop
java -jar desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar

# 6. Run the Web Front-end
npx serve web      # access http://localhost:3000 (demo login: lucas / 1234)
```

---

## 📌 Overview

**Barbershop** is a complete barbershop management system (appointments, staff, services, history, and revenue), built on a modular architecture:

- **Shared Core (`core`)**: Centralizes domain entities, JDBC persistence, idempotent migrations, and core business rules (such as RF11 visual classification and RF10 real time-overlap conflict validation), decoupled from any visual UI.
- **Desktop Application (`desktop`)**: Built with Java Swing (FlatLaf Look & Feel) featuring automatic bootstrap, services/barbers CRUD, real-time schedule management, reports dashboard, and an operational smoke test (`VerificacaoSistema`).
- **Web Version (`web`)**: Modern static front-end in HTML5, CSS3, and JavaScript, featuring an **interactive daily schedule timeline** and REST client integration (`web/js/api.js`).
- **Web REST Back-end (`api`)**: Java Web application with Spring Boot 3.2.5 REST (Stage 9), exposing JSON endpoints for authentication, barbershop management, catalog, scheduling, history, and analytics.

---

## 🏗️ Architecture

```text
barber-shop-suite/
├── pom.xml                             # Parent POM with centralized dependency management
├── docker-compose.yml                  # Pre-configured MySQL 8 container
├── README.md / README.en.md
├── LICENSE
├── docs/                               # Technical documentation and delivery records
│
├── core/                               # [MODULE 1] Shared business rules core (Java)
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/com/barbershop/
│       │   ├── model/                  # Domain POJOs
│       │   ├── dao/                    # JDBC/MySQL access layer (Repository Pattern)
│       │   ├── service/                # Business services (Agenda, Auth, Catalogo, Classificador, Relatorio)
│       │   └── util/                   # Security hashing and date utilities
│       └── test/java/                  # 52 unit tests with in-memory fake repositories
│
├── desktop/                            # [MODULE 2] Desktop Java Swing interface (FlatLaf)
│   ├── pom.xml
│   └── src/main/java/br/com/barbershop/
│       ├── app/                        # Main entry point, ServiceFactory, and SystemVerification
│       └── ui/                         # Swing views and controllers
│
├── web/                                # [MODULE 3] Front-end Web (HTML5, CSS3, JavaScript)
│   ├── index.html, agenda.html, agendamento.html, barbearia.html, historico.html, relatorios.html
│   ├── verificacao-classificacao.html  # Visual runner for RF11 parity verification
│   ├── css/                            # Modular styles
│   └── js/                             # UI logic, client REST (api.js), and demo data
│
└── api/                                # [MODULE 4] Spring Boot 3.2.5 REST API (Stage 9)
    ├── pom.xml
    └── src/
        ├── main/java/br/com/barbershop/api/
        │   ├── Application.java
        │   ├── config/                 # ServiceConfig and WebMvcConfig (CORS/Static)
        │   ├── controller/             # REST Controllers (Auth, Barbearia, Catalogo, Agenda, Historico, Relatorios)
        │   └── dto/                    # Data Transfer Objects
        └── test/java/                  # 14 MockMvc integration tests
```

---

## 🛠️ Tech Stack

- **Platform:** Java 17 · Spring Boot 3.2.5 · HTML5 / CSS3 / Vanilla JavaScript
- **Build System:** Apache Maven (multi-module monorepo)
- **Desktop UI:** Java Swing (FlatLaf `3.4.1`)
- **Web Back-end:** Spring Boot 3.2.5 REST (Spring MVC, Jackson JSR-310)
- **Database:** MySQL 8 + JDBC (`mysql-connector-j 8.3.0`), HikariCP `5.1.0`
- **Testing:** JUnit 5 (Jupiter) & Spring MockMvc (66 automated tests)
- **Containerization:** Docker & Docker Compose

---

## 🧪 Automated Testing

```bash
mvn clean test
```

Executes 66 automated tests:
- 52 unit tests across `core` services and domain models.
- 14 integration and controller tests across `api` REST endpoints.

---

## 👨‍💻 Author

**Lucas Hochmann Rosa**

- Repository: <https://github.com/lucas-hochmann-rosa/barber-shop-suite>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 License

Distributed under the MIT License. See [LICENSE](./LICENSE) for details.
