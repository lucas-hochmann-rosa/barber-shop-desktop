# 💈 BarberDesk

<p align="center">
  <a href="https://github.com/lucas-hochmann-rosa/barber-shop-desktop">
    <img src="https://img.shields.io/badge/GitHub-barber--shop--desktop-181717?style=for-the-badge&logo=github">
  </a>
  <a href="https://www.linkedin.com/in/lucas-hochmann-rosa">
    <img src="https://img.shields.io/badge/LinkedIn-Lucas_Hochmann_Rosa-0A66C2?style=for-the-badge&logo=linkedin">
  </a>
  <a href="#-tecnologias">
    <img src="https://img.shields.io/badge/Java-8%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  </a>
  <a href="#-tecnologias">
    <img src="https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  </a>
  <a href="./LICENCE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

<p align="center">🇧🇷 Português · <a href="README.en.md">🇺🇸 English</a></p>

> Sistema desktop (Java Swing) para gestão operacional de uma barbearia: cadastro inicial, autenticação, gestão de serviços e barbeiros, e agenda de atendimentos com histórico persistido em MySQL.

---

## ⚡ Início Rápido

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-desktop.git
cd barber-shop-desktop
# crie o banco uma vez (as tabelas são criadas automaticamente no 1º start):
#   CREATE DATABASE barberdesk;
mvn clean package
java -jar target/BarberDesk-1.0-SNAPSHOT.jar
```

Detalhes de cada passo nas seções abaixo.

---

## 📌 Visão Geral

O **BarberDesk** é uma aplicação desktop (Java Swing) para uso local/rede interna, com foco no controle operacional da barbearia:

- Cadastro inicial da empresa, equipe e serviços.
- Login de acesso com senha em hash SHA-256.
- Agendamento com validação de conflito por barbeiro e horário.
- Painel Home com agenda pendente e grid de serviços.
- Histórico completo de atendimentos.

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
- Home com:
  - grid de serviços com atalho para novo agendamento;
  - tabela de agendamentos pendentes (`AGENDADO` e `EM_ATENDIMENTO`);
  - menu de contexto para editar, iniciar, concluir e cancelar.
- Tela "Minha Barbearia" para manutenção de:
  - dados gerais;
  - serviços (CRUD);
  - barbeiros (CRUD).
- Histórico de agendamentos com todos os status.
- Cadastro e edição de agendamento com campos:
  - cliente, contato, data/hora, serviço, barbeiro, origem e status.
- Regra de conflito:
  - bloqueia agendamento do mesmo barbeiro em janela de 30 minutos;
  - permite o mesmo horário para barbeiros diferentes.

---

## 🧭 Sumário

- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Regras de construção do projeto](#-regras-de-construção-do-projeto)
- [Requisitos](#-requisitos)
- [Instalação](#-instalação)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Execução](#-execução)
- [Telas Principais](#-telas-principais)
- [Regras de Negócio](#-regras-de-negócio-implementadas)
- [Adesão aos Requisitos](#-adesão-aos-requisitos-estado-atual)
- [Testes Locais Rápidos](#-testes-locais-rápidos)
- [Screenshots](#-screenshots)
- [Melhorias Futuras](#-melhorias-futuras)
- [Avisos](#-avisos)
- [Autor](#-autor)
- [Licença](#-licença)

---

## 🏗️ Arquitetura

```text
barber-shop-desktop/
├── pom.xml
├── nbactions.xml                # Configuração de execução direta pela IDE NetBeans
├── README.md / README.en.md
├── LICENCE
├── docs/
│   └── screenshots/               # Prints do sistema usados no README
├── .github/
│   └── workflows/build.yml         # CI: compila o projeto a cada push/PR
├── src/main/java/br/com/barberdesk/
│   ├── app/Main.java                # Ponto de entrada: decide login vs. cadastro inicial
│   ├── dao/                          # Acesso a dados (MySQL), uma classe por entidade
│   ├── model/                         # Entidades de domínio (POJOs)
│   ├── service/                        # Regras de negócio e orquestração entre DAOs
│   ├── ui/                              # Telas Swing (NetBeans GUI Builder)
│   └── util/                             # Helpers (contexto de sessão, hash, layout, datas)
├── src/main/resources/
│   ├── config.properties                 # Conexão com o banco (sobrescrevível por env vars)
│   └── db/schema.sql                      # Schema inicial, criado automaticamente no 1º start
└── target/                                 # Artefatos gerados de build (JAR) — não versionado
```

### Organização

- **app/Main.java**: ponto de entrada e decisão entre cadastro inicial e login.
- **service/DatabaseInitService.java**: criação de schema e migrações automáticas.
- **dao/**: camada de acesso MySQL (CRUD e regras de consulta).
- **ui/TelaHome.java**: painel principal, histórico e manutenção da barbearia.
- **ui/TelaNovoAgendamento.java** e **ui/TelaEditarAgendamento.java**: fluxo operacional da agenda.

---

## 🛠️ Tecnologias

**Linguagem:** Java (compilação alvo Java 8; execução compatível com JREs mais novas)

**Interface:** Java Swing, telas geradas pelo NetBeans GUI Builder (`AbsoluteLayout`)

**Build:** Maven

**Banco de dados:** MySQL 8 + MySQL Connector/J (`mysql-connector-j 8.3.0`)

**CI:** GitHub Actions (compilação automática a cada push/PR)

---

## 📐 Regras de construção do projeto

- Identificadores (classes, métodos, variáveis, tabelas e colunas do banco) ficam em português — é o vocabulário natural do domínio (barbearia, agendamento, barbeiro) e o sistema é feito para uso local/BR.
- Comentários no código ficam reservados para decisões não óbvias — o "porquê", não o "o quê".
- Textos de interface (telas Swing, mensagens ao usuário) ficam sempre em português: é o idioma de quem realmente usa o sistema.
- Nenhuma credencial é commitada: `config.properties` traz apenas defaults de ambiente local (senha vazia), com suporte a sobrescrita por variável de ambiente para outros ambientes.

---

## ⚙️ Requisitos

- JDK 8+
- MySQL ativo e acessível
- Usuário MySQL com permissão para criar/alterar tabelas do schema `barberdesk`
- Maven 3.x (opcional, caso execute pela IDE NetBeans)

---

## 🔧 Instalação

```bash
git clone https://github.com/lucas-hochmann-rosa/barber-shop-desktop.git
cd barber-shop-desktop
```

### 🗄️ Banco de Dados

Crie o banco no MySQL antes da primeira execução:

```sql
CREATE DATABASE barberdesk;
```

> As tabelas são criadas automaticamente pelo sistema no primeiro start (`src/main/resources/db/schema.sql`).

---

## 🔐 Variáveis de Ambiente

Você pode configurar a conexão em `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barberdesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Ou sobrescrever via variáveis de ambiente:

