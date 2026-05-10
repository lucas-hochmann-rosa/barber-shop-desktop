# Roadmap — BarberDesk

Plano de melhorias para deixar o projeto com qualidade de portfólio: correções reais, arquitetura, funcionalidades novas e polimento visual. Itens organizados por prioridade, não por ordem cronológica obrigatória.

Convenção: cada item referencia o(s) arquivo(s) afetado(s) para facilitar retomar o trabalho depois.

---

## 🐞 Tier 1 — Correções (bugs reais, não só estilo)

- [x] **Caminho de imagem absoluto não é portável.** `util/ImageStorageUtil.java` copia a imagem escolhida no `JFileChooser` para `<user.home>/.barberdesk/images/` com nome único, usado em `DialogBarbeiro`/`DialogServico`.
- [x] **`TelaNovoAgendamento` casava serviço/barbeiro por índice de combo contra uma nova query.** Combos tipados (`JComboBox<Servico>`, `JComboBox<Barbeiro>`), mesmo padrão de `TelaEditarAgendamento`.
- [x] **Conflito de horário usava 30 minutos fixos.** `servicos.duracao_minutos` + `agendamentos.duracao_minutos_snapshot`; `AgendamentoDAO.verificarConflito` calcula sobreposição real de intervalo.
- [x] **Reflection para achar item selecionado em combo.** `Servico`/`Barbeiro` ganharam `equals()`/`hashCode()` por `id`.

## 🔐 Tier 2 — Segurança e confiabilidade

- [x] **Hash de senha com salt** (PBKDF2WithHmacSHA256, 120k iterações) em `HashUtil`. Contas antigas (SHA-256 sem salt) recebem upgrade silencioso no próximo login (`AuthService.autenticar`).
- [x] **Pool de conexões** (HikariCP 4.0.3) em `ConexaoMySQL` — contrato de `getConexao()` não mudou, `close()` devolve pro pool.
- [x] **Logging estruturado** (SLF4J + Logback) no lugar de `printStackTrace()`. Grava em `~/.barberdesk/logs/` (rotação diária, 14 dias).
- [x] **Validações de negócio**: preço > 0 em `DialogServico`; nome duplicado de serviço/barbeiro bloqueado (`ServicoDAO`/`BarbeiroDAO.existePorNome`).

## 🏗️ Tier 3 — Arquitetura

- [x] **Serviços de negócio finalmente conectados.** `AgendaService` (transições de status) e `SetupService.criarCadastroInicial` existiam no código mas nenhuma tela os chamava — `TelaHome`, `TelaEditarAgendamento` e `TelaCadastroInicial` reimplementavam a mesma lógica direto nos handlers. Agora usam os services.
- [ ] **Separação completa UI/negócio** em `TelaHome.java` (~1000 linhas) e `TelaCadastroInicial.java` — ainda misturam bastante acesso a dados com código Swing gerado. O que dava pra extrair com risco baixo (transições de status, cadastro inicial) já foi feito; o resto (grids, tabelas, todos os handlers de CRUD de serviço/barbeiro) é um refactor maior, que merece sua própria sessão dedicada com possibilidade de teste visual.
- [ ] **Flyway** no lugar do `ALTER TABLE ... try/catch` manual em `DatabaseInitService`. Adiado de propósito: trocar a estratégia de migração exige testar contra um MySQL de verdade (criação da tabela de histórico do Flyway, baseline em banco já existente) — não dá pra validar isso só compilando, e o esquema atual (V2 a V7) está funcionando e é auditável.
- [x] **Testes automatizados** — JUnit 5 para lógica pura (`HashUtil`, `DateTimeUtil`, `equals()`/`hashCode()` de `Servico`/`Barbeiro`). 19 testes, rodados localmente com `junit-platform-console-standalone` (sem Maven neste ambiente).
- [ ] Testes de integração dos DAOs contra MySQL real (Testcontainers) — não implementado; exigiria Docker, que não está disponível neste ambiente pra sequer tentar.

## ✨ Tier 4 — Funcionalidades novas

