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
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center">🇧🇷 Português · <a href="README.en.md">🇺🇸 English</a></p>

> Sistema de gestão operacional de barbearia estruturado como monorepo com três módulos: um núcleo compartilhado de regras de negócio em Java (`barber-shop-core`), uma aplicação desktop completa em Java Swing com persistência em MySQL (`barber-shop-desktop`) e uma interface web moderna em HTML, CSS e JavaScript puros (`barber-shop-web`) que compartilha as mesmas regras de negócio.

---

## ⚡ Início Rápido

```bash
# 1. Clonar o repositório
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite

# 2. Executar a versão Desktop (requer Java 17+ e MySQL)
docker compose up -d          # sobe um MySQL 8 já configurado
mvn clean package
java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar

# 3. Ou executar a versão Web (front-end independente)
npx serve barber-shop-web      # e acesse http://localhost:3000 (login: lucas / 1234)
```

---

## 📌 Visão Geral

O **Barbershop** é um sistema para controle operacional completo de uma barbearia (atendimentos, equipe, serviços, histórico e faturamento), projetado em arquitetura modular:

- **Núcleo Compartilhado (`barber-shop-core`)**: Centraliza as entidades de domínio, persistência JDBC, migrações e regras de negócio essenciais (como a regra de classificação RF11 e a validação de sobreposição real de horários RF10), sem qualquer acoplamento com interfaces visuais.
- **Versão Desktop (`barber-shop-desktop`)**: Aplicação desktop em Java Swing (Look & Feel FlatLaf) com fluxo de bootstrap automático (cadastro inicial vs. login), CRUD de serviços/barbeiros, gestão de agenda em tempo real, painel de relatórios e smoke test operacional (`VerificacaoSistema`).
- **Versão Web (`barber-shop-web`)**: Front-end estático moderno em HTML5, CSS3 e JavaScript (Etapa 8), espelhando as telas e fluxos operacionais da barbearia, com destaque para a **régua visual do dia** e a portabilidade direta das regras de negócio do núcleo.

---

## 🧠 Funcionalidades

- Fluxo de bootstrap:
  - se não houver barbearia no banco, abre `TelaCadastroInicial`;
  - se já houver cadastro, abre `TelaLogin`.
- Inicialização automática de schema (`DatabaseInitService`) na abertura do app.
- Migrações idempotentes para preservar histórico de agendamentos:
  - snapshots de nome de serviço/barbeiro;
  - remoção de FKs em `agendamentos` para permitir exclusões sem perda de histórico.
- Cadastro inicial completo:
  - dados da barbearia;
  - serviços (nome, preço e imagem opcional);
  - barbeiros (nome e imagem opcional);
  - usuário administrador.
- Login e sessão com contexto de usuário e barbearia.
- Home / Agenda do dia com:
  - régua visual do dia (faixa do expediente com marcadores de agendamento e indicador da hora atual);
  - grid de serviços com atalho para novo agendamento;
  - tabela de agendamentos pendentes (`AGENDADO` e `EM_ATENDIMENTO`);
  - menu de contexto / botões rápidos para editar, iniciar, concluir e cancelar.
- Tela "Minha Barbearia" para manutenção de:
  - dados gerais;
  - serviços (CRUD);
  - barbeiros (CRUD).
- Histórico de agendamentos com todos os status e filtros.
- Cadastro e edição de agendamento com campos:
  - cliente, contato, data/hora, serviço, barbeiro, origem e status.
- Duração configurável por serviço, usada na regra de conflito:
  - bloqueia agendamento do mesmo barbeiro quando os intervalos se sobrepõem de verdade (considerando a duração de cada serviço), não uma janela fixa;
  - permite o mesmo horário para barbeiros diferentes;
  - permite agendamentos "encostados" (um termina exatamente quando o outro começa).
