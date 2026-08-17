# 💈 Barbershop

<p align="center">
  <a href="https://github.com/lucas-hochmann-rosa/barber-shop-suite">
    <img src="https://img.shields.io/badge/GitHub-barber--shop--suite-181717?style=for-the-badge&logo=github">
  </a>
  <a href="https://www.linkedin.com/in/lucas-hochmann-rosa">
    <img src="https://img.shields.io/badge/LinkedIn-Lucas_Hochmann_Rosa-0A66C2?style=for-the-badge&logo=linkedin">
  </a>
  <a href="#-tecnologias">
    <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  </a>
  <a href="#-tecnologias">
    <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  </a>
  <a href="#-tecnologias">
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center">🇧🇷 Português · <a href="README.en.md">🇺🇸 English</a></p>

> Sistema de gestão operacional de barbearia estruturado como monorepo multi-módulo em quatro frentes: um núcleo desacoplado de regras de negócio em Java (`core`), uma aplicação desktop em Java Swing (`desktop`), uma interface web moderna em HTML, CSS e JS (`web`) e um back-end Java Web Spring Boot REST (`api`).

---

## ⚡ Início Rápido

```bash
# 1. Clonar o repositório
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite

# 2. Subir o banco de dados MySQL via Docker
docker compose up -d

# 3. Compilar e rodar todos os testes automatizados
mvn clean test

# 4. Executar a API REST Spring Boot (Back-end Web)
mvn spring-boot:run -pl api
# ou: java -jar api/target/barber-shop-api-1.0-SNAPSHOT.jar (porta 8080)

# 5. Executar a versão Desktop (Java Swing)
mvn package -pl desktop
java -jar desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar

# 6. Executar o Front-end Web independente
npx serve web      # acesse http://localhost:3000 (login: lucas / 1234)
```

---

## 📌 Visão Geral

O **Barbershop** é um sistema para controle operacional completo de uma barbearia (atendimentos, equipe, serviços, histórico e faturamento), projetado em arquitetura modular:

- **Núcleo Compartilhado (`core`)**: Centraliza as entidades de domínio, persistência JDBC, migrações idempotentes e regras de negócio essenciais (como a regra de classificação RF11 e a validação de sobreposição real de horários RF10), sem acoplamento com interfaces visuais.
- **Versão Desktop (`desktop`)**: Aplicação desktop em Java Swing (Look & Feel FlatLaf) com bootstrap automático (cadastro inicial vs. login), CRUD de serviços/barbeiros, gestão de agenda em tempo real, painel de relatórios e smoke test operacional (`VerificacaoSistema`).
- **Versão Web (`web`)**: Front-end moderno em HTML5, CSS3 modular e JavaScript puro, espelhando os fluxos operacionais da barbearia, com destaque para a **régua visual do dia** e o cliente REST integrado.
- **Back-end Web REST (`api`)**: Aplicação Java Web com Spring Boot 3.2.5 REST (Etapa 9), expondo endpoints JSON para autenticação, barbearia, catálogo, agenda, histórico e relatórios.

---

## 🧠 Funcionalidades

- Fluxo de bootstrap:
  - se não houver barbearia no banco, abre `TelaCadastroInicial` (desktop) ou setup via API;
  - se já houver cadastro, abre `TelaLogin`.
- Inicialização automática de schema (`DatabaseInitService`) na abertura do app e na API.
- Migrações idempotentes para preservar histórico de agendamentos:
  - snapshots de nome de serviço/barbeiro;
  - remoção de FKs em `agendamentos` para permitir exclusões sem perda de histórico.
- Cadastro inicial completo:
  - dados da barbearia;
  - serviços (nome, preço, duração e imagem opcional);
  - barbeiros (nome e imagem opcional);
  - usuário administrador.
