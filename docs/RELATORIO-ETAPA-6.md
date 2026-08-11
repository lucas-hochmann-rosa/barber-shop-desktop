# Relatório — Etapa 6: Separação de Camadas e Preparação para Migração Web

**Projeto:** BarberDesk — Projeto Integrador II
**Autor:** Lucas Hochmann Rosa

---

## 1. Introdução

Esta etapa teve como objetivo separar as regras de negócio do BarberDesk da interface gráfica (Java Swing), aplicando princípios SOLID — com ênfase em SRP (Single Responsibility Principle) — e eliminando *code smells* identificados no código herdado das etapas anteriores. O sistema desktop precisava continuar funcionando exatamente como antes: nenhuma tela, fluxo ou regra de negócio poderia mudar de comportamento.

O motivo prático por trás da separação: a próxima etapa do projeto prevê um sistema Web reaproveitando as mesmas regras de negócio hoje usadas pelo desktop. Um sistema Web não pode depender de `javax.swing`/NetBeans GUI Builder, então qualquer classe que misturasse SQL, regra de negócio e código de tela ao mesmo tempo seria, na prática, código morto para a próxima etapa — teria que ser reescrito do zero, e não reaproveitado.

O trabalho foi dividido em 8 fases incrementais (Fase 0 a Fase 7), cada uma com critério de aceite verificável e cada uma fechada com `mvn clean test` passando antes de avançar para a próxima. Este relatório documenta o resultado.

---

## 2. Reestruturação Multi-módulo

O projeto, que antes era um único módulo Maven, foi dividido em dois:

- **`barbershop-core`**: modelo de domínio (`model`), acesso a dados (`dao`, `dao.repository`), regras de negócio (`service`) e utilitários puros (`util`). **Não depende de `javax.swing` em nenhuma classe** — a única classe que toca a API `java.awt` é `ImageStorageUtil`, e só usa `java.awt.Image`/`BufferedImage` para redimensionar imagens antes de gravá-las em Base64, uma API de processamento de imagem que funciona normalmente em ambiente headless (sem tela), como um servidor. Esse módulo é o candidato natural a ser reaproveitado por uma futura camada Web.
- **`barbershop-desktop`**: tudo que é específico do desktop Swing — as telas (`ui`), os novos controllers de UI (`ui.controller`), suporte de UI (`ui.support`) e o ponto de montagem da aplicação (`app`), incluindo `FabricaDeServicos` (composition root) e `VerificacaoSistema` (smoke test operacional).

Essa fronteira é o que torna a "prontidão para migração Web" (seção 6) concreta e verificável, e não apenas uma intenção declarada.

---

## 3. Princípios SOLID Aplicados

### SRP — Single Responsibility Principle

- **`TelaHome`** concentrava, em ~1480 linhas, tanto o código gerado pelo NetBeans GUI Builder quanto toda a lógica de quatro áreas distintas da tela (agenda, catálogo, clientes, relatórios). Foi quebrada em seis controllers dedicados (`AgendaController`, `HistoricoController`, `CatalogoController`, `ClienteController`, `BarbeariaController`, `RelatorioController`), cada um responsável por uma única área. `TelaHome` caiu para 773 linhas — das quais 484 são `initComponents()` e 73 são o bloco de declaração de variáveis, ambos gerados pelo NetBeans e propositalmente não tocados (ver seção 4).
- **`RelatorioService`** e **`DatabaseInitService`** continham SQL direto (consultas de agregação e todo o script de migração de schema, respectivamente) misturado com a própria regra de orquestração. O SQL foi extraído para `RelatorioDAO` e `SchemaInitializer`; os services viraram delegadores finos.
- **`AppContext`** duplicava, em campos próprios, dados que já existiam em `Session` (usuário e barbearia logados). Foi redesenhado para guardar um único campo `Session`, eliminando a duplicação de estado.

### OCP — Open/Closed Principle

Os services dependem de interfaces de repositório (`AgendamentoRepository`, `ClienteRepository`, etc.), não de classes DAO concretas. Uma nova forma de persistência (ex.: repositório em memória para testes, ou futuramente um repositório HTTP para um cliente Web) pode ser adicionada implementando a interface, sem alterar uma linha sequer dos services existentes.

### LSP — Liskov Substitution Principle

Os repositórios *fake* criados na Fase 6 (`FakeAgendamentoRepository`, `FakeClienteRepository`, `FakeUsuarioRepository`, `FakeRelatorioRepository`) substituem as implementações JDBC reais nos testes sem que `AgendaService`, `AuthService` ou `RelatorioService` percebam a diferença — os services continuam funcionando corretamente com qualquer implementação que respeite o contrato da interface.

### ISP — Interface Segregation Principle