| Variável | Para que serve |
| --- | --- |
| `DB_URL` | URL JDBC de conexão |
| `DB_USER` | Usuário do MySQL |
| `DB_PASSWORD` | Senha do MySQL |
| `DB_DRIVER` | Driver JDBC (padrão `com.mysql.cj.jdbc.Driver`) |

---

## ▶️ Execução

### Opção 1: NetBeans (recomendada no contexto do projeto)

- Abra o projeto Maven no NetBeans.
- Execute a classe principal `br.com.barberdesk.app.Main`.

### Opção 2: Maven + JAR

```bash
mvn clean package
java -jar target/BarberDesk-1.0-SNAPSHOT.jar
```

---

## 🖥️ Telas Principais

| Tela | Objetivo |
| ---- | -------- |
| `TelaCadastroInicial` | Configuração inicial completa da barbearia |
| `TelaLogin` | Autenticação de acesso |
| `TelaHome` | Agenda pendente e atalhos de operação |
| `Minha Barbearia` | Edição de dados gerais, serviços e barbeiros |
| `Histórico` | Visualização de todos os agendamentos |
| `TelaNovoAgendamento` / `TelaEditarAgendamento` | Cadastro, edição, exclusão e mudança de status |

---

## 📋 Regras de Negócio (Implementadas)

- Sem barbearia cadastrada: abre cadastro inicial.
- Com barbearia cadastrada: exige login.
- Agendamento exige dados essenciais (cliente, contato, data/hora, serviço, barbeiro e origem).
- Home exibe apenas agendamentos não concluídos.
- Histórico exibe todos os status.
- Conflito por barbeiro em janela de 30 minutos.
- Status suportados:
  - `AGENDADO`
  - `EM_ATENDIMENTO`
  - `CONCLUIDO`
  - `CANCELADO`

---

## 🔎 Adesão aos Requisitos (Estado Atual)

### Requisitos Funcionais

- **RF01**: Permitir cadastro inicial da barbearia com dados básicos, serviços, barbeiros e usuário de acesso.
  **Status**: Implementado.
- **RF02**: Permitir autenticação por meio de login e senha.
  **Status**: Implementado.
- **RF03**: Permitir cadastrar, editar e excluir serviços.
  **Status**: Implementado.
- **RF04**: Permitir cadastrar, editar e excluir barbeiros.
  **Status**: Implementado.
- **RF05**: Permitir criar novo agendamento informando cliente, contato, data/hora, serviço, barbeiro responsável e origem do contato.
  **Status**: Implementado.
- **RF06**: Permitir editar e excluir agendamentos.
  **Status**: Implementado.
- **RF07**: Permitir alterar o status do agendamento (iniciar e concluir atendimento).
  **Status**: Implementado.
- **RF08**: Exibir na Home apenas agendamentos não concluídos.
  **Status**: Implementado.
- **RF09**: Exibir histórico completo de agendamentos, incluindo concluídos.
  **Status**: Implementado.
- **RF10**: Validar conflito de horário apenas quando houver coincidência de data/hora para o mesmo barbeiro.
  **Status**: Implementado com ajuste de regra: o sistema aplica uma validação mais restritiva, bloqueando conflitos em janela de 30 minutos para o mesmo barbeiro.
