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
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center"><a href="README.md">🇧🇷 Português</a> · 🇺🇸 English</p>

> Barbershop operational management system structured as a three-module monorepo: a shared business rules core in Java (`barber-shop-core`), a complete desktop application in Java Swing with MySQL persistence (`barber-shop-desktop`), and a modern web interface in plain HTML, CSS and JavaScript (`barber-shop-web`) that shares the same business rules.

---

## ⚡ Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite

# 2. Run the Desktop application (requires Java 17+ and MySQL)
docker compose up -d          # spins up a pre-configured MySQL 8 instance
mvn clean package
java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar

# 3. Or run the Web version (independent front-end)
npx serve barber-shop-web      # access http://localhost:3000 (demo login: lucas / 1234)
```

---

## 📌 Overview

**Barbershop** is a comprehensive barbershop management system (appointments, staff, services, history, and revenue), built on a modular architecture:

- **Shared Core (`barber-shop-core`)**: Centralizes domain entities, JDBC persistence, migrations, and core business rules (such as RF11 visual classification and RF10 real time-overlap conflict validation), without any coupling to visual interfaces.
- **Desktop Application (`barber-shop-desktop`)**: Desktop application built with Java Swing (FlatLaf Look & Feel) featuring automatic bootstrap (initial setup vs. login), CRUD operations for services and barbers, real-time schedule management, reports dashboard, and an operational smoke test (`VerificacaoSistema`).
- **Web Version (`barber-shop-web`)**: Modern static front-end in HTML5, CSS3, and JavaScript (Stage 8), mirroring the barbershop operational workflows, highlighting the **interactive daily schedule timeline** and direct portability of core business rules.

---

## 🧠 Key Features

- Bootstrap flow:
  - if there's no barbershop in the database, opens `TelaCadastroInicial`;
  - if one already exists, opens `TelaLogin`.
- Automatic schema initialization (`DatabaseInitService`) on app startup.
- Idempotent migrations that preserve appointment history:
  - service and barber name snapshots;
  - removal of foreign keys on `agendamentos` so items can be deleted without losing historical records.
- Complete initial setup:
  - barbershop general data;
  - services (name, price, optional image);
  - barbers (name, optional image);
  - administrator credentials.
- Session and login with user and barbershop context.
- Daily Schedule / Home screen with:
  - interactive daily timeline (business hours rail with appointment markers and real-time needle);
  - service grid with new appointment shortcuts;
  - pending appointments table (`AGENDADO` and `EM_ATENDIMENTO`);
  - quick action buttons / context actions to edit, start, complete, and cancel.
- "My Barbershop" screen to maintain:
  - general information;
  - services (CRUD);
  - barbers (CRUD).
- Full appointment history with comprehensive status filters.
- Appointment creation and editing:
  - client, contact, date/time, service, barber, contact source, and status.
- Configurable duration per service for realistic conflict validation:
  - blocks appointments for the same barber when time intervals truly overlap (based on actual service duration), rather than a fixed window;
  - allows simultaneous appointments for different barbers;
  - allows back-to-back appointments (one ending exactly when the next begins).
- Configurable business hours per barbershop, validated during appointment scheduling.
- Client directory, auto-populated from appointments, with search capabilities.
- Cancellation reason capture upon cancellation.
- Direct shortcut to open the client's WhatsApp chat from the appointment.
- Visual classification of appointments by status and proximity (color-coded badges/rows per RF11).
- Search and filtering across appointment history and client directory.
- Reports dashboard: revenue by period, top services, and barber rankings.
- Photos stored as Base64 in the database - no dependency on specific local disk paths.
- Native application icon across all windows.
- File logging (`~/.barbershop/logs/`) and connection pooling (HikariCP).

---

## 🧭 Table of Contents

- [Module Architecture](#-module-architecture)
- [Execution Instructions by Module](#-execution-instructions-by-module)
- [Core Reuse and Parity (RF11)](#-core-reuse-and-parity-rf11)
- [Tech Stack](#-tech-stack)
- [Project Ground Rules](#-project-ground-rules)
- [Requirements](#-requirements)
- [Installation and Database](#-installation-and-database)
- [Environment Variables](#-environment-variables)
- [Desktop Deployment (Windows / Linux)](#-desktop-deployment-windows--linux)
- [Main Screens](#-main-screens)
- [Business Rules Implemented](#-business-rules-implemented)
- [Requirements Compliance](#-requirements-compliance-current-state)
- [Automated Tests](#-automated-tests)
- [System Verification (Smoke Test)](#-system-verification-smoke-test)
- [Screenshots](#-screenshots)
- [Roadmap](#-roadmap)
- [Disclaimer](#-disclaimer)
- [Author](#-author)
- [License](#-license)

---

## 🏗️ Module Architecture

The repository is organized as a **multi-module Maven monorepo** with three distinct modules:

```text
barber-shop-suite/
├── pom.xml                             # Parent Maven aggregator: centralized dependency versions
├── docker-compose.yml                  # Local MySQL 8 pre-configured with project defaults
├── README.md / README.en.md
├── LICENSE
├── docs/
│   ├── screenshots/                    # Application screenshots used across documentation
│   └── wireframes/                     # Low-fidelity pure SVG wireframes for desktop and web
│       ├── desktop/                    # Desktop Swing application wireframes
│       └── web/                        # Web interface wireframes
│
├── barber-shop-core/                    # [MODULE 1] Shared business rules core (Java)
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/com/barbershop/
│       │   ├── model/                  # Domain entities (POJOs: Agendamento, Barbearia, Servico, Barbeiro, Usuario)
│       │   ├── dao/                    # Data access layer (JDBC/MySQL), one class per entity
│       │   │   └── repository/         # Interfaces consumed by services (Repository Pattern)
│       │   ├── service/                # Business services (AgendaService, AuthService, CatalogoService, ClassificadorAgenda, RelatorioService)
│       │   └── util/                   # Pure utilities (salted PBKDF2 hashing, date parsing/formatting, image encoding)
│       ├── main/resources/
│       │   ├── config.properties       # JDBC MySQL database connection defaults
│       │   └── db/schema.sql           # Initial DDL schema, automatically executed on first start
│       └── test/java/br/com/barbershop/
│           ├── model/, util/           # Unit tests for domain logic, hashing, and dates
│           └── service/                # Service tests using in-memory fake repositories (e.g. ClassificadorAgendaTest)
│               └── fake/               # In-memory test doubles for repository interfaces
│
├── barber-shop-desktop/                 # [MODULE 2] Graphical desktop interface in Java Swing
│   ├── pom.xml                         # Builds shaded standalone executable JAR (maven-shade-plugin)
│   ├── nbactions.xml                   # NetBeans IDE execution and debug profiles
│   └── src/main/
│       ├── java/br/com/barbershop/
│       │   ├── app/
│       │   │   ├── Main.java             # Entry point: selects between initial setup and login
│       │   │   ├── FabricaDeServicos.java # Composition root: instantiates concrete DAOs and injects into services
│       │   │   └── VerificacaoSistema.java # End-to-end operational smoke test against real MySQL
│       │   ├── ui/                        # Swing UI views (NetBeans GUI Builder + FlatLaf)
│       │   │   ├── controller/            # Screen controllers (Home, Barbearia, Catalogo, Relatorios...)
│       │   │   └── support/                # UI helpers (icons, masks, custom table cell renderers)
│       └── resources/
│           ├── logback.xml                # Logging configuration (console + rotating file appender)
│           └── icon.ico                   # Application native icon
│
└── barber-shop-web/                     # [MODULE 3] Standalone Web Front-end (plain HTML, CSS, and JavaScript)
    ├── README.md                       # The web module's dedicated documentation
    ├── index.html                      # Login screen (RF02)
    ├── agenda.html                     # Main screen: daily timeline, summary cards, and pending table (RF08, RF11)
    ├── agendamento.html                # Appointment creation/edit with conflict detection (RF05, RF06, RF10)
    ├── barbearia.html                  # Barbershop profile, service catalog, and barber management (RF03, RF04)
    ├── historico.html                  # Appointment history with search and filtering (RF09)
    ├── relatorios.html                 # Reports: revenue, top services, and barber rankings (RF09)
    ├── verificacao-classificacao.html  # Visual evidence of RF11 parity against JUnit test cases
    ├── css/                            # Modular stylesheets (base, layout, componentes, paginas)
    ├── js/                             # UI logic, validations, ported core rules, and sample data
    └── img/                            # Custom SVGs (brand logo, avatars, service graphics)
