# 💈 BarberDesk

<p align="center">
  <a href="https://github.com/hrlucas">
    <img src="https://img.shields.io/badge/GitHub-hrlucas-181717?style=for-the-badge&logo=github">
  </a>
  <a href="https://www.linkedin.com/in/lucas-hochmann-rosa-456bb7339/">
    <img src="https://img.shields.io/badge/LinkedIn-Lucas_Hochmann_Rosa-0A66C2?style=for-the-badge&logo=linkedin">
  </a>
  <a href="./LICENCE">
    <img src="https://img.shields.io/badge/License-MIT-2ea44f?style=for-the-badge">
  </a>
</p>

> Desenvolvi o **BarberDesk** como projeto integrador do meu curso Técnico em Desenvolvimento de Sistemas, com o objetivo de centralizar a operação de uma barbearia em um sistema desktop Java. O projeto cobre cadastro inicial, autenticação, gestão de serviços e barbeiros, além de agenda com histórico persistido em MySQL.

---

## 🚧 Status do Projeto

**Em desenvolvimento.**

---

## 📌 Visão Geral

O **BarberDesk** é uma aplicação desktop (Java Swing) para uso local/rede interna, com foco no controle operacional da barbearia:

- Cadastro inicial da empresa, equipe e serviços.
- Login de acesso com senha em hash SHA-256.
- Agendamento com validação de conflito por barbeiro e horário.
- Painel Home com agenda pendente e grid de serviços.
- Histórico completo de atendimentos.

A base funcional e as regras do sistema foram atualizadas conforme a implementação real do código.

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

## 🏗️ Arquitetura

```text
project-root/
│
├── pom.xml
├── nbactions.xml
├── README.md
├── LICENCE
├── src/main/java/br/com/barberdesk/
│   ├── app/Main.java
│   ├── dao/
│   ├── model/
│   ├── service/
│   ├── ui/
│   └── util/
├── src/main/resources/
│   ├── config.properties
│   └── db/schema.sql
└── target/                      # artefatos gerados de build (JAR)
```

### Organização

- **app/Main.java**: ponto de entrada e decisão entre cadastro inicial e login.
- **service/DatabaseInitService.java**: criação de schema e migrações automáticas.
- **dao/**: camada de acesso MySQL (CRUD e regras de consulta).
- **ui/TelaHome.java**: painel principal, histórico e manutenção da barbearia.
- **ui/TelaNovoAgendamento.java** e **ui/TelaEditarAgendamento.java**: fluxo operacional da agenda.

---

## 🛠️ Tecnologias

- Java (compilação alvo Java 8; execução compatível com JREs mais novas)
- Java Swing (interface desktop)
- Maven (build e empacotamento)
- MySQL 8 + MySQL Connector/J (`mysql-connector-j 8.3.0`)
- NetBeans GUI Builder (`AbsoluteLayout`)

---

## ⚙️ Requisitos

- JDK 8+
- MySQL ativo e acessível
- Usuário MySQL com permissão para criar/alterar tabelas do schema `barberdesk`
- Maven 3.x (opcional, caso execute pela IDE NetBeans)

---

## 🔧 Instalação

```bash
git clone <url-do-repositorio>
cd BarberDesktop
```

### 🗄️ Banco de Dados

Crie o banco no MySQL antes da primeira execução:

```sql
CREATE DATABASE barberdesk;
```

> As tabelas são criadas automaticamente pelo sistema no primeiro start (`src/main/resources/db/schema.sql`).

### 🔐 Configuração

Você pode configurar a conexão em `src/main/resources/config.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/barberdesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo
db.user=root
db.password=
db.driver=com.mysql.cj.jdbc.Driver
```

Ou sobrescrever via variáveis de ambiente:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `DB_DRIVER`

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

## 🔎 Aderência ao PDF (Estado Atual)

Implementado:
- RF01 a RF10.
- RNF01 a RNF07.

Parcial / pendente:
- RF11 (classificação visual por cores na agenda) ainda não está aplicada na UI atual.
- O menu lateral existe, mas está fixo (não recolhível nesta versão).

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

> Atualmente, não há suíte automatizada de testes versionada no projeto.

---

## 📄 Licença

Licenciado sob MIT. Você pode usar, modificar e distribuir, mantendo o aviso de copyright e atribuindo crédito a **Lucas Hochmann Rosa / hrlucas.dev**.

Consulte o arquivo [LICENCE](./LICENCE).

---

## 👨‍💻 Autor

**Lucas Hochmann Rosa / hrlucas.dev** - Desenvolvedor Full Stack

- GitHub: https://github.com/hrlucas
- LinkedIn: https://www.linkedin.com/in/lucas-hochmann-rosa-456bb7339/