- **RF11**: Classificar visualmente os agendamentos conforme sua proximidade ou status.
  **Status**: Pendente — ver [Melhorias Futuras](#-melhorias-futuras).

### Requisitos Não Funcionais

- **RNF01**: O sistema deverá ser desenvolvido na linguagem Java.
  **Status**: Implementado.
- **RNF02**: O banco de dados utilizado deverá ser MySQL.
  **Status**: Implementado.
- **RNF03**: O sistema será executado como aplicação desktop.
  **Status**: Implementado.
- **RNF04**: O código deverá seguir princípios de orientação a objetos.
  **Status**: Implementado.
- **RNF05**: As informações deverão ser persistidas em banco de dados relacional.
  **Status**: Implementado.
- **RNF06**: O sistema deverá validar campos obrigatórios antes de salvar registros.
  **Status**: Implementado.
- **RNF07**: O acesso ao sistema deverá ser protegido por autenticação básica (login e senha).
  **Status**: Implementado.

---

## 🧪 Testes Locais Rápidos

Fluxo manual sugerido:

1. Iniciar a aplicação sem dados no banco e validar a abertura do cadastro inicial.
2. Criar barbearia + usuário + serviços + barbeiros.
3. Encerrar e validar login.
4. Criar agendamento e validar presença na Home.
5. Tentar conflito (mesmo barbeiro em janela de 30 min) e validar bloqueio.
6. Alterar status para `EM_ATENDIMENTO` e `CONCLUIDO`; confirmar saída da Home e presença no histórico.
7. Excluir serviço/barbeiro usado e validar histórico preservado (snapshot de nomes).

> Não há, ainda, suíte automatizada de testes versionada no projeto — ver [Melhorias Futuras](#-melhorias-futuras).

---

## 📸 Screenshots

Prints das telas principais, para referência visual rápida do sistema:

| Login | Cadastro Inicial |
| --- | --- |
| ![Tela de login](docs/screenshots/login.png) | ![Cadastro inicial](docs/screenshots/cadastro-inicial.png) |

| Home (agenda) | Novo agendamento |
| --- | --- |
| ![Home](docs/screenshots/home.png) | ![Novo agendamento](docs/screenshots/novo-agendamento.png) |

| Minha Barbearia | Histórico |
| --- | --- |
| ![Minha Barbearia](docs/screenshots/minha-barbearia.png) | ![Histórico](docs/screenshots/historico.png) |

> Ver [`docs/screenshots/`](docs/screenshots/) para os nomes de arquivo esperados.

---

## 🚀 Melhorias Futuras

Resumo rápido abaixo — a lista completa e priorizada vive em [`ROADMAP.md`](ROADMAP.md).

Itens conhecidos e documentados conscientemente como próximos passos, não como descuido:

- **Hash de senha com salt**: hoje é SHA-256 sem salt (`HashUtil`), adequado ao escopo atual (uso local/rede interna). Migrar para BCrypt/PBKDF2 com salt por usuário antes de qualquer exposição externa.
- **Pool de conexões**: cada chamada de DAO abre uma nova conexão MySQL — funciona bem para uso single-user, mas não escala para múltiplos usuários simultâneos. Avaliar HikariCP.
- **Separação de camadas na UI**: `TelaHome.java` e `TelaCadastroInicial.java` concentram bastante lógica de negócio junto com o código gerado pelo GUI Builder; extrair para services/controllers reduziria o acoplamento.
- **Testes automatizados**: hoje não há suíte de testes. Prioridade: testes unitários para regras de negócio puras e testes de integração dos DAOs contra um MySQL real (ex.: Testcontainers).
- **RF11 (classificação visual de agendamentos)**: ainda não implementado — ver seção de requisitos acima.
- **Índice único do banco vs. regra de conflito**: `ux_barbeiro_horario` trava apenas o mesmo instante exato; a regra de negócio real (janela de 30 minutos) vive em `AgendamentoDAO.verificarConflito`. Avaliar se vale reforçar isso em nível de banco.

---

## ⚠️ Avisos

Projeto pensado para uso local/rede interna de uma única barbearia. Não foi projetado para exposição direta à internet — ver [Melhorias Futuras](#-melhorias-futuras) para o que seria necessário antes disso (hash de senha com salt, pool de conexões, etc.).

---

## 👨‍💻 Autor

**Lucas Hochmann Rosa**

- Repositório: <https://github.com/lucas-hochmann-rosa/barber-shop-desktop>
- GitHub: <https://github.com/lucas-hochmann-rosa>
- LinkedIn: <https://www.linkedin.com/in/lucas-hochmann-rosa>

---

## 📄 Licença

Licenciado sob MIT. Você pode usar, modificar e distribuir, mantendo o aviso de copyright e atribuindo crédito a **Lucas Hochmann Rosa**.

Consulte o arquivo [LICENCE](./LICENCE).