```

---

## 🚀 Execution Instructions by Module

### 1. `barber-shop-core` Module (Core Rules)

This module has no graphical interface or standalone executable entry point - it is a library containing business logic and data access components.

To compile and run the core unit test suite:

```bash
mvn test -pl barber-shop-core
```

---

### 2. `barber-shop-desktop` Module (Desktop App)

Requires **JDK 17+** and a running **MySQL 8** instance.

#### Preliminary Step: Start MySQL

```bash
docker compose up -d
```

#### Option A: Run via Apache NetBeans (Recommended for academic evaluation)

1. Open the project root folder (`barber-shop-suite`) in NetBeans: **File** → **Open Project**.
2. NetBeans will detect the Maven monorepo and its submodules automatically.
3. Expand the **barber-shop-desktop** (`barber-shop-desktop`) project, right-click, and choose **Run** (the configured main class is `br.com.barbershop.app.Main`).
4. **Debug** and **Profile** actions work through the same menu.

#### Option B: Run via Command Line (Maven + JAR)

```bash
mvn clean package
java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
```

---

### 3. `barber-shop-web` Module (Web Front-end)

The web version is developed with **plain HTML5, CSS3, and JavaScript** - no heavy frameworks, no build steps, and zero external runtime dependencies.

#### How to run:

- **Option A (Direct in Browser):** Simply open `barber-shop-web/index.html` in any modern web browser.
- **Option B (Local HTTP Server - recommended):**
  ```bash
  # Using npx serve
  npx serve barber-shop-web

  # Or using Python 3
  cd barber-shop-web
  python -m http.server 8000
  ```
  Open <http://localhost:8000> (or the port shown in your terminal).

#### 🔑 Demo Credentials (Web):
- **Username:** `lucas`
- **Password:** `1234`

> [!NOTE]
> **Note on the Web Back-end (Stage 8):** In this stage, the web module runs with rich in-memory sample data (`barber-shop-web/js/dados.js`). Actions performed on screen (starting/completing appointments, new bookings, etc.) operate on local session state. Integration with a persistent back-end API (consuming `barber-shop-core` services) is scheduled for the subsequent project stage.

---

## 🔄 Core Reuse and Parity (RF11)

A core design principle of Barbershop is the consistency and portability of business rules across platforms:

- **Schedule Classification Rule (RF11):** Determines visual status for each appointment based on time proximity to a reference instant (`EM_ANDAMENTO`, `ATRASADO`, `IMINENTE` within 60 min, `PROXIMO` within 120 min, `DISTANTE` over 120 min, `CONCLUIDO`, and `CANCELADO`).
- **Java Implementation:** Class `br.com.barbershop.service.ClassificadorAgenda` in `barber-shop-core`, covered by the JUnit 5 test suite [`ClassificadorAgendaTest`](barber-shop-core/src/test/java/br/com/barbershop/service/ClassificadorAgendaTest.java).
- **JavaScript Port:** Ported to [`barber-shop-web/js/classificacao.js`](barber-shop-web/js/classificacao.js), maintaining identical constant names, precedence rules, and interval boundaries.
- **In-Browser Verification:** The page [`barber-shop-web/verificacao-classificacao.html`](barber-shop-web/verificacao-classificacao.html) runs all 12 JUnit test cases directly in the browser against the JS implementation, validating 100% behavioral parity.

---

## 🧰 Tech Stack

**Languages & Platforms:** Java 17 (Desktop/Core) · Plain HTML5 / CSS3 / Vanilla JavaScript (Web)

**Build & Monorepo:** Apache Maven multi-module structure with centralized dependency management in root `pom.xml`

**Desktop UI:** Java Swing (Look & Feel [FlatLaf](https://www.formdev.com/flatlaf/) `3.4.1`), screens built with NetBeans GUI Builder (`AbsoluteLayout`)

**Database:** MySQL 8 + JDBC Connector (`mysql-connector-j 8.3.0`), connection pooling with [HikariCP](https://github.com/brettwooldridge/HikariCP) `5.1.0`

**Logging:** SLF4J `2.0.16` + Logback `1.5.16` (console output and rotating file logging)

**Automated Testing:** JUnit 5 (Jupiter), service tests using in-memory fake repositories

**Local Environment:** Docker & Docker Compose for MySQL 8

---

## 📐 Project Ground Rules

- Identifiers (classes, methods, variables, database tables, and columns) remain in Portuguese - matching the domain vocabulary of a Brazilian barbershop.
- Code comments are reserved for non-obvious design decisions - the "why", not the "what".
- User interface copy (Swing screens and Web pages) is in Portuguese for end-user realism.
- No sensitive credentials are committed: `config.properties` ships with local development defaults, overridable via environment variables.

---

## ⚙️ Requirements

- JDK 17+ installed and available on `PATH`
- MySQL 8 active and accessible (locally or via Docker Compose)
- MySQL user with permissions to create and modify tables in the `barbershop` schema
- Maven 3.8+ (optional if using NetBeans IDE)
- Modern web browser for the web module

---

## 🔧 Installation and Database

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite
```