- [x] **Entidade Cliente**: diretório (`clientes`) populado automaticamente ao criar agendamento (`ClienteDAO.registrar`), com aba de busca em "Minha Barbearia". `agendamentos.cliente_nome/contato` continuam como estavam (texto livre) — não foi uma migração de FK arriscada, é um índice de consulta por cima.
- [x] **Horário de funcionamento** configurável por barbearia (`barbearias.horario_abertura/fechamento`, nulo = sem restrição), validado em `AgendaService.dentroDoHorarioFuncionamento` nas telas de novo/editar agendamento. Não adicionado em `TelaCadastroInicial` (usa GroupLayout, mais arriscado sem preview visual) — dá pra configurar depois em Minha Barbearia.
- [x] **Dashboard/relatórios**: faturamento por período, serviços mais vendidos, ranking de barbeiros (`RelatorioService`, tela "Relatórios"). Limitação conhecida: faturamento usa o preço atual do serviço, não há snapshot de preço no agendamento.
- [ ] **Papéis de usuário** (admin vs. barbeiro): não implementado. É o item mais estrutural que sobrou — hoje `Usuario` e `Barbeiro` são entidades sem relação nenhuma entre si (um login não corresponde a "ser" um barbeiro específico). Fazer isso direito exige repensar o modelo de sessão/login, não só adicionar uma coluna `role`. Fica pra uma sessão dedicada.
- [x] **Motivo de cancelamento** — `agendamentos.motivo_cancelamento`, capturado via prompt ao cancelar (`TelaHome` e novo botão em `TelaEditarAgendamento`), centralizado em `AgendaService.cancelarAgendamento`.
- [x] **Busca/filtro** no Histórico (`TableRowSorter` + `RowFilter`) e agora também em Clientes. Não adicionado na lista de "Agendamentos Pendentes" da Home (lista naturalmente pequena, só `AGENDADO`/`EM_ATENDIMENTO`). Paginação não implementada — o volume atual (filtrado por texto) não justificou a complexidade extra ainda.
- [x] Link direto pro WhatsApp do cliente (`UIUtil.abrirWhatsApp`) em `TelaEditarAgendamento`, habilitado só quando o contato parece um telefone de verdade.
- [x] **RF11** (classificação visual de agendamentos por status/proximidade) — `TelaHome.StatusRowRenderer` colore linhas de `tblAgendamentos`/`tblHistorico`.

## 🎨 Tier 5 — Visual (alto impacto, baixo risco)

- [x] **FlatLaf** (Look & Feel MIT, versão 2.6) ativado em `Main.java`.
- [x] Máscaras de input de data/hora (`JFormattedTextField` + `MaskFormatter`, `UIUtil.criarCampoMascarado`). Máscara de telefone ficou de fora de propósito (contato pode ser Instagram/WhatsApp, não só número).
- [x] Ordenação (`TableRowSorter`) em todas as tabelas de `TelaHome` (agendamentos, histórico, serviços, barbeiros, clientes, relatórios).
- [x] Reuso de `DateTimeUtil` em vez de `DateTimeFormatter` duplicado em cada tela.
- [x] Preço formatado como moeda BR (`NumberFormat.getCurrencyInstance`).

## 🛠️ Tier 6 — DevOps/portfólio

- [x] `docker-compose.yml` com MySQL 8, já configurado pros defaults de `config.properties` (schema `barberdesk`, root sem senha).
- [x] `.gitattributes` normalizando quebra de linha pra LF.
- [x] CI (`.github/workflows/build.yml`) rodando `mvn test` (compila + roda a suíte JUnit) em todo push/PR.
- [ ] **Instalador nativo via `jpackage`**: não implementado. `jpackage` precisa do jar já empacotado pelo `maven-shade-plugin` como entrada, e este ambiente não tem Maven — não dá pra gerar esse jar nem testar o instalador resultante, então não faz sentido adicionar configuração de plugin que eu não consigo verificar. Pra fazer isso depois, com Maven disponível: `mvn clean package` e então `jpackage --input target --main-jar BarberDesk-1.0-SNAPSHOT.jar --main-class br.com.barberdesk.app.Main --type exe --name BarberDesk` (ajustar ícone/versão conforme necessário).

---

## Status de execução

- **Concluído e compilando** (`javac`, verificado a cada leva): Tiers 1, 2, 5, 6 quase completo, e a maior parte do Tier 3/4.
- **Verificado de verdade** (não só compilado): os 19 testes JUnit rodam e passam (`junit-platform-console-standalone`, já que não há Maven neste ambiente).
- **Ainda não testado contra um MySQL/GUI real**: nada disso rodou contra um banco de verdade nem foi visto na tela. Antes de considerar 100% pronto, meça isso na prática:
  - `docker compose up -d` + `mvn clean package` + `java -jar target/BarberDesk-1.0-SNAPSHOT.jar`;
  - conferir visualmente os layouts editados à mão sem NetBeans (campo de duração em `DialogServico`, horário de funcionamento e aba Clientes em `TelaHome`, tela de Relatórios);
  - testar o fluxo de conflito de horário com serviços de duração diferente;
  - testar cancelamento com motivo, link do WhatsApp, filtro de histórico/clientes, e a geração de relatório com dados reais;
  - confirmar que uma conta criada antes desta leva ainda loga normalmente (upgrade de hash silencioso).
- **Deliberadamente não implementado nesta leva**, com o motivo registrado ao lado de cada item acima: separação completa de UI/negócio em `TelaHome`/`TelaCadastroInicial`, Flyway, testes de integração com Testcontainers, papéis de usuário, instalador via `jpackage`.

Cada item novo, ao ser implementado, deve ser validado por compilação e, sempre que envolver banco ou UI, testado manualmente contra um MySQL real antes de ser considerado concluído.
