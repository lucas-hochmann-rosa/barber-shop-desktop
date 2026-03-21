# Roadmap — BarberDesk

Plano de melhorias para deixar o projeto com qualidade de portfólio: correções reais, arquitetura, funcionalidades novas e polimento visual. Itens organizados por prioridade, não por ordem cronológica obrigatória.

Convenção: cada item referencia o(s) arquivo(s) afetado(s) para facilitar retomar o trabalho depois.

---

## 🐞 Tier 1 — Correções (bugs reais, não só estilo)

- [x] **Caminho de imagem absoluto não é portável.** Resolvido com `util/ImageStorageUtil.java`: copia a imagem escolhida no `JFileChooser` para `<user.home>/.barberdesk/images/` com nome único, usado em `DialogBarbeiro`/`DialogServico`. Imagens cadastradas antes desta mudança continuam usando o caminho antigo.
- [x] **`TelaNovoAgendamento` casava serviço/barbeiro por índice de combo contra uma nova query.** Combos agora são tipados (`JComboBox<Servico>`, `JComboBox<Barbeiro>`), mesmo padrão de `TelaEditarAgendamento`.
- [x] **Conflito de horário usava 30 minutos fixos.** `servicos.duracao_minutos` + `agendamentos.duracao_minutos_snapshot` (migração V3 em `DatabaseInitService`); `AgendamentoDAO.verificarConflito` agora calcula sobreposição real de intervalo. Campo de duração adicionado em `DialogServico`.
- [x] **Reflection para achar item selecionado em combo.** `Servico`/`Barbeiro` ganharam `equals()`/`hashCode()` por `id`; `TelaEditarAgendamento.selecionarComboPorId` não usa mais reflection.

## 🔐 Tier 2 — Segurança e confiabilidade

- [ ] Hash de senha com salt (BCrypt ou PBKDF2) no lugar do SHA-256 puro de `HashUtil` — exige migração de schema (nova coluna de salt) e reautenticação.
- [ ] Pool de conexões (HikariCP) no lugar de uma conexão nova por chamada de DAO (`ConexaoMySQL.getConexao`).
- [ ] Logging estruturado (SLF4J + Logback) no lugar de `e.printStackTrace()` espalhado (`Main.java`, `TelaNovoAgendamento`, outras telas) — hoje, se o app quebra em uso real, não há registro além do console.
- [ ] Validações de negócio que faltam: preço não pode ser negativo em `DialogServico`, checar nome duplicado de barbeiro/serviço.

## 🏗️ Tier 3 — Arquitetura

- [ ] Separar lógica de negócio da UI: `TelaHome.java` (813 linhas) e `TelaCadastroInicial.java` (530 linhas) fazem acesso a dados direto nos handlers de evento. Extrair para a camada `service/`.
- [ ] Ferramenta de migração real (Flyway) no lugar do `ALTER TABLE` com `try/catch (SQLException ignored)` manual em `DatabaseInitService`.
- [ ] Testes automatizados: JUnit 5 para regras de negócio puras, Testcontainers para os DAOs contra MySQL real.

## ✨ Tier 4 — Funcionalidades novas

- [ ] **Entidade Cliente** própria (hoje é só texto solto em cada agendamento): histórico por cliente, busca por nome/telefone, autocompletar ao criar novo agendamento.
- [ ] **Horário de funcionamento** da barbearia configurável, com validação ao agendar fora do expediente.
- [ ] **Dashboard/relatórios**: faturamento por período, serviços mais vendidos, ranking de barbeiros.
- [ ] **Papéis de usuário**: hoje só existe um admin; um barbeiro logado só veria a própria agenda.
- [ ] **Motivo de cancelamento** — status `CANCELADO` existe mas não captura o porquê.
- [ ] **Busca/filtro** na Home e no Histórico (cliente, status, período) + paginação — hoje carrega tudo de uma vez (`AgendamentoDAO.listarPorBarbearia`).
- [ ] Link direto pro WhatsApp do cliente (`wa.me/<contato>`) a partir da tela de agendamento.
- [ ] **RF11** (classificação visual de agendamentos por status/proximidade) — já documentado como pendente no README.

## 🎨 Tier 5 — Visual (alto impacto, baixo risco)

- [x] **FlatLaf** (Look & Feel MIT, versão 2.6 — última compatível com Java 8) ativado em `Main.java`.
- [x] Máscaras de input de data/hora (`JFormattedTextField` + `MaskFormatter`, `UIUtil.criarCampoMascarado`) em `TelaNovoAgendamento`/`TelaEditarAgendamento`. Máscara de telefone ficou de fora de propósito: `contato` aceita WhatsApp/Instagram/telefone (ver `OrigemContato`), uma máscara fixa quebraria os casos que não são número.
- [x] Ordenação (`TableRowSorter`) nas 4 tabelas de `TelaHome` (agendamentos, histórico, gerenciar serviços, gerenciar barbeiros). Filtro/busca continua pendente — ver Tier 4.
- [x] `TelaHome`, `TelaNovoAgendamento` e `TelaEditarAgendamento` passaram a usar `DateTimeUtil` em vez de declarar seu próprio `DateTimeFormatter`.
- [x] Preço formatado como moeda BR (`NumberFormat.getCurrencyInstance`) no grid de serviços de `TelaHome`.

## 🛠️ Tier 6 — DevOps/portfólio

- [ ] `docker-compose.yml` com MySQL para reduzir fricção de quem for rodar/avaliar o repo.
- [ ] Expandir `.github/workflows/build.yml` para rodar testes assim que existirem (Tier 3).
- [ ] Instalador nativo Windows via `jpackage` (gera `.exe`, não só o jar shaded).
- [ ] `.gitattributes` para normalizar quebra de linha (evita os avisos LF/CRLF do Git em commits recentes).

---

## Status de execução

- **Concluído**: Tier 1 (correções) + Tier 5 (visual) — implementado e compilando (`javac`), mas **ainda não testado contra um MySQL/GUI real**. Antes de considerar 100% pronto: rodar o app de verdade, confirmar visualmente o layout novo de `DialogServico` (campo de duração), o Look & Feel do FlatLaf, as máscaras de data/hora, e testar dois agendamentos com serviços de duração diferente pro mesmo barbeiro pra validar o conflito por sobreposição real.
- **Planejado, não iniciado**: Tiers 2, 3, 4, 6.

Cada item, ao ser implementado, deve ser validado por compilação (`javac`/`mvn compile`) e, sempre que envolver banco ou UI, testado manualmente contra um MySQL real antes de ser considerado concluído.