### Database

**Option 1 - Docker Compose (recommended for local development):**

```bash
docker compose up -d
```

Spins up a MySQL 8 container with the `barbershop` schema and passwordless `root` user.

**Option 2 - Local MySQL Server:**

```sql
CREATE DATABASE barbershop;
```

> In both options, all tables and schema migrations are automatically executed upon the first start of the desktop application (`barber-shop-core/src/main/resources/db/schema.sql`).

---

## 🔐 Environment Variables

Connection settings are defined in `barber-shop-core/src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barbershop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

They can also be overridden using OS environment variables:

| Variable | Description | Default |
| --- | --- | --- |
| `DB_URL` | JDBC connection URL | `jdbc:mysql://localhost:3306/barbershop...` |
| `DB_USER` | MySQL database user | `root` |
| `DB_PASSWORD` | MySQL database password | *(empty)* |
| `DB_DRIVER` | JDBC driver class | `com.mysql.cj.jdbc.Driver` |

---

## 📦 Desktop Deployment (Windows / Linux)

Packaging creates a single **standalone shaded executable JAR (*fat jar*)** bundling all dependencies:

```bash
mvn clean package
```
Generated artifact: `barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar`.

### 🪟 Windows

1. Ensure JRE/JDK 17+ is installed (`java -version`).
2. Run in PowerShell or Command Prompt:
   ```powershell
   java -jar barber-shop-desktop\target\barber-shop-desktop-1.0-SNAPSHOT.jar
   ```
