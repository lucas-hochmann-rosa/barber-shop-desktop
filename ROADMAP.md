# Roadmap — BarberDesk

Plano de melhorias para deixar o projeto com qualidade de portfólio: correções reais, arquitetura, funcionalidades novas e polimento visual. Itens organizados por prioridade, não por ordem cronológica obrigatória.

Convenção: cada item referencia o(s) arquivo(s) afetado(s) para facilitar retomar o trabalho depois.

---

## 🐞 Tier 1 — Correções (bugs reais, não só estilo)

- [ ] **Caminho de imagem absoluto não é portável.** `DialogBarbeiro`/`DialogServico` salvam `chooser.getSelectedFile().getAbsolutePath()` direto no banco. Se o app mudar de máquina ou a pasta original for movida, as fotos somem. Corrigir copiando a imagem escolhida para uma pasta gerenciada pelo próprio app (ex.: diretório de dados do usuário) e salvando um caminho relativo/gerenciado.
- [ ] **`TelaNovoAgendamento` casa serviço/barbeiro por índice de combo contra uma nova query.** `servicoDAO.listarPorBarbearia(bId).get(cbServico.getSelectedIndex())` (linha ~176) reconsulta o banco no momento de salvar; se a lista mudar entre abrir a tela e salvar, o índice pode não corresponder mais ao item exibido. `TelaEditarAgendamento` já resolve isso certo com `JComboBox<Servico>` tipado — replicar o mesmo padrão.
- [ ] **Conflito de horário usa 30 minutos fixos**, mas `Servico` não tem duração — um corte simples e um combo (corte + barba) ocupam tempos diferentes na prática. Adicionar `duracao_minutos` em `servicos` e usar a duração real do serviço agendado em `AgendamentoDAO.verificarConflito`.
- [ ] **Reflection para achar item selecionado em combo.** `TelaEditarAgendamento.selecionarComboPorId` usa `item.getClass().getMethod("getId").invoke(item)` para contornar a ausência de `equals()/hashCode()` nos models. Implementar `equals()/hashCode()` por `id` em `Servico`, `Barbeiro` (e outros models de entidade) resolve isso sem reflection.

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

- [ ] **FlatLaf** (Look & Feel MIT) no lugar do Look & Feel padrão do Swing — troca pequena no `Main`, visual moderno imediato.
- [ ] Máscaras de input (data/hora com `JFormattedTextField`, telefone com máscara) no lugar de texto livre validado só na hora de salvar.
- [ ] Ordenação/filtro nas tabelas (`TableRowSorter`) em `TelaHome`.
- [ ] Reusar `DateTimeUtil` (já existe em `util/`) em vez de cada tela declarar seu próprio `DateTimeFormatter` — hoje `TelaNovoAgendamento` e `TelaEditarAgendamento` duplicam isso.
- [ ] Formatação de moeda `R$ 0,00` (`NumberFormat.getCurrencyInstance`) no lugar de `BigDecimal.toString()` cru.

## 🛠️ Tier 6 — DevOps/portfólio

- [ ] `docker-compose.yml` com MySQL para reduzir fricção de quem for rodar/avaliar o repo.
- [ ] Expandir `.github/workflows/build.yml` para rodar testes assim que existirem (Tier 3).
- [ ] Instalador nativo Windows via `jpackage` (gera `.exe`, não só o jar shaded).
- [ ] `.gitattributes` para normalizar quebra de linha (evita os avisos LF/CRLF do Git em commits recentes).

---

## Status de execução

- **Em andamento**: Tier 1 (correções) + Tier 5 (visual).
- **Planejado, não iniciado**: Tiers 2, 3, 4, 6.

Cada item, ao ser implementado, deve ser validado por compilação (`javac`/`mvn compile`) e, sempre que envolver banco ou UI, testado manualmente contra um MySQL real antes de ser considerado concluído.