- Horário de funcionamento configurável por barbearia, validado ao criar/editar agendamento.
- Diretório de clientes, populado automaticamente a partir dos agendamentos, com busca.
- Cancelamento de agendamento captura o motivo.
- Atalho para abrir o WhatsApp do cliente a partir do agendamento.
- Classificação visual de agendamentos por status/proximidade do horário (linhas e marcadores coloridos conforme RF11).
- Busca/filtro no histórico e no diretório de clientes.
- Tela de Relatórios: faturamento por período, serviços mais vendidos e ranking de barbeiros.
- Fotos de barbeiro/serviço guardadas como Base64 direto no banco - não depende de um arquivo existir num caminho específico do disco.
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
- [Melhorias Futuras](#-melhorias-futuras)
- [Avisos](#-avisos)
- [Autor](#-autor)
- [Licença](#-licença)

---

## 🏗️ Arquitetura dos Módulos

O repositório é um **monorepo multi-módulo Maven**, organizado em três módulos com papéis bem delimitados:

```text
barber-shop-suite/
├── pom.xml                             # Módulo pai (agregador Maven): controle centralizado de versões
├── docker-compose.yml                  # MySQL local já configurado pros defaults do projeto
├── README.md / README.en.md
├── LICENSE
├── docs/
│   ├── screenshots/                    # Prints do sistema usados na documentação
│   └── wireframes/                     # Wireframes em SVG puro para desktop e web
│       ├── desktop/                    # Wireframes conceituais da aplicação Swing
│       └── web/                        # Wireframes conceituais da interface web
│
├── barber-shop-core/                    # [MÓDULO 1] Núcleo de regras de negócio compartilhado (Java)
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/com/barberdesk/
│       │   ├── model/                  # Entidades de domínio (POJOs: Agendamento, Barbearia, Servico, Barbeiro, Usuario)
│       │   ├── dao/                    # Camada de acesso a dados (JDBC/MySQL), uma classe por entidade
│       │   │   └── repository/         # Interfaces consumidas pelos services (Repository Pattern)
│       │   ├── service/                # Regras de negócio (AgendaService, AuthService, CatalogoService, ClassificadorAgenda, RelatorioService)
│       │   └── util/                   # Utilitários puros (hash PBKDF2 com salt, parse/formatação de datas, imagens)
│       ├── main/resources/
│       │   ├── config.properties       # Configuração de conexão JDBC com o banco MySQL
│       │   └── db/schema.sql           # DDL do schema inicial, criado automaticamente no 1º start
│       └── test/java/br/com/barberdesk/
│           ├── model/, util/           # Testes unitários de domínio, hash e datas
│           └── service/                # Testes de service com repositórios fake em memória (ex.: ClassificadorAgendaTest)
│               └── fake/               # Implementações em memória das interfaces de repositório
│
├── barber-shop-desktop/                 # [MÓDULO 2] Interface desktop gráfica em Java Swing
│   ├── pom.xml                         # Gera o JAR executável único sombreado (maven-shade-plugin)
│   ├── nbactions.xml                   # Perfis de execução/debug para Apache NetBeans
│   └── src/main/
│       ├── java/br/com/barberdesk/
│       │   ├── app/
│       │   │   ├── Main.java             # Ponto de entrada: decide entre cadastro inicial e login
│       │   │   ├── FabricaDeServicos.java # Composition root: instancia DAOs concretos e injeta nos services
│       │   │   └── VerificacaoSistema.java # Smoke test operacional de ponta a ponta contra MySQL real
│       │   ├── ui/                        # Telas Swing (NetBeans GUI Builder + FlatLaf)
│       │   │   ├── controller/            # Controladores de tela (Home, Barbearia, Catalogo, Relatorios...)
│       │   │   └── support/                # Utilitários de UI (ícones, máscaras, renderizadores de tabela)
│       └── resources/
│           ├── logback.xml                # Configuração de logging (console e arquivo)
│           └── icon.ico                   # Ícone nativo do aplicativo
│
└── barber-shop-web/                     # [MÓDULO 3] Front-end Web independente (HTML, CSS e JavaScript puros)
    ├── README.md                       # Documentação própria do módulo web
    ├── index.html                      # Tela de login / entrada (RF02)
    ├── agenda.html                     # Tela principal: régua do dia, cartões-resumo e pendentes (RF08, RF11)
    ├── agendamento.html                # Novo agendamento e edição com validação de conflito (RF05, RF06, RF10)
    ├── barbearia.html                  # Gestão da barbearia, catálogo de serviços e barbeiros (RF03, RF04)
    ├── historico.html                  # Histórico geral com filtros e ordenação (RF09)
    ├── relatorios.html                 # Relatórios de faturamento, serviços e ranking (RF09)
    ├── verificacao-classificacao.html  # Evidência visual da paridade da regra RF11 contra testes JUnit
    ├── css/                            # Folhas de estilo (base, layout, componentes, paginas)
    ├── js/                             # Lógica de interface, validações e dados de demonstração
    └── img/                            # Ícones e ilustrações em SVG próprio
```

---

## 🚀 Instruções de Execução por Módulo

### 1. Módulo `barber-shop-core` (Núcleo)

Não possui interface gráfica nem ponto de entrada executável direto — é uma biblioteca de regras de negócio e acesso a dados consumida pelo desktop e portada conceitualmente para a web.

Para compilar e rodar a suíte de testes unitários do núcleo:

```bash
mvn test -pl barber-shop-core
```

---

### 2. Módulo `barber-shop-desktop` (Sistema Desktop)

Requer **JDK 17+** e uma instância do **MySQL 8** ativa.

#### Passo prévio: Subir o banco de dados

```bash
docker compose up -d
```

#### Opção A: Executar via Apache NetBeans (Recomendada no contexto acadêmico)

1. Abra a pasta raiz do repositório (`barber-shop-suite`) no NetBeans: **File** → **Open Project**.
2. O NetBeans identificará automaticamente o monorepo Maven e seus submódulos.
3. Expanda o projeto **barber-shop-desktop** (`barber-shop-desktop`), clique com o botão direito e selecione **Run** (a classe principal configurada é `br.com.barberdesk.app.Main`).
4. Os menus de **Debug** e **Profile** funcionam da mesma forma pela IDE.

#### Opção B: Executar via Linha de Comando (Maven + JAR)

```bash
mvn clean package
java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
```

---

### 3. Módulo `barber-shop-web` (Front-end Web)

A versão web é construída com **HTML5, CSS3 e JavaScript puros** — sem frameworks pesados, sem etapas de build e sem dependências externas.

#### Como executar:

- **Opção A (Navegador direto):** Basta abrir o arquivo `barber-shop-web/index.html` em qualquer navegador moderno.
- **Opção B (Servidor estático local - recomendado):**
  ```bash
  # Usando npx serve
  npx serve barber-shop-web

  # Ou usando Python 3
  cd barber-shop-web
  python -m http.server 8000
  ```
  Acesse <http://localhost:8000> ou a porta indicada no terminal.

#### 🔑 Credenciais de Demonstração (Web):
- **Usuário:** `lucas`
- **Senha:** `1234`

> [!NOTE]
> **Nota sobre o Back-end Web (Etapa 8):** O módulo web nesta etapa funciona com dados de exemplo ricos em memória (`barber-shop-web/js/dados.js`). As alterações efetuadas em tela (iniciar/concluir atendimentos, novo agendamento, etc.) operam no estado local da sessão. A integração com uma API back-end persistente (consumindo as regras do `barber-shop-core`) está planejada para a etapa seguinte.

---

## 🔄 Reutilização do Núcleo e Paridade (RF11)

Um dos princípios fundamentais da arquitetura do Barbershop é a preservação e portabilidade das regras de negócio entre as diferentes plataformas:

- **Regra de Classificação da Agenda (RF11):** Define o status visual de cada agendamento conforme a proximidade temporal em relação à hora de referência (`EM_ANDAMENTO`, `ATRASADO`, `IMINENTE` até 60 min, `PROXIMO` até 120 min, `DISTANTE` acima de 120 min, `CONCLUIDO` e `CANCELADO`).
- **Implementação no Java:** Classe `br.com.barberdesk.service.ClassificadorAgenda` no `barber-shop-core`, rigorosamente coberta pela suíte de testes JUnit 5 [`ClassificadorAgendaTest`](barber-shop-core/src/test/java/br/com/barberdesk/service/ClassificadorAgendaTest.java).
- **Portabilidade para JavaScript:** A regra foi portada fielmente para [`barber-shop-web/js/classificacao.js`](barber-shop-web/js/classificacao.js), mantendo exatamente os mesmos nomes de constantes, ordem de precedência e tratamento de fronteiras.
- **Comprovação de Paridade em Navegador:** A página [`barber-shop-web/verificacao-classificacao.html`](barber-shop-web/verificacao-classificacao.html) executa todos os 12 casos do JUnit diretamente no navegador contra o script JS, apresentando uma tabela comparativa com 100% de conformidade comprovada.

---

## 🛠️ Tecnologias

**Linguagem & Plataforma:** Java 17 (Desktop/Core) · HTML5 / CSS3 / JavaScript Vanilla (Web)

**Build & Módulos:** Apache Maven, monorepo multi-módulo com versões centralizadas no `pom.xml` raiz

**Interface Desktop:** Java Swing (Look & Feel [FlatLaf](https://www.formdev.com/flatlaf/) `3.4.1`), telas geradas pelo NetBeans GUI Builder (`AbsoluteLayout`)

**Banco de dados:** MySQL 8 + Driver JDBC (`mysql-connector-j 8.3.0`), pool de conexões [HikariCP](https://github.com/brettwooldridge/HikariCP) `5.1.0`

**Logging:** SLF4J `2.0.16` + Logback `1.5.16` (saída em console e arquivo com rotação)

**Testes Automatizados:** JUnit 5 (Jupiter), testes de service com repositórios fake em memória

**Ambiente Local:** Docker & Docker Compose para MySQL 8

---

## 📐 Regras de Construção do Projeto

- Identificadores (classes, métodos, variáveis, tabelas e colunas do banco) ficam em português - é o vocabulário natural do domínio (barbearia, agendamento, barbeiro) e o sistema é feito para uso local/BR.
- Comentários no código ficam reservados para decisões não óbvias - o "porquê", não o "o quê".
- Textos de interface (telas Swing e páginas Web) ficam sempre em português: é o idioma de quem realmente usa o sistema.
- Nenhuma credencial é commitada: `config.properties` traz apenas defaults de ambiente local (senha vazia), com suporte a sobrescrita por variável de ambiente para outros ambientes.

---

## ⚙️ Requisitos

- JDK 17+ instalado e configurado no `PATH`
- MySQL 8 ativo e acessível (localmente ou via Docker Compose)
- Usuário MySQL com permissão para criar/alterar tabelas no schema `barberdesk`
- Maven 3.8+ (opcional, caso utilize a IDE NetBeans)
- Navegador web moderno (Chrome, Firefox, Edge, Safari) para o módulo web

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

Sobe automaticamente um container MySQL 8 configurado com o schema `barberdesk` e usuário `root` sem senha.

**Opção 2 - MySQL Instalado Localmente:**

```sql
CREATE DATABASE barberdesk;
```

> Em ambas as opções, todas as tabelas e migrações são executadas automaticamente no primeiro start da aplicação desktop (`barber-shop-core/src/main/resources/db/schema.sql`).

---

## 🔐 Variáveis de Ambiente

As configurações de conexão residem em `barber-shop-core/src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barberdesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Ou podem ser sobrescritas via variáveis de ambiente do sistema operacional:

| Variável | Descrição | Padrão |
| --- | --- | --- |
| `DB_URL` | URL de conexão JDBC | `jdbc:mysql://localhost:3306/barberdesk...` |
| `DB_USER` | Usuário do MySQL | `root` |
| `DB_PASSWORD` | Senha do MySQL | *(vazio)* |
| `DB_DRIVER` | Classe do driver JDBC | `com.mysql.cj.jdbc.Driver` |

---

## 📦 Deploy Desktop (Windows / Linux)

O empacotamento gera um único **JAR executável sombreado (*fat jar*)**, contendo todas as dependências:

```bash
mvn clean package
```
Arquivo gerado: `barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar`.

### 🪟 Windows

1. Certifique-se de ter o JRE/JDK 17+ instalado (`java -version`).
2. Execute no PowerShell ou Prompt de Comando:
   ```powershell
   java -jar barber-shop-desktop\target\barber-shop-desktop-1.0-SNAPSHOT.jar
   ```
3. **Atalho sem terminal:** Crie um arquivo `Barbershop.bat` ao lado do `.jar`:
   ```bat
   @echo off
   start javaw -jar "%~dp0barber-shop-desktop-1.0-SNAPSHOT.jar"
   ```
   Aponte o ícone do atalho para `barber-shop-desktop/src/main/resources/icon.ico`.

### 🐧 Linux

1. Instale o OpenJDK 17 (`sudo apt install openjdk-17-jre` no Debian/Ubuntu).
2. Execute:
   ```bash
   java -jar barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar
   ```
3. **Atalho `.desktop`** em `~/.local/share/applications/barbershop.desktop`:
   ```ini
   [Desktop Entry]
   Name=Barbershop
   Exec=java -jar /caminho/completo/para/barber-shop-desktop-1.0-SNAPSHOT.jar
   Icon=/caminho/completo/para/icon.ico
   Type=Application
   Categories=Office;
   ```

---

## 🖥️ Telas Principais

| Tela Desktop | Tela Web Equivalente | Objetivo |
| --- | --- | --- |
| `TelaCadastroInicial` | — | Configuração inicial da barbearia (RF01) |
| `TelaLogin` | `index.html` | Autenticação com credenciais (RF02) |
| `TelaHome` | `agenda.html` | Agenda diária, régua visual e ações rápidas (RF08, RF11, RF07) |
| `Minha Barbearia` | `barbearia.html` | Manutenção de dados gerais, serviços e barbeiros (RF03, RF04) |
| `Histórico` | `historico.html` | Consulta geral de atendimentos com filtros (RF09) |
| `TelaNovoAgendamento` / `TelaEditarAgendamento` | `agendamento.html` | Agendamento com validação de conflito de horário (RF05, RF06, RF10) |
| `Clientes` (aba em Barbearia) | — | Diretório consolidado de clientes |
| `Relatórios` | `relatorios.html` | Faturamento por período, serviços mais vendidos e ranking (RF09) |

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

### Requisitos Não Funcionais

- **RNF01**: O sistema deverá ser desenvolvido na linguagem Java. **Status**: Implementado.
- **RNF02**: O banco de dados utilizado deverá ser MySQL. **Status**: Implementado.
- **RNF03**: O sistema será executado como aplicação desktop (e complementado por front-end web na Etapa 8). **Status**: Implementado.
- **RNF04**: O código deverá seguir princípios de orientação a objetos e separação de responsabilidades. **Status**: Implementado.
- **RNF05**: As informações deverão ser persistidas em banco de dados relacional. **Status**: Implementado.
- **RNF06**: O sistema deverá validar campos obrigatórios antes de salvar registros. **Status**: Implementado.
- **RNF07**: O acesso ao sistema deverá ser protegido por autenticação (login e senha em PBKDF2). **Status**: Implementado.

---

## 🧪 Testes Automatizados

Suíte de testes automatizados JUnit 5 (40 testes unitários) rodando na raiz do projeto:

```bash
mvn test
```

Cobre:
- Lógica pura de domínio e utilitários (`model`, `util`, hash de senhas, formatação de datas, equals/hashCode).
- Services de negócio (`AgendaService`, `AuthService`, `CatalogoService`, `ClassificadorAgenda`, `RelatorioService`) usando **repositórios fake em memória** — testes rápidos, isolados e determinísticos, sem necessidade de banco real.

---

## ✅ Verificação do Sistema (Smoke Test)

Além dos testes unitários em memória, o módulo desktop inclui um utilitário de verificação operacional que testa o sistema de ponta a ponta contra um **MySQL real**:

```bash
docker compose up -d
mvn clean package
java -cp barber-shop-desktop/target/barber-shop-desktop-1.0-SNAPSHOT.jar br.com.barberdesk.app.VerificacaoSistema
```

Valida automaticamente:
1. Conexão com o banco de dados
2. Execução de migrações e schema
3. Cadastro inicial de teste
4. Autenticação com senha correta e rejeição de senha incorreta
5. Criação de agendamento
6. Detecção de conflito de horário
7. Cancelamento de agendamento com motivo
8. Geração de relatórios

Ao final, remove todos os registros criados no teste, deixando o banco limpo.

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

## 🚀 Melhorias Futuras

- **API Back-end RESTful:** Construção de uma camada de serviços web (ex.: Spring Boot ou Micronaut) consumindo o `barber-shop-core` para alimentar o `barber-shop-web` de forma persistente.
- **Testes de Integração com Testcontainers:** Execução automatizada de testes contra instâncias efêmeras de MySQL no pipeline de CI/CD.
- **Papéis e Permissões:** Separação granular entre perfil administrador e perfil de barbeiro individual.
- **Migrações via Flyway:** Evolução automatizada de esquemas de banco de dados.
- **Instalador Nativo:** Geração de pacotes `.msi` / `.deb` via `jpackage`.

---

## ⚠️ Avisos

Projeto desenvolvido com finalidade acadêmica no âmbito do Projeto Integrador II. Concebido para uso operacional em ambiente local/rede interna de uma única barbearia.

---

## 👨‍💻 Autor

**Lucas Hochmann Rosa**

- Repositório: <https://github.com/lucas-hochmann-rosa/barber-shop-suite>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 Licença

Distribuído sob a licença MIT. Consulte o arquivo [LICENSE](./LICENSE) para mais detalhes.