3. **Desktop Shortcut:** Create a `Barbershop.bat` file next to the `.jar`:
   ```bat
   @echo off
   start javaw -jar "%~dp0barber-shop-desktop-1.0-SNAPSHOT.jar"
   ```
   Set the shortcut icon to `barber-shop-desktop/src/main/resources/icon.ico`.

### 🐧 Linux

1. Install OpenJDK 17 (`sudo apt install openjdk-17-jre` on Debian/Ubuntu).
2. Run:
   ```bash
   java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
   ```
3. **Application Launcher (`.desktop`)** in `~/.local/share/applications/barbershop.desktop`:
   ```ini
   [Desktop Entry]
   Name=Barbershop
   Exec=java -jar /full/path/to/barber-shop-desktop-1.0-SNAPSHOT.jar
   Icon=/full/path/to/icon.ico
   Type=Application
   Categories=Office;
   ```

---

## 🖥️ Main Screens

| Desktop Screen | Equivalent Web Screen | Purpose |
| --- | --- | --- |
| `TelaCadastroInicial` | - | Initial barbershop setup (RF01) |
| `TelaLogin` | `index.html` | Authentication with credentials (RF02) |
| `TelaHome` | `agenda.html` | Daily schedule, timeline, and quick actions (RF08, RF11, RF07) |
| `Minha Barbearia` | `barbearia.html` | General settings, services, and barbers (RF03, RF04) |
| `Histórico` | `historico.html` | Full appointment history with filters (RF09) |
| `TelaNovoAgendamento` / `TelaEditarAgendamento` | `agendamento.html` | Appointment scheduling with conflict validation (RF05, RF06, RF10) |
| `Clientes` (tab in Barbearia) | - | Consolidated client directory |
| `Relatórios` | `relatorios.html` | Revenue by period, top services, and rankings (RF09) |

---

## 📋 Business Rules Implemented

- If no barbershop exists: opens initial setup.
- If barbershop exists: requires login with PBKDF2 salted hash.
- Appointments require essential fields (client, contact, date/time, service, barber, and channel).
- Home / Daily schedule displays only active appointments (`AGENDADO` and `EM_ATENDIMENTO`).
- History displays all appointment statuses (`AGENDADO`, `EM_ATENDIMENTO`, `CONCLUIDO`, `CANCELADO`).
- Barber scheduling conflict checks calculate real service duration (interval overlap, not fixed slot).
- Scheduling outside configured business hours is blocked.
- Appointment cancellation requires entering a cancellation reason.
- RF11 visual classification applied across both Desktop tables and Web timeline/tables.

---