- Login e sessão com contexto de usuário e barbearia.
- Home / Agenda do dia com:
  - régua visual do dia (faixa do expediente com marcadores de agendamento e indicador da hora atual);
  - grid de serviços com atalho para novo agendamento;
  - tabela de agendamentos pendentes (`AGENDADO` e `EM_ATENDIMENTO`);
  - menu de contexto / botões rápidos para editar, iniciar, concluir e cancelar.
- Tela "Minha Barbearia" para manutenção de dados gerais, serviços e barbeiros.
- Histórico de agendamentos com todos os status e múltiplos filtros.
- Duração configurável por serviço, usada na regra de conflito (RF10).
- Horário de funcionamento configurável por barbearia.
- Diretório de clientes, populado automaticamente a partir dos agendamentos, com busca.
- Cancelamento de agendamento com captura obrigatória do motivo.
- Atalho para abrir o WhatsApp do cliente a partir do agendamento.
- Classificação visual de agendamentos por status/proximidade do horário (RF11).
- Tela de Relatórios: faturamento por período, serviços mais vendidos e ranking de barbeiros.
- Fotos de barbeiro/serviço guardadas como Base64 direto no banco.
- Ícone próprio do aplicativo em todas as janelas.
- Logging em arquivo (`~/.barbershop/logs/`) e pool de conexões com o banco (HikariCP).

---

## 🧭 Sumário

