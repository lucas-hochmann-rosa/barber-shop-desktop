# 💈 BarberDesk Web — front-end

Versão web do BarberDesk, feita com **HTML, CSS e JavaScript puros**: sem framework, sem build, sem back-end. Os dados vêm de um arquivo `.js` de exemplo. É a camada de apresentação que, numa etapa seguinte, será ligada a uma API — provavelmente consumindo o módulo `barbershop-core` do sistema desktop, que já não depende de Swing.

---

## Como abrir

Os arquivos são estáticos, então abrir `index.html` direto no navegador funciona. Mesmo assim, prefira servir por HTTP — é o mais parecido com o ambiente real e evita restrições do protocolo `file://`:

```bash
cd barbershop-web
python -m http.server 8000
```

E acesse <http://localhost:8000>.

**Acesso de demonstração:** usuário `lucas`, senha `1234`.

---

## Estrutura

```text
barbershop-web/
├── index.html                 Entrar (RF02)
├── agenda.html                Tela principal: régua do dia, resumo, serviços e pendentes (RF08, RF11)
├── agendamento.html           Novo agendamento / edição (RF05, RF06, RF10)
├── barbearia.html             Dados, serviços e barbeiros, em abas (RF03, RF04)
├── historico.html             Listagem completa com filtros (RF09)
├── relatorios.html            Faturamento, mais vendidos e ranking (RF09)
├── teste-classificacao.html   Confere a regra RF11 contra os casos do teste JUnit
├── css/
│   ├── base.css               Reset, variáveis, tipografia e utilitários
│   ├── layout.css             Barra lateral, topo e grades das páginas
│   ├── componentes.css        Botões, campos, tabelas, cartões, selos, modal e régua
│   └── paginas.css            Regras específicas de cada tela
├── js/
│   ├── dados.js               Dados de exemplo (barbearia, serviços, barbeiros, agendamentos)
│   ├── classificacao.js       Regra do RF11, portada do Java
│   ├── validacao.js           Validação de formulários
│   ├── app.js                 Navegação, menu recolhível, modal e formatação
│   ├── agenda.js              Régua do dia, cartões-resumo e tabela de pendentes
│   ├── agendamento.js         Formulário, conflito de horário e resumo
│   ├── barbearia.js           Abas, cartões e modal de cadastro
│   ├── historico.js           Filtros, ordenação e paginação
│   └── relatorios.js          Agregações e gráficos
└── img/                       SVGs próprios (logo, ilustração do login, serviços, avatares)
```

---

## A régua do dia

É o elemento central da tela de agenda: uma faixa horizontal que representa o expediente (08h às 20h), com

- cada agendamento posicionado proporcionalmente pelo seu horário;
- um marcador vertical na hora atual, que anda sozinho de minuto em minuto;
- cada bloco colorido pela classificação do RF11;
- um balão, ao passar o mouse ou navegar por teclado, com cliente, serviço e horário.

Os blocos são botões de verdade, alcançáveis por `Tab` e com `aria-label` descrevendo horário, cliente, serviço e classificação — quem usa leitor de tela recebe a mesma informação que o balão mostra.

---

## A regra do RF11 é a mesma do desktop

`js/classificacao.js` é a tradução direta da classe Java `ClassificadorAgenda`
(`barbershop-core/src/main/java/br/com/barberdesk/service/ClassificadorAgenda.java`), mantendo os mesmos nomes de classificação, a mesma ordem de decisão e as mesmas fronteiras (60 e 120 minutos). Assim como no Java, a data de referência entra por parâmetro em vez de ser lida do relógio dentro da função — é isso que torna a regra testável.

Para não ficar só na palavra, **`teste-classificacao.html` roda no navegador os mesmos 12 casos do teste JUnit `ClassificadorAgendaTest`** (o da Etapa 7), com a mesma data de referência fixa, e mostra o resultado numa tabela. Abra a página: os 12 passam.

| Classificação | Quando | Cor |
| --- | --- | --- |
| `EM_ANDAMENTO` | status em atendimento | verde-claro |
| `ATRASADO` | agendado e o horário já passou | oxblood |
| `IMINENTE` | começa em até 60 min | latão |
| `PROXIMO` | começa em até 120 min | fumaça |
| `DISTANTE` | começa em mais de 120 min | neblina |
| `CONCLUIDO` | atendimento concluído | verde-cadeira a 40% |
| `CANCELADO` | agendamento cancelado | neblina, com texto riscado |

