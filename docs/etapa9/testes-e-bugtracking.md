# 📋 Barbershop - Evidências de Testes e Bugtracking (Etapa 9)

**Projeto Integrador II - Etapa 9: Back-end Java Web Spring REST, Integração e Qualidade de Software**  
**Autor:** Lucas Hochmann Rosa  
**Repositório:** [https://github.com/lucas-hochmann-rosa/barber-shop-suite](https://github.com/lucas-hochmann-rosa/barber-shop-suite)  
**Data:** 16/08/2026  

---

## 1. Introdução e Escopo dos Testes

Nesta Etapa 9 do Projeto Integrador II, o sistema **Barbershop** concluiu a implementação do seu back-end Java Web baseado em **Spring Boot REST** (`api`), integrando a base de regras de negócio desacoplada (`core`), a interface gráfica desktop Java Swing (`desktop`) e o front-end web dinâmico (`web`).

Para garantir a confiabilidade, estabilidade e conformidade com os Requisitos Funcionais (RF01 a RF11), foi executada uma bateria abrangente de testes em múltiplos níveis:
- **Testes Unitários e de Domínio (JUnit 5):** Validação de regras de negócio, serviços, algoritmos de cálculo, validação de sobreposição de horário (RF10) e classificação visual de agendamentos (RF11).
- **Testes de Integração e Controladores REST (Spring MockMvc):** Validação dos contratos da API HTTP, códigos de status HTTP (200, 201, 400, 401, 409, 404), serialização JSON (Jackson JSR-310) e tratamento centralizado de exceções (`GlobalExceptionHandler`).
- **Testes de Paridade Front-end (Runner HTML/JS):** Validação da paridade de execução das regras de classificação da agenda entre o núcleo Java e o front-end web (`verificacao-classificacao.html`).
- **Smoke Test Operacional Desktop:** Execução de diagnóstico ponta a ponta via `VerificacaoSistema`.

---

## 2. Resumo da Execução dos Testes Automatizados

A suíte automatizada de testes do monorepo é executada através do Maven Surefire Plugin.

### 2.1 Resultado Geral da Suíte

```text
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for barber-shop-suite 1.0-SNAPSHOT:
[INFO] 
[INFO] barber-shop-suite .................................. SUCCESS [  0.142 s]
[INFO] barber-shop-core ................................... SUCCESS [  3.016 s]
[INFO] barber-shop-desktop ................................ SUCCESS [  0.810 s]
[INFO] barber-shop-api .................................... SUCCESS [  2.635 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.858 s
[INFO] ------------------------------------------------------------------------
```

- **Total de Módulos:** 4 (`core`, `desktop`, `web`, `api`)
- **Total de Testes Unitários e de Integração Executados:** 66 testes automatizados
- **Sucessos:** 66 (100%)
- **Falhas / Erros:** 0 (0%)
- **Taxa de Sucesso:** 100%

---

## 3. Matriz Detalhada de Testes de Unidade e Integração

### 3.1 Módulo `core` (52 Testes JUnit 5)

| Classe de Teste | Quantidade | Foco / Requisito Avaliado | Resultado |
| :--- | :---: | :--- | :---: |
| `AgendaServiceTest` | 13 | Criação, cancelamento, alteração de status e validação de sobreposição real de horários (RF10). | APROVADO |
| `ClassificadorAgendaTest` | 12 | Classificação temporal da agenda conforme RF11 (atrasado, em atendimento, próximo, futuro, concluído, cancelado). | APROVADO |
| `AuthServiceTest` | 5 | Autenticação com credenciais válidas/inválidas, hash SHA-256 e migração progressiva de salt. | APROVADO |
| `DateTimeUtilTest` | 7 | Formatação, parsing ISO, manipulação de datas e horários de agendamento. | APROVADO |
| `HashUtilTest` | 5 | Geração de salt criptográfico e hashing unidirecional SHA-256. | APROVADO |
| `BarbeiroTest` | 3 | Criação e validação de atributos da entidade Barbeiro. | APROVADO |
| `ServicoTest` | 4 | Criação, duração padrão e validação de preço da entidade Serviço. | APROVADO |
| `RelatorioServiceTest` | 3 | Consolidação de faturamento e rankings gerenciais (RF09). | APROVADO |

### 3.2 Módulo `api` (14 Testes MockMvc REST)

| Classe de Teste | Quantidade | Endpoint / Funcionalidade Avaliada | Status Esperado | Resultado |
| :--- | :---: | :--- | :---: | :---: |
| `AuthControllerTest` | 3 | `POST /api/auth/login` (sucesso), `POST /api/auth/login` (credenciais inválidas), `GET /api/auth/session`. | 200 / 401 | APROVADO |
| `AgendaControllerTest` | 3 | `GET /api/agenda/hoje`, `POST /api/agenda` (criação bem-sucedida), `POST /api/agenda` (conflito de horário RF10). | 200 / 201 / 409 | APROVADO |
| `BarbeariaControllerTest` | 2 | `GET /api/barbearia` (consulta dos dados cadastrais), `PUT /api/barbearia` (atualização cadastral). | 200 | APROVADO |
| `CatalogoControllerTest` | 4 | `GET /api/servicos`, `POST /api/servicos`, `GET /api/barbeiros`, `DELETE /api/servicos/{id}`. | 200 / 201 | APROVADO |
| `HistoricoControllerTest` | 1 | `GET /api/historico` (consulta com filtros de status, período e profissional). | 200 | APROVADO |
| `RelatorioControllerTest` | 1 | `GET /api/relatorios` (cálculo de faturamento e agregação de serviços/barbeiros mais produtivos). | 200 | APROVADO |

### 3.3 Módulo `web` (12 Cenários no Runner de Paridade RF11)

Executado através de `web/verificacao-classificacao.html`:

| ID Cenário | Agendamento | Hora Ref. | Classificação Esperada | Cor do Marcador | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| RF11-01 | 14:00 (AGENDADO) | 14:15 | `ATRASADO` | Vermelho (`#dc2626`) | APROVADO |
| RF11-02 | 14:00 (EM_ATENDIMENTO) | 14:10 | `EM_ATENDIMENTO` | Azul (`#2563eb`) | APROVADO |
| RF11-03 | 14:00 (AGENDADO) | 13:45 | `PROXIMO` (<= 30 min) | Amarelo (`#d97706`) | APROVADO |
| RF11-04 | 14:00 (AGENDADO) | 13:30 | `PROXIMO` (limiar exato 30 min) | Amarelo (`#d97706`) | APROVADO |
| RF11-05 | 14:00 (AGENDADO) | 12:00 | `FUTURO` (> 30 min) | Verde (`#16a34a`) | APROVADO |
| RF11-06 | 14:00 (CONCLUIDO) | 15:00 | `CONCLUIDO` | Cinza (`#6b7280`) | APROVADO |
| RF11-07 | 14:00 (CANCELADO) | 15:00 | `CANCELADO` | Cinza claro (`#9ca3af`)| APROVADO |
| RF11-08 a 12| Casos de borda (virada de hora, tolerâncias) | Variados | Conforme especificação | Conforme tabela | APROVADO |

---

## 4. Registro de Bugtracking (Defeitos Encontrados e Corrigidos)

Durante os ciclos de desenvolvimento, refatoração e integração dos módulos, as falhas identificadas foram documentadas, analisadas e resolvidas conforme a matriz de bugtracking a seguir:

```
+----------------------------------------------------------------------------------------------------+
|                                    MATRIZ DE BUGTRACKING                                          |
+--------+----------+-------------------------------------------------------------+-----------------+
| ID     | SEV.     | RESUMO DO DEFEITO                                           | STATUS          |
+--------+----------+-------------------------------------------------------------+-----------------+
| BUG-01 | CRÍTICA  | Sobreposição de agendamento ignorando duração do serviço    | RESOLVIDO/FECH. |
| BUG-02 | ALTA     | Falha de integridade ao excluir serviço com histórico      | RESOLVIDO/FECH. |
| BUG-03 | ALTA     | Missing parameter names em endpoints Spring MVC 6           | RESOLVIDO/FECH. |
| BUG-04 | MÉDIA    | Serialização Jackson de tipos Java 8 Date/Time em DTOs      | RESOLVIDO/FECH. |
| BUG-05 | ALTA     | Divergência na classificação RF11 entre front-end e core    | RESOLVIDO/FECH. |
| BUG-06 | MÉDIA    | Exposição de números de telefone reais em dados de exemplo  | RESOLVIDO/FECH. |
| BUG-07 | CRÍTICA  | Incompatibilidade de hash de senha pré e pós migração salt  | RESOLVIDO/FECH. |
| BUG-08 | BAIXA    | Roteamento estático Spring MVC após renomeação de diretórios| RESOLVIDO/FECH. |
+--------+----------+-------------------------------------------------------------+-----------------+
```

### Detalhamento das Ocorrências de Bugtracking

#### [BUG-01] Sobreposição de agendamento ignorando a duração real do serviço (RF10)
- **Módulo:** `core` / `api`
- **Severidade:** Crítica
- **Descrição:** O sistema anteriormente verificava duplicidade apenas checando a exata mesma hora de início de agendamento. Se um cliente agendasse um Combo de 60 minutos às 14:00 e outro cliente agendasse um Corte às 14:30 com o mesmo profissional, o sistema permitia a colisão de horários.
- **Causa Raiz:** Ausência de cálculo intervalar (`inicioA < fimB && fimA > inicioB`) considerando o campo `duracao_minutos` do serviço.
- **Correção Aplicada:** Implementação do método `verificarConflito(barbeiroId, dataHora, duracaoMinutos)` em `AgendamentoDAO` e `AgendaService`, considerando a soma da duração de cada agendamento e retornando HTTP 409 Conflict na API.
- **Status:** Fechado (Validado por 13 testes unitários).

#### [BUG-02] Violação de chave estrangeira ao excluir serviço ou barbeiro com histórico
- **Módulo:** `core` / Banco de Dados
- **Severidade:** Alta
- **Descrição:** Ao tentar excluir um serviço ou barbeiro no menu "Minha Barbearia", o banco disparava erro de Foreign Key Constraint porque existiam registros na tabela `agendamentos`.
- **Causa Raiz:** Dependência direta de chave estrangeira rígida sem campos de snapshot histórico.
- **Correção Aplicada:** Migração de banco (V3/V4) adicionando as colunas `servico_nome`, `barbeiro_nome` e `duracao_minutos` como snapshot histórico imutável na tabela `agendamentos`, desvinculando o histórico da obrigatoriedade do registro ativo.
- **Status:** Fechado (Validado em testes manuais e de unidade).

#### [BUG-03] Argument Name not available via reflection em controladores Spring REST
- **Módulo:** `api`
- **Severidade:** Alta
- **Descrição:** Ao realizar requisições GET em endpoints com `@RequestParam` e `@PathVariable`, o Spring MVC lançava `IllegalArgumentException: Name for argument of type not specified, and parameter name information not available via reflection`.
- **Causa Raiz:** No Spring Framework 6+ / Spring Boot 3+, a descoberta automática de parâmetros de métodos via reflexão exige a flag `-parameters` no compilador Java ou declaração explícita do atributo `name`.
- **Correção Aplicada:** 
  1. Configuração de `<parameters>true</parameters>` no `maven-compiler-plugin` do `pom.xml`.
  2. Declaração explícita de `name = "..."` em todas as anotações `@RequestParam` e `@PathVariable` nos controladores REST.
- **Status:** Fechado (Validado por 14 testes MockMvc).

#### [BUG-04] Falha de serialização JSON de tipos `LocalDate` e `LocalTime` no Jackson
- **Módulo:** `api`
- **Severidade:** Média
- **Descrição:** O Spring MockMvc e a serialização REST lançavam `InvalidDefinitionException: Java 8 date/time type not supported by default` para os campos `horarioAbertura` e `dataFundacao`.
- **Causa Raiz:** Módulo `JavaTimeModule` do Jackson não estava registrado explicitamente no contexto isolado de testes standalone.
- **Correção Aplicada:** Inclusão de `jackson-datatype-jsr310` e registro do `JavaTimeModule` e do `FormattingConversionService` no utilitário de testes `TestUtils`.
- **Status:** Fechado (Validado em testes de API).

#### [BUG-05] Divergência na classificação visual da agenda (RF11) no front-end web
- **Módulo:** `web` / `core`
- **Severidade:** Alta
- **Descrição:** O front-end web classificava agendamentos como "próximos" se estivessem dentro de 45 minutos, enquanto o núcleo Java classificava com limite estrito de 30 minutos.
- **Causa Raiz:** Implementação paralela com regra de negócio escrita de forma divergente em JavaScript.
- **Correção Aplicada:** Reescreveu-se `web/js/classificacao.js` espelhando rigorosamente as constantes e a lógica de `ClassificadorAgenda.java`. Criou-se a página `verificacao-classificacao.html` que executa automaticamente os 12 casos de teste de classificação no navegador.
- **Status:** Fechado (100% de paridade validada).

#### [BUG-06] Números de telefone e dados de contato reais em sementes de exemplo
- **Módulo:** `core` / `web` / `docs`
- **Severidade:** Média
- **Descrição:** Havia números de telefone de terceiros nos scripts de seed e nos dados de exemplo da interface.
- **Causa Raiz:** Dados de teste herdados de protótipos iniciais sem sanitização.
- **Correção Aplicada:** Substituição de todos os números de telefone do projeto por números fictícios com DDD 49 no formato `(49) 99999-9901` a `(49) 99999-9999` e `49999999999`.
- **Status:** Fechado (Conformidade com privacidade garantida).

#### [BUG-07] Falha de autenticação ao validar usuários criados antes da migração de salt
- **Módulo:** `core`
- **Severidade:** Crítica
- **Descrição:** Contas de usuários criadas na versão inicial sem coluna `salt` não conseguiam efetuar login após a implementação do algoritmo de hash com salt.
- **Causa Raiz:** `AuthService` tentava validar obrigatoriamente com salt, causando falha em hashes legados.
- **Correção Aplicada:** Implementação de migração transparente em `AuthService.autenticar()`: caso o salt seja nulo, o sistema valida contra o hash legado simples e, em caso de sucesso, gera um novo salt e re-criptografa a senha automaticamente no banco.
- **Status:** Fechado (Validado por `AuthServiceTest`).

#### [BUG-08] Mapeamento de recursos estáticos após renomeação de diretórios
- **Módulo:** `api`
- **Severidade:** Baixa
- **Descrição:** O `WebMvcConfig` procurava os arquivos estáticos na pasta `barber-shop-web`, falhando ao servir o front-end após a renomeação para `web/`.
- **Causa Raiz:** Caminho relativo estático no código de configuração do Spring.
- **Correção Aplicada:** Atualização do `WebMvcConfig` para verificar dinamicamente as pastas `../web` e `web`, garantindo a entrega do front-end independente do diretório de inicialização da API.
- **Status:** Fechado (Validado com execução integrada).

---

## 5. Conclusão da Qualidade

A execução do plano de testes e a disciplina no registro e correção dos defeitos através do bugtracking asseguram que o sistema **Barbershop** atende com excelência a todos os requisitos de negócio, segurança, integridade de dados e arquitetura da Etapa 9 do Projeto Integrador II.