- [Arquitetura dos Módulos](#-arquitetura-dos-módulos)
- [Instruções de Execução por Módulo](#-instruções-de-execução-por-módulo)
- [Reutilização do Núcleo e Paridade (RF11)](#-reutilização-do-núcleo-e-paridade-rf11)
- [Tecnologias](#-tecnologias)
- [Regras de Construção do Projeto](#-regras-de-construção-do-projeto)
- [Requisitos](#-requisitos)
- [Instalação e Banco de Dados](#-instalação-e-banco-de-dados)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Deploy Desktop (Windows / Linux)](#-deploy-desktop-windows--linux)
- [Telas Principais](#-telas-principais)
- [Regras de Negócio](#-regras-de-negócio-implementadas)
- [Adesão aos Requisitos](#-adesão-aos-requisitos-estado-atual)
- [Testes Automatizados](#-testes-automatizados)
- [Verificação do Sistema (Smoke Test)](#-verificação-do-sistema-smoke-test)
- [Screenshots](#-screenshots)
- [Autor](#-autor)
- [Licença](#-licença)

---

## 🏗️ Arquitetura dos Módulos

O repositório é um **monorepo multi-módulo Maven**, organizado em quatro módulos com papéis bem delimitados:

```text
barber-shop-suite/
├── pom.xml                             # Módulo pai (agregador Maven): controle centralizado de versões
├── docker-compose.yml                  # MySQL local já configurado pros defaults do projeto
├── README.md / README.en.md
├── LICENSE
├── docs/
│   ├── screenshots/                    # Prints do sistema usados na documentação
│   ├── wireframes/                     # Wireframes em SVG puro para desktop e web
│   └── etapa9/                         # Documentos de entrega da Etapa 9 (testes, bugtracking, versionamento)
│
├── core/                               # [MÓDULO 1] Núcleo de regras de negócio compartilhado (Java)
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/com/barbershop/
│       │   ├── model/                  # Entidades de domínio (POJOs: Agendamento, Barbearia, Servico, Barbeiro, Usuario)
│       │   ├── dao/                    # Camada de acesso a dados (JDBC/MySQL), uma classe por entidade
│       │   │   └── repository/         # Interfaces consumidas pelos services (Repository Pattern)
│       │   ├── service/                # Regras de negócio (AgendaService, AuthService, CatalogoService, ClassificadorAgenda, RelatorioService)
│       │   └── util/                   # Utilitários puros (hash PBKDF2 com salt, parse/formatação de datas, imagens)
│       ├── main/resources/
│       │   ├── config.properties       # Configuração de conexão JDBC com o banco MySQL
│       │   └── db/schema.sql           # DDL do schema inicial, criado automaticamente no 1º start
│       └── test/java/br/com/barbershop/
│           ├── model/, util/           # Testes unitários de domínio, hash e datas
│           └── service/                # Testes de service com repositórios fake em memória
│
├── desktop/                            # [MÓDULO 2] Interface desktop gráfica em Java Swing
│   ├── pom.xml                         # Gera o JAR executável único sombreado (maven-shade-plugin)
│   ├── nbactions.xml                   # Perfis de execução/debug para Apache NetBeans
│   └── src/main/
│       ├── java/br/com/barbershop/
│       │   ├── app/                    # Ponto de entrada (Main), FabricaDeServicos e VerificacaoSistema
│       │   ├── ui/                     # Telas Swing (NetBeans GUI Builder + FlatLaf)
│       │   │   ├── controller/         # Controladores de tela
│       │   │   └── support/            # Utilitários de UI (ícones, renderizadores de tabela)
│       └── resources/
│           ├── logback.xml             # Configuração de logging
│           └── icon.ico                # Ícone nativo do aplicativo
│
├── web/                                # [MÓDULO 3] Front-end Web independente (HTML, CSS e JavaScript puros)
│   ├── README.md                       # Documentação própria do módulo web
│   ├── index.html                      # Tela de login / entrada (RF02)
│   ├── agenda.html                     # Tela principal: régua do dia, cartões-resumo e pendentes (RF08, RF11)
│   ├── agendamento.html                # Novo agendamento e edição com validação de conflito (RF05, RF06, RF10)
│   ├── barbearia.html                  # Gestão da barbearia, catálogo de serviços e barbeiros (RF03, RF04)
│   ├── historico.html                  # Histórico geral com filtros e ordenação (RF09)
│   ├── relatorios.html                 # Relatórios de faturamento, serviços e ranking (RF09)
│   ├── verificacao-classificacao.html  # Evidência visual da paridade da regra RF11 contra testes JUnit
│   ├── css/                            # Folhas de estilo (base, layout, componentes, paginas)
│   ├── js/                             # Lógica de interface, cliente REST (api.js) e dados de demonstração
│   └── img/                            # Ícones e ilustrações em SVG próprio
│
└── api/                                # [MÓDULO 4] Back-end Java Web Spring Boot REST (Etapa 9)
    ├── pom.xml                         # Dependências do Spring Boot Starter Web e barber-shop-core
    └── src/
        ├── main/java/br/com/barbershop/api/
        │   ├── Application.java        # Main class do Spring Boot
        │   ├── config/                 # Configuração de beans (ServiceConfig) e CORS/Static (WebMvcConfig)
        │   ├── controller/             # Controladores REST (Auth, Barbearia, Catalogo, Agenda, Historico, Relatorios)
        │   └── dto/                    # Objetos de transferência de dados (DTOs)
        └── test/java/br/com/barbershop/api/
            └── controller/             # Testes de integração dos endpoints REST via Spring MockMvc
```

---

## 🚀 Instruções de Execução por Módulo

### 1. Módulo `core` (Núcleo)

Biblioteca de domínio e acesso a dados consumida pelo desktop e pela API.

Para compilar e rodar a suíte de testes unitários do núcleo:

```bash
mvn test -pl core
```

---

### 2. Módulo `desktop` (Sistema Desktop)

Requer **JDK 17+** e uma instância do **MySQL 8** ativa.

#### Passo prévio: Subir o banco de dados

```bash
docker compose up -d
```

#### Opção A: Executar via Apache NetBeans

1. Abra a pasta raiz do repositório (`barber-shop-suite`) no NetBeans: **File** → **Open Project**.
2. O NetBeans identificará automaticamente o monorepo Maven e seus submódulos.
3. Expanda o projeto **desktop**, clique com o botão direito e selecione **Run** (classe principal: `br.com.barbershop.app.Main`).

#### Opção B: Executar via Linha de Comando (JAR)

```bash
mvn clean package -pl desktop
java -jar desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
```

---

### 3. Módulo `api` (Back-end Java Web Spring REST)

Requer **JDK 17+** e **MySQL 8**.

#### Como executar:

```bash
# Execução direta via plugin do Spring Boot
mvn spring-boot:run -pl api

# Ou via pacote JAR
mvn clean package -pl api
java -jar api/target/barber-shop-api-1.0-SNAPSHOT.jar
```

A API estará disponível em `http://localhost:8080/api/` e servirá automaticamente as páginas do front-end `web/`.

---

### 4. Módulo `web` (Front-end Web)

Construído com **HTML5, CSS3 modular e JavaScript puro**, com cliente HTTP (`web/js/api.js`) pronto para comunicar com o back-end Spring REST ou rodar de forma autônoma.

#### Como executar:

- **Opção A (Servidor estático local):**
  ```bash
  npx serve web
  ```
  Acesse <http://localhost:3000>.

- **Opção B (Integrado à API):**
  Basta iniciar o módulo `api` e acessar <http://localhost:8080/agenda.html>.

#### 🔑 Credenciais de Demonstração (Web):
- **Usuário:** `lucas`
- **Senha:** `1234`

---

## 🔄 Reutilização do Núcleo e Paridade (RF11)

Um dos princípios fundamentais da arquitetura do Barbershop é a preservação e portabilidade das regras de negócio entre as diferentes plataformas:

- **Regra de Classificação da Agenda (RF11):** Define o status visual de cada agendamento conforme a proximidade temporal (`EM_ATENDIMENTO`, `ATRASADO`, `PROXIMO` até 30 min, `FUTURO` acima de 30 min, `CONCLUIDO` e `CANCELADO`).
- **Implementação no Java:** Classe `br.com.barbershop.service.ClassificadorAgenda` no `core`, rigorosamente coberta pela suíte de testes JUnit 5.
- **Portabilidade para JavaScript:** A regra foi portada fielmente para `web/js/classificacao.js`, mantendo exatamente os mesmos nomes de constantes e ordem de precedência.
- **Comprovação de Paridade em Navegador:** A página `web/verificacao-classificacao.html` executa todos os 12 casos do JUnit diretamente no navegador contra o script JS, apresentando uma tabela comparativa com 100% de conformidade comprovada.

---

## 🛠️ Tecnologias

- **Linguagem & Plataforma:** Java 17 · Spring Boot 3.2.5 · HTML5 / CSS3 / JavaScript Vanilla
- **Build & Módulos:** Apache Maven, monorepo multi-módulo com versões centralizadas no `pom.xml` raiz
- **Interface Desktop:** Java Swing (Look & Feel [FlatLaf](https://www.formdev.com/flatlaf/) `3.4.1`), telas geradas pelo NetBeans GUI Builder
- **Back-end Web:** Spring Boot 3.2.5 REST (Spring MVC, Jackson JSR-310, Bean Validation)
- **Banco de dados:** MySQL 8 + Driver JDBC (`mysql-connector-j 8.3.0`), pool de conexões [HikariCP](https://github.com/brettwooldridge/HikariCP) `5.1.0`
- **Logging:** SLF4J + Logback (saída em console e arquivo com rotação)
- **Testes Automatizados:** JUnit 5 (Jupiter), Spring MockMvc e repositórios fake em memória
- **Ambiente Local:** Docker & Docker Compose para MySQL 8

---

## 📐 Regras de Construção do Projeto

- Identificadores (classes, métodos, variáveis, tabelas e colunas do banco) ficam em português: é o vocabulário natural do domínio e o sistema é feito para uso local/BR.
- Comentários no código ficam reservados para decisões não óbvias: o "porquê", não o "o quê".
- Textos de interface (telas Swing e páginas Web) ficam sempre em português.
- Nenhuma credencial é commitada: `config.properties` traz apenas defaults de ambiente local (senha vazia), com suporte a sobrescrita por variável de ambiente para outros ambientes.

---

## ⚙️ Requisitos

- JDK 17+ instalado e configurado no `PATH`
- MySQL 8 ativo e acessível (localmente ou via Docker Compose)
- Usuário MySQL com permissão para criar/alterar tabelas no schema `barbershop`
- Maven 3.8+ (opcional, caso utilize a IDE NetBeans)
- Navegador web moderno (Chrome, Firefox, Edge, Safari)

---

## 🔧 Instalação e Banco de Dados

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite
```

### Banco de Dados

**Opção 1 - Docker Compose (recomendado para desenvolvimento local):**

```bash
docker compose up -d
```

Sobe automaticamente um container MySQL 8 configurado com o schema `barbershop` e usuário `root` sem senha.

**Opção 2 - MySQL Instalado Localmente:**

```sql
CREATE DATABASE barbershop;
```

> Em ambas as opções, todas as tabelas e migrações são executadas automaticamente no primeiro start da aplicação (`core/src/main/resources/db/schema.sql`).

---

## 🔐 Variáveis de Ambiente

As configurações de conexão residem em `core/src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barbershop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Ou podem ser sobrescritas via variáveis de ambiente do sistema operacional:

| Variável | Descrição | Padrão |
| --- | --- | --- |
| `DB_URL` | URL de conexão JDBC | `jdbc:mysql://localhost:3306/barbershop...` |
| `DB_USER` | Usuário do MySQL | `root` |
| `DB_PASSWORD` | Senha do MySQL | *(vazio)* |
| `DB_DRIVER` | Classe do driver JDBC | `com.mysql.cj.jdbc.Driver` |

---

## 📦 Deploy Desktop (Windows / Linux)

O empacotamento gera um único **JAR executável sombreado (*fat jar*)**, contendo todas as dependências:

```bash
mvn clean package -pl desktop
```
Arquivo gerado: `desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar`.

### 🪟 Windows

1. Certifique-se de ter o JRE/JDK 17+ instalado (`java -version`).
2. Execute no PowerShell ou Prompt de Comando:
   ```powershell
   java -jar desktop\target\barber-shop-desktop-1.0-SNAPSHOT.jar
   ```

### 🐧 Linux

1. Instale o OpenJDK 17 (`sudo apt install openjdk-17-jre` no Debian/Ubuntu).
2. Execute:
   ```bash
   java -jar desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
   ```

---

## 🖥️ Telas Principais

| Tela Desktop | Tela Web Equivalente | Endpoint REST | Objetivo |
| --- | --- | --- | --- |
| `TelaCadastroInicial` | - | `POST /api/barbearia/setup` | Configuração inicial da barbearia (RF01) |
| `TelaLogin` | `index.html` | `POST /api/auth/login` | Autenticação com credenciais (RF02) |
| `TelaHome` | `agenda.html` | `GET /api/agenda/hoje` | Agenda diária, régua visual e ações rápidas (RF08, RF11, RF07) |
| `Minha Barbearia` | `barbearia.html` | `GET /api/servicos`, `GET /api/barbeiros` | Manutenção de dados gerais, serviços e barbeiros (RF03, RF04) |
| `Histórico` | `historico.html` | `GET /api/historico` | Consulta geral de atendimentos com filtros (RF09) |
| `TelaNovoAgendamento` | `agendamento.html` | `POST /api/agenda` | Agendamento com validação de conflito de horário (RF05, RF06, RF10) |
| `Clientes` (aba em Barbearia) | - | - | Diretório consolidado de clientes |
| `Relatórios` | `relatorios.html` | `GET /api/relatorios` | Faturamento por período, serviços mais vendidos e ranking (RF09) |

---

## 📋 Regras de Negócio (Implementadas)

- Sem barbearia cadastrada: abre cadastro inicial.
- Com barbearia cadastrada: exige login com hash salgado PBKDF2.
- Agendamento exige dados essenciais (cliente, contato, data/hora, serviço, barbeiro e origem).
- Home / Agenda exibe apenas agendamentos não concluídos (`AGENDADO` e `EM_ATENDIMENTO`).
- Histórico exibe todos os status (`AGENDADO`, `EM_ATENDIMENTO`, `CONCLUIDO`, `CANCELADO`).
- Conflito por barbeiro considera a duração real do serviço (sobreposição real de intervalos, não janela fixa).
- Agendamento fora do horário de funcionamento configurado da barbearia é bloqueado.
- Cancelamento de agendamento exige captura do motivo.
- Classificação visual RF11 aplicada tanto na grade desktop quanto na régua/tabela web.

---

## 🔎 Adesão aos Requisitos (Estado Atual)

### Requisitos Funcionais

- **RF01**: Permitir cadastro inicial da barbearia com dados básicos, serviços, barbeiros e usuário de acesso. **Status**: Implementado.
- **RF02**: Permitir autenticação por meio de login e senha. **Status**: Implementado.
- **RF03**: Permitir cadastrar, editar e excluir serviços. **Status**: Implementado.
- **RF04**: Permitir cadastrar, editar e excluir barbeiros. **Status**: Implementado.
- **RF05**: Permitir criar novo agendamento informando cliente, contato, data/hora, serviço, barbeiro responsável e origem do contato. **Status**: Implementado.
- **RF06**: Permitir editar e excluir agendamentos. **Status**: Implementado.
- **RF07**: Permitir alterar o status do agendamento (iniciar e concluir atendimento). **Status**: Implementado.
- **RF08**: Exibir na Home apenas agendamentos não concluídos. **Status**: Implementado.
- **RF09**: Exibir histórico completo de agendamentos, incluindo concluídos. **Status**: Implementado.
- **RF10**: Validar conflito de horário por barbeiro considerando a sobreposição real de horários pela duração do serviço. **Status**: Implementado.
- **RF11**: Classificar visualmente os agendamentos conforme sua proximidade ou status. **Status**: Implementado.

---

## 🧪 Testes Automatizados

Suíte de testes automatizados JUnit 5 (66 testes automatizados) rodando na raiz do projeto:

```bash
mvn clean test
```

Cobre:
- Lógica pura de domínio e utilitários (`model`, `util`, hash de senhas, formatação de datas, equals/hashCode).
- Services de negócio (`AgendaService`, `AuthService`, `CatalogoService`, `ClassificadorAgenda`, `RelatorioService`) usando **repositórios fake em memória**.
- Controladores REST (`AuthController`, `AgendaController`, `BarbeariaController`, `CatalogoController`, `HistoricoController`, `RelatorioController`) via **Spring MockMvc**.

---

## ✅ Verificação do Sistema (Smoke Test)

Além dos testes unitários em memória, o módulo desktop inclui um utilitário de verificação operacional que testa o sistema de ponta a ponta contra um **MySQL real**:

```bash
docker compose up -d
mvn clean package -pl desktop
java -cp desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar br.com.barbershop.app.VerificacaoSistema
```

---

## 📸 Screenshots

| Login Desktop | Cadastro Inicial Desktop |
| --- | --- |
| ![Tela de login](docs/screenshots/login.png) | ![Cadastro inicial](docs/screenshots/cadastro-inicial.png) |

| Home Desktop (Agenda) | Novo Agendamento Desktop |
| --- | --- |
| ![Home](docs/screenshots/home.png) | ![Novo agendamento](docs/screenshots/novo-agendamento.png) |

| Minha Barbearia Desktop | Histórico Desktop |
| --- | --- |
| ![Minha Barbearia](docs/screenshots/minha-barbearia.png) | ![Histórico](docs/screenshots/historico.png) |

| Relatórios Desktop |
| --- |
| ![Relatórios](docs/screenshots/relatorios.png) |

---

## 👨‍💻 Autor

**Lucas Hochmann Rosa**

- Repositório: <https://github.com/lucas-hochmann-rosa/barber-shop-suite>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 Licença

Distribuído sob a licença MIT. Consulte o arquivo [LICENSE](./LICENSE) para mais detalhes.