---

## Direção visual

O vocabulário vem da barbearia física: azulejo, couro, latão, cadeira. Nada de gradiente nem de sombra difusa — as superfícies são separadas por borda de 1px, e sombra só aparece em elemento flutuante (o modal).

| Variável | Cor | Uso |
| --- | --- | --- |
| `--porcelana` | `#F2F5F3` | fundo |
| `--branco` | `#FFFFFF` | superfícies |
| `--verde-cadeira` | `#14483F` | barra lateral, títulos, botão primário |
| `--verde-claro` | `#2E7D6B` | estados ativos |
| `--oxblood` | `#8A3324` | couro, ações destrutivas |
| `--latao` | `#C8912F` | acento e destaque |
| `--tinta` | `#14201E` | texto |
| `--fumaca` | `#6B7A76` | texto secundário |
| `--neblina` | `#DCE3E0` | bordas |

**Tipografia:** Barlow Condensed (600/700) em caixa alta para títulos e rótulos, como letreiro esmaltado; IBM Plex Sans (400/600) para a interface; IBM Plex Mono (500) para horários, valores e datas, que são dados tabulares e alinham melhor em monoespaçada.

---

## Acessibilidade e responsividade

- HTML5 semântico: `nav`, `main`, `section`, tabelas com `thead` e `th scope`, `caption` explicando cada tabela.
- Todo campo tem `label` associado; erros aparecem ao lado do campo, em elementos com `aria-live` — **nenhum `alert()` no projeto**.
- Foco sempre visível; o modal prende o foco enquanto aberto e devolve ao elemento anterior ao fechar; `Esc` fecha.
- Link "pular para o conteúdo" no começo de cada página.
- `prefers-reduced-motion` respeitado.
- Responsivo até 380px: a barra lateral vira gaveta com véu, as tabelas rolam na horizontal dentro do próprio quadro e as grades passam a uma coluna. **Verificado: nenhuma das sete páginas estoura na horizontal a 380px.**

---

## Cobertura dos requisitos

| Requisito | Onde |
| --- | --- |
| RF02 — autenticação | `index.html` |
| RF03 — serviços | `barbearia.html` (aba Serviços) e grade da `agenda.html` |
| RF04 — barbeiros | `barbearia.html` (aba Barbeiros) |
| RF05 — novo agendamento | `agendamento.html` |
| RF06 — editar e excluir | `agendamento.html` (`?id=`) |
| RF07 — iniciar e concluir | `agenda.html`, ações na tabela de pendentes |
| RF08 — só pendentes na Home | `agenda.html` |
| RF09 — histórico e relatórios | `historico.html`, `relatorios.html` |
| RF10 — conflito de horário | `agendamento.html`, painel de verificação |
| RF11 — classificação visual | régua e faixa colorida da tabela, via `js/classificacao.js` |

RF01 (cadastro inicial da barbearia) não tem tela própria nesta etapa: os seis wireframes não previam uma, e a edição dos dados da barbearia está em `barbearia.html`, na aba Dados.

---

## Limitações conhecidas

- **Nada é persistido.** Iniciar um atendimento, salvar um agendamento ou cadastrar um serviço muda os dados apenas na memória daquela página; recarregar ou trocar de tela volta tudo ao estado inicial de `dados.js`. É esperado nesta etapa — a persistência entra junto com o back-end.
- Os agendamentos de hoje são montados **em relação ao horário atual**, e não em horas fixas, para que a régua e as faixas do RF11 sempre tenham o que mostrar, a qualquer hora em que a página seja aberta. O histórico dos últimos 60 dias é gerado com sorteio de semente fixa, então os relatórios dão sempre o mesmo resultado.
- A verificação de conflito (RF10) roda contra os dados de exemplo carregados na página, não contra um banco.

---

## Observação sobre a estrutura de arquivos

A especificação da etapa listava sete arquivos em `js/`. Foram usados nove: `agendamento.js` e `barbearia.js` foram acrescentados para que a lógica dessas duas telas não virasse um `<script>` gigante dentro do HTML, seguindo o mesmo padrão de `agenda.js`, `historico.js` e `relatorios.js`. A página `teste-classificacao.html` também é um acréscimo, para deixar registrada a paridade da regra RF11 entre desktop e web.