As interfaces de repositório nasceram enxutas (Fase 1) e cresceram sob demanda, conforme a UI parava de acessar os DAOs diretamente (Fase 4) — nunca replicaram de uma vez toda a superfície de métodos das classes DAO concretas. `RelatorioRepository`, por exemplo, expõe só os três métodos de agregação que `RelatorioService` realmente usa, não todo o SQL que `RelatorioDAO` poderia executar.

### DIP — Dependency Inversion Principle

Nenhum service instancia um DAO concreto com `new`. Todos recebem suas dependências (interfaces de repositório) via construtor. `FabricaDeServicos`, no módulo desktop, é o único ponto do sistema que conhece as classes DAO concretas e monta o grafo de objetos — um *composition root* manual, sem framework de injeção de dependência (não foi usado Spring nem CDI, por decisão explícita de manter o stack do projeto).

---

## 4. Code Smells Eliminados

| Code Smell | Classes Afetadas | Refatoração Aplicada | Resultado |
| --- | --- | --- | --- |
| God Class | `TelaHome` (~1480 linhas) | Extração de 6 controllers dedicados (`ui.controller`) + `StatusRowRenderer` compartilhado movido para `ui.support` | `TelaHome` caiu para 773 linhas; o código de orquestração de UI que sobrou é só delegação de uma linha para o controller responsável |
| SQL embutido em Service | `RelatorioService`, `DatabaseInitService` | SQL extraído para `RelatorioDAO` e `SchemaInitializer` | Services viraram delegadores finos, sem `import java.sql.*` além de `SQLException` na assinatura |
| Acoplamento direto UI → DAO | `TelaCadastroInicial`, `TelaNovoAgendamento`, `TelaEditarAgendamento`, `TelaHome` | UI passa a falar só com services, obtidos via `FabricaDeServicos` | `grep -rn "DAO" barbershop-desktop/src` não retorna nenhuma ocorrência fora de `FabricaDeServicos.java` |
| Estado de sessão duplicado | `AppContext`, `Session` | `AppContext` unificado para guardar um único `Session` | Uma única fonte de verdade para usuário/barbearia logados; elimina o risco dos dois ficarem dessincronizados |
| Instanciação concreta espalhada / falta de injeção | Todos os `service.*` (antes recebiam DAOs concretos ou eram instanciados ad hoc pela UI) | Interfaces de repositório + injeção por construtor + `FabricaDeServicos` como composition root | Services testáveis isoladamente com fakes em memória, sem precisar de banco nem de framework de DI |
| Retorno primitivo forçando uma nova consulta | `SetupService.criarCadastroInicial` (retornava `int`, obrigando `TelaCadastroInicial` a buscar o usuário de novo) | Passa a retornar o `Usuario` já criado | Elimina um round-trip ao banco redundante, efeito colateral natural da remoção do acesso direto a DAO em `TelaCadastroInicial` |

---

## 5. Design Patterns Identificados/Aplicados

- **Repository Pattern** (`dao.repository`): abstrai a persistência atrás de interfaces consumidas pelos services, permitindo trocar a implementação (JDBC real, fake em memória) sem tocar na regra de negócio.
- **Dependency Injection manual (Constructor Injection)**: toda dependência de um service é recebida via construtor — sem framework, sem *service locator*, sem campos estáticos mutáveis.
- **Composition Root / Factory** (`FabricaDeServicos`): único ponto do módulo desktop que sabe montar o grafo de objetos completo (DAO → service), documentado explicitamente em seu Javadoc como tal.
- **Controller** (`ui.controller`): cada controller isola a lógica de uma área da tela (dados + eventos), recebendo os componentes Swing e services de que precisa via construtor — não é MVC no sentido estrito de framework web, mas cumpre o mesmo papel de separar "o que a tela faz" de "como ela é desenhada".
- **Strategy** (`StatusRowRenderer`): a colorização de linha por status recebe a origem dos dados como um `Supplier<List<Agendamento>>` no construtor, permitindo que a mesma classe sirva tanto a tabela de agendamentos pendentes quanto a de histórico, cada uma com sua própria fonte.
- **Fake Object** (`service.fake`, em `src/test`): *test doubles* que implementam as interfaces de repositório com armazenamento em memória, usados nos testes de service para isolar a lógica de negócio do banco de dados real.

---

## 6. Prontidão para Migração Web

`barbershop-core` não tem nenhuma dependência de `javax.swing` (verificado por busca em todo o módulo — ver seção 2). Isso significa que uma futura camada Web (ex.: um projeto Spring Boot ou Javalin separado) pode:

