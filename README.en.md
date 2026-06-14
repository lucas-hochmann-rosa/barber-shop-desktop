# 💈 barber-shop-desktop — *the BarberDesk*

<p align="center">
  <a href="https://github.com/lucas-hochmann-rosa/barber-shop-desktop">
    <img src="https://img.shields.io/badge/GitHub-barber--shop--desktop-181717?style=for-the-badge&logo=github">
  </a>
  <a href="https://www.linkedin.com/in/lucas-hochmann-rosa">
    <img src="https://img.shields.io/badge/LinkedIn-Lucas_Hochmann_Rosa-0A66C2?style=for-the-badge&logo=linkedin">
  </a>
  <a href="#-tech-stack">
    <img src="https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  </a>
  <a href="#-tech-stack">
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENCE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center"><a href="README.md">🇧🇷 Português</a> · 🇺🇸 English</p>

> Desktop application (Java Swing) for the day-to-day operation of a barbershop: initial setup, authentication, service/barber management, and an appointment schedule with history persisted in MySQL.

---

## ⚡ Quick Start

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-desktop.git
cd barber-shop-desktop
docker compose up -d          # spins up a pre-configured MySQL (see docker-compose.yml)
mvn clean package
java -jar target/BarberDesk-1.0-SNAPSHOT.jar
```

Details for each step below.

---

## 📌 Overview

**BarberDesk** is a desktop application (Java Swing) for local/internal-network use, focused on the operational control of a barbershop:

- Initial setup of the company, staff and services.
- Login secured with a salted password hash (PBKDF2).
- Appointment scheduling with per-barber/time conflict validation (accounting for real service duration) and business-hours validation.
- Home panel with the pending schedule, visually classified by status/proximity, and a service grid.
- Full appointment history, with search.
- Client directory and reports dashboard (revenue, top services, barber ranking).

---

## 🧠 Key Features

- Bootstrap flow:
  - if there's no barbershop in the database, opens `TelaCadastroInicial` (initial setup);
  - if one already exists, opens `TelaLogin`.
- Automatic schema initialization (`DatabaseInitService`) on app startup.
- Idempotent migrations that preserve appointment history:
  - service/barber name snapshots;
  - FK removal on `agendamentos` so records can be deleted without losing history.
- Full initial setup:
  - barbershop data;
  - services (name, price, optional image);
  - barbers (name, optional image);
  - admin user.
- Login and session with user + barbershop context.
- Home screen with:
  - service grid with a shortcut to a new appointment;
  - table of pending appointments (`AGENDADO` / `EM_ATENDIMENTO`);
  - context menu to edit, start, finish and cancel.
- "My Barbershop" screen to maintain:
  - general data;
  - services (CRUD);
  - barbers (CRUD).
- Appointment history with every status.
- Appointment creation/edit with:
  - client, contact, date/time, service, barber, source and status.
- Configurable duration per service, used in the conflict rule:
  - blocks a second appointment for the same barber when the intervals truly overlap (accounting for each service's duration), not a fixed window;
  - allows the same time slot for different barbers;
  - allows back-to-back appointments (one ending exactly when the next starts).
- Configurable business hours per barbershop, validated on appointment creation/edit.
- Client directory, auto-populated from appointments, with search.
- Appointment cancellation captures a reason.
- Shortcut to open the client's WhatsApp chat from the appointment.
- Visual classification of appointments by status/time proximity (colored table rows).
- Search/filter on the history and client directory.
- Reports screen: revenue by period, top services, barber ranking.
- Barber/service photos stored as Base64 directly in the database — no dependency on a file existing at some disk path.
- Custom application icon on every window.
- File logging (`~/.barberdesk/logs/`) and a database connection pool (HikariCP).

---

## 🧭 Table of Contents

- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Ground Rules](#-project-ground-rules)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Environment Configuration](#-environment-configuration)
- [Usage](#-usage)
- [Deploy (Windows / Linux)](#-deploy-windows--linux)
- [Main Screens](#-main-screens)
- [Business Rules](#-business-rules-implemented)
- [Requirements Compliance](#-requirements-compliance-current-state)
- [Quick Local Testing](#-quick-local-testing)
- [Screenshots](#-screenshots)
- [Roadmap](#-roadmap)
- [Disclaimer](#-disclaimer)
- [Author](#-author)
- [License](#-license)

---

## 🏗️ Architecture

```text
barber-shop-desktop/
├── pom.xml
├── nbactions.xml                # NetBeans IDE run configuration
├── docker-compose.yml            # Pre-configured local MySQL, matching the project's defaults
├── README.md / README.en.md
├── LICENCE
├── docs/
│   └── screenshots/               # Screenshots used in the README
├── .github/
│   └── workflows/build.yml         # CI: compiles and runs tests on every push/PR
├── src/main/java/br/com/barberdesk/
│   ├── app/Main.java                # Entry point: decides login vs. initial setup
│   ├── dao/                          # Data access (MySQL), one class per entity
│   ├── model/                         # Domain entities (POJOs)
│   ├── service/                        # Business rules (schedule, reports, initial setup)
│   ├── ui/                              # Swing screens (NetBeans GUI Builder)
│   └── util/                             # Helpers (session context, hashing, layout, dates)
├── src/main/resources/
│   ├── config.properties                 # Database connection (overridable via env vars)
│   ├── logback.xml                        # Logging config (console + file)
│   └── db/schema.sql                      # Initial schema, created automatically on first start
├── src/test/java/br/com/barberdesk/       # JUnit 5 tests (pure logic: hashing, dates, equals/hashCode)
└── target/                                 # Generated build artifacts (JAR) — not versioned
```

### Organization

- **app/Main.java**: entry point, decides between initial setup and login.
- **service/DatabaseInitService.java**: schema creation and automatic migrations.
- **service/AgendaService.java**: appointment status transitions and business-hours validation.
- **service/RelatorioService.java**: aggregations for the Reports screen.
- **dao/**: MySQL access layer (CRUD and query rules).
- **ui/TelaHome.java**: main panel, history, clients, reports and barbershop maintenance.
- **ui/TelaNovoAgendamento.java** and **ui/TelaEditarAgendamento.java**: appointment operational flow.

---

## 🧰 Tech Stack

**Language:** Java (compiled for Java 8; runs on newer JREs too)

**UI:** Java Swing (Look & Feel [FlatLaf](https://www.formdev.com/flatlaf/)), screens generated by the NetBeans GUI Builder (`AbsoluteLayout`)

**Build:** Maven

**Database:** MySQL 8 + MySQL Connector/J (`mysql-connector-j 8.3.0`), connection pooling via [HikariCP](https://github.com/brettwooldridge/HikariCP)

**Logging:** SLF4J + Logback (console and file)

**Testing:** JUnit 5

**Local dev:** Docker Compose (MySQL)

**CI:** GitHub Actions (compiles and runs tests on every push/PR)

---

## 📐 Project Ground Rules

- Identifiers (classes, methods, variables, database tables and columns) stay in Portuguese — it's the natural domain vocabulary (barbershop, appointment, barber) and the system is built for local/Brazilian use.
- Code comments are reserved for non-obvious decisions — the "why", not the "what".
- UI copy (Swing screens, user-facing messages) stays in Portuguese: it's the language of the actual end users.
- No credentials are committed: `config.properties` only ships local-environment defaults (empty password), overridable via environment variables for other environments.

---

## ⚙️ Requirements

- JDK 8+
- A running, reachable MySQL instance
- A MySQL user with permission to create/alter tables in the `barberdesk` schema
- Maven 3.x (optional, if running from the NetBeans IDE)

---

## 🔧 Installation

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-desktop.git
cd barber-shop-desktop
```