## 🔎 Requirements Compliance (Current State)

### Functional Requirements

- **RF01**: Allow initial barbershop registration with basic details, services, barbers, and admin user. **Status**: Implemented.
- **RF02**: Allow authentication via username and password. **Status**: Implemented.
- **RF03**: Allow creating, editing, and deleting services. **Status**: Implemented.
- **RF04**: Allow creating, editing, and deleting barbers. **Status**: Implemented.
- **RF05**: Allow creating new appointments with client, contact, date/time, service, barber, and channel. **Status**: Implemented.
- **RF06**: Allow editing and deleting appointments. **Status**: Implemented.
- **RF07**: Allow changing appointment status (start and complete service). **Status**: Implemented.
- **RF08**: Display only non-completed appointments on Home. **Status**: Implemented.
- **RF09**: Display full appointment history including completed records. **Status**: Implemented.
- **RF10**: Validate scheduling conflicts per barber based on actual service duration. **Status**: Implemented.
- **RF11**: Visually classify appointments based on time proximity or status. **Status**: Implemented.

### Non-Functional Requirements

- **RNF01**: System must be developed in Java. **Status**: Implemented.
- **RNF02**: Database engine must be MySQL. **Status**: Implemented.
- **RNF03**: System must run as a desktop application (complemented by web front-end in Stage 8). **Status**: Implemented.
- **RNF04**: Code must follow object-oriented principles and separation of concerns. **Status**: Implemented.
- **RNF05**: Data must be persisted in a relational database. **Status**: Implemented.
- **RNF06**: System must validate mandatory fields before persisting data. **Status**: Implemented.
- **RNF07**: Access must be protected by authentication (PBKDF2 passwords). **Status**: Implemented.

---

## 🧪 Automated Tests

JUnit 5 automated test suite (40 unit tests) executed from project root:

```bash
mvn test
```

Covers:
- Pure domain models and utilities (`model`, `util`, password hashing, date handling, equals/hashCode).
- Business services (`AgendaService`, `AuthService`, `CatalogoService`, `ClassificadorAgenda`, `RelatorioService`) using **in-memory fake repositories** - ultra-fast, isolated, and deterministic tests without needing a real database.

---

## ✅ System Verification (Smoke Test)

In addition to in-memory unit tests, the desktop module includes an end-to-end operational verification tool running against a **real MySQL instance**:

```bash
docker compose up -d
mvn clean package
java -cp barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar br.com.barbershop.app.VerificacaoSistema
```

Automatically validates:
1. Database connectivity
2. Schema creation and migrations
3. Initial setup workflow
4. Authentication with valid/invalid credentials
5. Appointment booking
6. Scheduling conflict detection
7. Appointment cancellation with reason
8. Report generation

Cleans up all generated test records upon completion, leaving the database pristine.

---

## 📸 Screenshots

| Desktop Login | Desktop Initial Setup |
| --- | --- |
| ![Login Screen](docs/screenshots/login.png) | ![Initial Setup](docs/screenshots/cadastro-inicial.png) |

| Desktop Home (Schedule) | Desktop New Appointment |
| --- | --- |
| ![Home](docs/screenshots/home.png) | ![New Appointment](docs/screenshots/novo-agendamento.png) |

| Desktop My Barbershop | Desktop History |
| --- | --- |
| ![My Barbershop](docs/screenshots/minha-barbearia.png) | ![History](docs/screenshots/historico.png) |

| Desktop Reports |
| --- |
| ![Reports](docs/screenshots/relatorios.png) |

---

## 🚀 Roadmap

- **RESTful Back-end API:** Implementation of a backend service layer (e.g. Spring Boot or Micronaut) consuming `barber-shop-core` to provide persistence for `barber-shop-web`.
- **Integration Tests with Testcontainers:** Automated end-to-end integration tests against ephemeral MySQL instances in CI/CD.
- **Role-Based Access Control:** Granular permission system separating admin accounts and individual barber logins.
- **Flyway Migrations:** Automated database schema versioning and deployment.
- **Native Installers:** Packaging with `jpackage` for `.msi` and `.deb` distributions.

---

## ⚠️ Disclaimer

Intended for operational use within the local environment or internal network of a single barbershop.

---

## 👨‍💻 Author

**Lucas Hochmann Rosa**

- Repository: <https://github.com/lucas-hochmann-rosa/barber-shop-suite>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 License

Distributed under the MIT License. See [LICENSE](./LICENSE) for details.