1. Depender de `barbershop-core` como uma biblioteca Maven (`br.com.barberdesk:barbershop-core`).
2. Reaproveitar `model`, `dao` (implementações JDBC reais) e, principalmente, `service` — `AgendaService`, `AuthService`, `CatalogoService`, `ClienteService`, `BarbeariaService`, `RelatorioService`, `SetupService`, `SessionService`, `DatabaseInitService` — sem modificar uma linha sequer.
3. Escrever seus próprios controllers REST (ex.: `AgendamentoController` no estilo Spring `@RestController`) que chamam exatamente os mesmos services hoje chamados por `TelaNovoAgendamento`/`TelaEditarAgendamento`/`TelaHome`, montando as respostas HTTP a partir dos mesmos objetos de domínio (`Agendamento`, `Servico`, `Barbeiro` etc.).

O que **não** seria reaproveitável (por natureza, não por descuido): tudo em `barbershop-desktop/src/main/java/br/com/barberdesk/ui` e `ui.controller` — são Swing puro, e uma aplicação Web precisaria de sua própria camada de apresentação. `FabricaDeServicos` também não se aplica diretamente a um ambiente Web (que tipicamente usa um container de injeção de dependência do próprio framework), mas serve como referência de quais services existem e como são montados.

---

## 7. Evidências

### 7.1 Build e testes automatizados

Saída real de `mvn -B clean test` na raiz do projeto, após a Fase 6 (repositórios em memória + `VerificacaoSistema`):

```
[INFO] Reactor Summary for BarberDesk 1.0-SNAPSHOT:
[INFO]
[INFO] BarberDesk ......................................... SUCCESS
[INFO] BarberDesk Core .................................... SUCCESS
[INFO] BarberDesk Desktop ................................. SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS

Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
```

Evolução da suíte de testes ao longo da etapa: 19 testes (só lógica pura de `model`/`util`, herdados de etapas anteriores) → 40 testes (com a adição de `AgendaServiceTest`, `AuthServiceTest` e `RelatorioServiceTest`, usando os repositórios fake da Fase 6).

### 7.2 Verificação de ponta a ponta contra MySQL real

`VerificacaoSistema` (`br.com.barberdesk.app.VerificacaoSistema`) foi executado contra o MySQL real do `docker-compose.yml` da raiz do projeto (`docker compose up -d`), a partir do jar sombreado (`mvn clean package` → `barbershop-desktop/target/barbershop-desktop-1.0-SNAPSHOT.jar`). Saída real:

```
=== BarberDesk — Verificação do Sistema ===

[OK]    Conexão com o banco de dados
[OK]    Schema do banco (migrações)
[OK]    Cadastro inicial (barbearia + usuário + serviço + barbeiro)
[OK]    Autenticação com senha correta
[OK]    Autenticação com senha incorreta é rejeitada
[OK]    Criação de agendamento
[OK]    Conflito de horário é detectado corretamente
[OK]    Cancelamento de agendamento
[OK]    Geração de relatório (faturamento/serviços/ranking)

9 checagem(ns), 0 falha(s).
```

Processo saiu com código 0. Os dados de verificação (barbearia, usuário, serviço, barbeiro e agendamento criados durante a execução) foram removidos ao final, sem deixar resíduo no banco — confirmado consultando a base diretamente após a execução.

### 7.3 Critério de aceite da Fase 4 (UI sem acesso direto a DAO)

```
$ grep -rn "DAO" barbershop-desktop/src
(nenhuma ocorrência fora de FabricaDeServicos.java)
```

### 7.4 Capturas de tela

As telas do sistema não mudaram visualmente nesta etapa (o trabalho foi inteiramente de reorganização interna de código — nenhum `initComponents()`/`.form` foi reescrito). As capturas já existentes em `docs/screenshots/` (referenciadas no `README.md`) continuam válidas como evidência visual do funcionamento do sistema.

<!-- INSERIR PRINT: execução de "docker compose up -d" mostrando o container barberdesk-mysql saudável -->
<!-- INSERIR PRINT: execução de VerificacaoSistema num terminal do usuário (fora deste ambiente de desenvolvimento), confirmando o mesmo resultado em uma máquina real -->

---

## 8. Conclusão

Ao final da Etapa 6, o BarberDesk desktop continua funcionando exatamente como antes (comportamento validado por 40 testes automatizados e por uma verificação de ponta a ponta contra um MySQL real), mas agora com:

- Regras de negócio isoladas em `barbershop-core`, sem qualquer dependência de Swing, prontas para serem reaproveitadas por uma camada Web na próxima etapa.
- Acesso a dados só por interface (Repository Pattern), com injeção manual por construtor.
- UI Swing organizada em controllers de responsabilidade única, em vez de uma única classe concentrando tudo.
- Cobertura de testes ampliada, usando *fakes* em memória para não depender de um banco real na esteira de CI/desenvolvimento.
- Um smoke test operacional (`VerificacaoSistema`) para validar rapidamente, contra um banco real, que nada quebrou depois de um deploy ou de uma migração de schema.

Consulte também [`README.md`](../README.md) para a documentação de uso, arquitetura e instruções de execução atualizadas.