### 🗄️ Database

Option 1 — Docker Compose (recommended for local development):

```bash
docker compose up -d
```

Spins up a MySQL 8 already configured to match `config.properties` defaults (schema `barberdesk`, user `root` with no password).

Option 2 — your own MySQL: create the database before the first run:

```sql
CREATE DATABASE barberdesk;
```

> Either way, tables are created automatically by the system on first start (`src/main/resources/db/schema.sql`).

---

## 🔐 Environment Configuration

You can configure the connection in `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barberdesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Or override via environment variables:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | JDBC connection URL |
| `DB_USER` | MySQL user |
| `DB_PASSWORD` | MySQL password |
| `DB_DRIVER` | JDBC driver (default `com.mysql.cj.jdbc.Driver`) |

---

## ▶️ Usage

### Option 1: NetBeans (recommended for this project)

- Open the Maven project in NetBeans.
- Run the main class `br.com.barberdesk.app.Main`.

### Option 2: Maven + JAR

```bash
mvn clean package
java -jar target/BarberDesk-1.0-SNAPSHOT.jar
```

---

## 📦 Deploy (Windows / Linux)

This project doesn't produce a native installer (MSI/DEB/RPM) — it's a cross-platform executable JAR via the JVM. Packaging and running follows the same steps on any OS, with small command differences below.

### 1. Build the package

```bash
mvn clean package
```

Produces `target/BarberDesk-1.0-SNAPSHOT.jar` with all dependencies already bundled in (`maven-shade-plugin`) — nothing else needed on the classpath to run it.

### 🪟 Windows

1. **Java**: check you have JRE/JDK 8+ installed (`java -version` in PowerShell/CMD). If not, download [Eclipse Temurin](https://adoptium.net/) and install it.
2. **Database**: bring it up via Docker Compose (`docker compose up -d`, requires Docker Desktop) or install [MySQL Community Server](https://dev.mysql.com/downloads/mysql/) and create the `barberdesk` database manually.
3. **Run**:
   ```powershell
   java -jar target\BarberDesk-1.0-SNAPSHOT.jar
   ```
4. **Desktop shortcut** (optional): create a `BarberDesk.bat` file next to the `.jar`:
   ```bat
   @echo off
   start javaw -jar "%~dp0BarberDesk-1.0-SNAPSHOT.jar"
   ```
   `javaw` (instead of `java`) avoids opening a console window alongside the app. To change the shortcut's icon, convert `icon.png` to `.ico` (Windows shortcuts don't accept `.png` directly) and point the shortcut to it.

### 🐧 Linux

1. **Java**: install a JRE/JDK 8+ through your distro's package manager, e.g.:
   ```bash
   sudo apt install openjdk-17-jre   # Debian/Ubuntu
   sudo dnf install java-17-openjdk  # Fedora
   ```
2. **Database**: `docker compose up -d` (requires Docker) or install MySQL locally (`sudo apt install mysql-server`) and create the `barberdesk` database.
3. **Run**:
   ```bash
   java -jar target/BarberDesk-1.0-SNAPSHOT.jar
   ```
4. **`.desktop` launcher** (optional, to show up in the application menu):
   ```ini
   [Desktop Entry]
   Name=BarberDesk
   Exec=java -jar /full/path/to/BarberDesk-1.0-SNAPSHOT.jar
   Icon=/full/path/to/icon.png
   Type=Application
   Categories=Office;
   ```
   Save it as `~/.local/share/applications/barberdesk.desktop`.

> On both systems, the application keeps no state outside the database — moving the `.jar` around or switching machines doesn't affect the data, as long as `config.properties`/environment variables point to the right MySQL instance (see [Environment Configuration](#-environment-configuration)).

---

## 🖥️ Main Screens

| Screen | Purpose |
| ------ | ------- |
| `TelaCadastroInicial` | Full initial setup of the barbershop |
| `TelaLogin` | Access authentication |
| `TelaHome` | Pending schedule and operational shortcuts |
| `Minha Barbearia` | Edit general data, services and barbers |
| `Histórico` | View every appointment, with search |
| `TelaNovoAgendamento` / `TelaEditarAgendamento` | Create, edit, delete and change status |
| `Clientes` (tab under My Barbershop) | Client directory, with search |
| `Relatórios` | Revenue, top services and barber ranking by period |

---

## 📋 Business Rules (Implemented)

- No barbershop registered: opens initial setup.
- Barbershop registered: requires login.
- An appointment requires essential data (client, contact, date/time, service, barber and source).
- Home only shows unfinished appointments.
- History shows every status.
- Per-barber conflict accounts for the service's real duration (interval overlap, not a fixed window).
- An appointment outside the barbershop's configured business hours is blocked (when configured).
- Cancellation captures a reason.
- Supported statuses:
  - `AGENDADO` (scheduled)
  - `EM_ATENDIMENTO` (in progress)
  - `CONCLUIDO` (completed)
  - `CANCELADO` (canceled)

---

## 🔎 Requirements Compliance (Current State)

### Functional Requirements

- **RF01**: Allow initial setup of the barbershop with basic data, services, barbers and an access user.
  **Status**: Implemented.
- **RF02**: Allow authentication via login and password.
  **Status**: Implemented.
- **RF03**: Allow creating, editing and deleting services.
  **Status**: Implemented.
- **RF04**: Allow creating, editing and deleting barbers.
  **Status**: Implemented.
- **RF05**: Allow creating a new appointment with client, contact, date/time, service, responsible barber and contact source.
  **Status**: Implemented.
- **RF06**: Allow editing and deleting appointments.
  **Status**: Implemented.
- **RF07**: Allow changing an appointment's status (start and finish service).
  **Status**: Implemented.
- **RF08**: Show only unfinished appointments on Home.
  **Status**: Implemented.
- **RF09**: Show the full appointment history, including finished ones.
  **Status**: Implemented.
- **RF10**: Validate a time conflict only when date/time coincide for the same barber.
  **Status**: Implemented with a stricter rule: conflicts account for real interval overlap (each appointment's start/end, by service duration) for the same barber, not just an exact date/time match.
- **RF11**: Visually classify appointments by proximity or status.
  **Status**: Implemented — rows colored by status and by time proximity (appointments starting within 30 minutes).

### Non-Functional Requirements

- **RNF01**: The system must be developed in Java.
  **Status**: Implemented.
- **RNF02**: The database must be MySQL.
  **Status**: Implemented.
- **RNF03**: The system must run as a desktop application.
  **Status**: Implemented.
- **RNF04**: The code must follow object-oriented principles.
  **Status**: Implemented.
- **RNF05**: Data must be persisted in a relational database.
  **Status**: Implemented.
- **RNF06**: The system must validate required fields before saving records.
  **Status**: Implemented.
- **RNF07**: Access must be protected by basic authentication (login and password).
  **Status**: Implemented.

---

## 🧪 Quick Local Testing

There's an automated test suite (JUnit 5) for the project's pure logic — password hashing, date parsing/formatting, and model `equals()`/`hashCode()`:

```bash
mvn test
```

It only covers logic that doesn't depend on a database/GUI. Suggested manual flow for the rest:

1. Start the app with an empty database and confirm the initial setup screen opens.
2. Create a barbershop + user + services + barbers.
3. Close and confirm login works.
4. Create an appointment and confirm it shows up on Home.
5. Try a conflicting appointment (same barber, overlapping interval accounting for service duration) and confirm it's blocked.
6. Change status to `EM_ATENDIMENTO` and `CONCLUIDO`; confirm it leaves Home and shows up in the history.
7. Delete a service/barber in use and confirm the history is preserved (name snapshot).
8. Cancel an appointment with a reason and confirm it shows up in the history.
9. Generate a report for a period with completed appointments.

> DAO integration tests against a real MySQL don't exist yet — see [Roadmap](#-roadmap).

---

## 📸 Screenshots

Screenshots of the main screens, for a quick visual reference:

| Login | Initial Setup |
| --- | --- |
| ![Login screen](docs/screenshots/login.png) | ![Initial setup](docs/screenshots/cadastro-inicial.png) |

| Home (schedule) | New appointment |
| --- | --- |
| ![Home](docs/screenshots/home.png) | ![New appointment](docs/screenshots/novo-agendamento.png) |

| My Barbershop | History |
| --- | --- |
| ![My Barbershop](docs/screenshots/minha-barbearia.png) | ![History](docs/screenshots/historico.png) |

| Clients | Reports |
| --- | --- |
| ![Clients](docs/screenshots/clientes.png) | ![Reports](docs/screenshots/relatorios.png) |

> See [`docs/screenshots/`](docs/screenshots/) for the expected file names.

---

## 🚀 Roadmap

Known items, tracked deliberately as next steps rather than oversights:

- **Full UI layer separation**: `TelaHome.java` still mixes a fair amount of data access with GUI-Builder-generated code — status transitions and initial setup were already extracted into services; the rest (service/barber CRUD grids) is left for a dedicated session with visual testing.
- **DAO integration tests** against a real MySQL (e.g. Testcontainers) — only pure-logic tests exist today.
- **User roles**: currently a single admin per barbershop; having a logged-in barber see only their own schedule would require rethinking the relationship between `Usuario` and `Barbeiro`, which doesn't exist today.
- **Flyway** instead of the manual migrations — deferred since it can't be validated against a real database in this environment.
- **Native installer** via `jpackage`.

---

## ⚠️ Disclaimer

Built for local/internal-network use by a single barbershop — not designed for multi-tenant use or per-user permissions (see [Roadmap](#-roadmap): user roles don't exist yet).

---

## 👨‍💻 Author

**Lucas Hochmann Rosa**

- Repository: <https://github.com/lucas-hochmann-rosa/barber-shop-desktop>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 License

Licensed under MIT. Feel free to use, modify, and distribute, while keeping the copyright notice and crediting **Lucas Hochmann Rosa**.

See the [LICENCE](./LICENCE) file.
