# 💈 BarberDesk

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
# create the database once (tables are created automatically on first start):
#   CREATE DATABASE barberdesk;
mvn clean package
java -jar target/BarberDesk-1.0-SNAPSHOT.jar
```

Details for each step below.

---

## 📌 Overview

**BarberDesk** is a desktop application (Java Swing) for local/internal-network use, focused on the operational control of a barbershop:

- Initial setup of the company, staff and services.
- Login secured with a SHA-256 password hash.
- Appointment scheduling with per-barber/time conflict validation.
- Home panel with the pending schedule and a service grid.
- Full appointment history.

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
- Conflict rule:
  - blocks a second appointment for the same barber within a 30-minute window;
  - allows the same time slot for different barbers.

---

## 🧭 Table of Contents

- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Ground Rules](#-project-ground-rules)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Environment Configuration](#-environment-configuration)
- [Usage](#-usage)
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
├── README.md / README.en.md
├── LICENCE
├── docs/
│   └── screenshots/               # Screenshots used in the README
├── .github/
│   └── workflows/build.yml         # CI: compiles the project on every push/PR
├── src/main/java/br/com/barberdesk/
│   ├── app/Main.java                # Entry point: decides login vs. initial setup
│   ├── dao/                          # Data access (MySQL), one class per entity
│   ├── model/                         # Domain entities (POJOs)
│   ├── service/                        # Business rules and orchestration between DAOs
│   ├── ui/                              # Swing screens (NetBeans GUI Builder)
│   └── util/                             # Helpers (session context, hashing, layout, dates)
├── src/main/resources/
│   ├── config.properties                 # Database connection (overridable via env vars)
│   └── db/schema.sql                      # Initial schema, created automatically on first start
└── target/                                 # Generated build artifacts (JAR) — not versioned
```

### Organization

- **app/Main.java**: entry point, decides between initial setup and login.
- **service/DatabaseInitService.java**: schema creation and automatic migrations.
- **dao/**: MySQL access layer (CRUD and query rules).
- **ui/TelaHome.java**: main panel, history and barbershop maintenance.
- **ui/TelaNovoAgendamento.java** and **ui/TelaEditarAgendamento.java**: appointment operational flow.

---

## 🧰 Tech Stack

**Language:** Java (compiled for Java 8; runs on newer JREs too)

**UI:** Java Swing, screens generated by the NetBeans GUI Builder (`AbsoluteLayout`)

**Build:** Maven

**Database:** MySQL 8 + MySQL Connector/J (`mysql-connector-j 8.3.0`)

**CI:** GitHub Actions (automatic compile on every push/PR)

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

Create the database in MySQL before the first run:

```sql
CREATE DATABASE barberdesk;
```

> Tables are created automatically by the system on first start (`src/main/resources/db/schema.sql`).

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

## 🖥️ Main Screens

| Screen | Purpose |
| ------ | ------- |
| `TelaCadastroInicial` | Full initial setup of the barbershop |
| `TelaLogin` | Access authentication |
| `TelaHome` | Pending schedule and operational shortcuts |
| `Minha Barbearia` | Edit general data, services and barbers |
| `Histórico` | View every appointment |
| `TelaNovoAgendamento` / `TelaEditarAgendamento` | Create, edit, delete and change status |

---

## 📋 Business Rules (Implemented)

- No barbershop registered: opens initial setup.
- Barbershop registered: requires login.
- An appointment requires essential data (client, contact, date/time, service, barber and source).
- Home only shows unfinished appointments.
- History shows every status.
- Per-barber conflict within a 30-minute window.
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
  **Status**: Implemented with a stricter rule: the system blocks conflicts within a 30-minute window for the same barber.
- **RF11**: Visually classify appointments by proximity or status.
  **Status**: Pending — see [Roadmap](#-roadmap).

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

Suggested manual flow:

1. Start the app with an empty database and confirm the initial setup screen opens.
2. Create a barbershop + user + services + barbers.
3. Close and confirm login works.
4. Create an appointment and confirm it shows up on Home.
5. Try a conflicting appointment (same barber within a 30-min window) and confirm it's blocked.
6. Change status to `EM_ATENDIMENTO` and `CONCLUIDO`; confirm it leaves Home and shows up in the history.
7. Delete a service/barber in use and confirm the history is preserved (name snapshot).

> There is no automated test suite versioned in the project yet — see [Roadmap](#-roadmap).

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

> See [`docs/screenshots/`](docs/screenshots/) for the expected file names.

---

## 🚀 Roadmap

Known items, tracked deliberately as next steps rather than oversights:

- **Salted password hashing**: currently plain SHA-256 (`HashUtil`), fine for the current scope (local/internal-network use). Migrate to BCrypt/PBKDF2 with a per-user salt before any external exposure.
- **Connection pooling**: every DAO call opens a new MySQL connection today — works fine for single-user use, doesn't scale to concurrent users. Evaluate HikariCP.
- **UI layer separation**: `TelaHome.java` and `TelaCadastroInicial.java` mix a fair amount of business logic with GUI-Builder-generated code; extracting it into services/controllers would reduce coupling.
- **Automated tests**: no test suite exists yet. Priority: unit tests for pure business rules and integration tests for the DAOs against a real MySQL (e.g. Testcontainers).
- **RF11 (visual appointment classification)**: not implemented yet — see the requirements section above.
- **Database unique index vs. conflict rule**: `ux_barbeiro_horario` only locks the exact same instant; the real business rule (30-minute window) lives in `AgendamentoDAO.verificarConflito`. Worth evaluating whether to reinforce this at the database level.

---

## ⚠️ Disclaimer

Built for local/internal-network use by a single barbershop. Not designed for direct internet exposure — see [Roadmap](#-roadmap) for what that would require (salted password hashing, connection pooling, etc.).

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
