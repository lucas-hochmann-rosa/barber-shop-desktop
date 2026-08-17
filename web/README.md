# 💈 Barbershop Web - Front-end

Versão web do Barbershop, feita com **HTML, CSS e JavaScript puros**: sem framework pesado e sem etapa de build. Comunica-se diretamente com a API Spring Boot REST (`api`) via `js/api.js`, mantendo fallback automático para dados locais em memória (`js/dados.js`). É a camada de apresentação que consome as mesmas regras de negócio do módulo `core`.

---

## Como abrir

Os arquivos são estáticos, então abrir `index.html` direto no navegador funciona. Mesmo assim, prefira servir por HTTP ou através da API Spring REST:

```bash
cd web
python -m http.server 8000
```

E acesse <http://localhost:8000>.

**Acesso de demonstração:** usuário `lucas`, senha `1234`.

---

## Estrutura

```text
web/
├── index.html                 Entrar (RF02)
├── agenda.html                Tela principal: régua do dia, resumo, serviços e pendentes (RF08, RF11)
├── agendamento.html           Novo agendamento / edição (RF05, RF06, RF10)
├── barbearia.html             Dados, serviços e barbeiros, em abas (RF03, RF04)
├── historico.html             Listagem completa com filtros (RF09)
├── relatorios.html            Faturamento, mais vendidos e ranking (RF09)
├── verificacao-classificacao.html Confere a regra RF11 contra os casos do teste JUnit
├── css/
│   ├── base.css               Reset, variáveis, tipografia e utilitários
│   ├── layout.css             Barra lateral, topo e grades das páginas
│   ├── componentes.css        Botões, campos, tabelas, cartões, selos, modal e régua
│   └── paginas.css            Regras específicas de cada tela
├── js/
│   ├── api.js                 Cliente HTTP REST assíncrono para comunicação com a API
│   ├── dados.js               Dados de exemplo em memória (fallback local)
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

É o elemento central da tela de agenda: uma faixa horizontal que representa o expediente (08h às 20h), com:

- cada agendamento posicionado proporcionalmente pelo seu horário;
- um marcador vertical na hora atual, que anda sozinho de minuto em minuto;
- cada bloco colorido pela classificação do RF11;
- um balão, ao passar o mouse ou navegar por teclado, com cliente, serviço e horário.

Os blocos são botões de verdade, alcançáveis por `Tab` e com `aria-label` descrevendo horário, cliente, serviço e classificação.

---

## A regra do RF11 é a mesma do core Java

`js/classificacao.js` é a tradução direta da classe Java `ClassificadorAgenda` (`core/src/main/java/br/com/barbershop/service/ClassificadorAgenda.java`), mantendo os mesmos nomes de classificação, a mesma ordem de decisão e as mesmas fronteiras. Assim como no Java, a data de referência entra por parâmetro em vez de ser lida do relógio dentro da função.

A página [**`verificacao-classificacao.html`**](verificacao-classificacao.html) roda no navegador os mesmos 12 casos do teste JUnit `ClassificadorAgendaTest`, com a mesma data de referência fixa, e mostra o resultado numa tabela com 100% de aprovação.

| Classificação | Quando | Cor |
| --- | --- | --- |
| `EM_ATENDIMENTO` | status em atendimento | azul |
| `ATRASADO` | agendado e o horário já passou | vermelho |
| `PROXIMO` | começa em até 30 min | amarelo |
| `FUTURO` | começa em mais de 30 min | verde |
| `CONCLUIDO` | atendimento concluído | cinza |
| `CANCELADO` | agendamento cancelado | cinza claro, riscado |

---

## Direção visual

O vocabulário vem da barbearia física: azulejo, couro, latão, cadeira. As superfícies são separadas por borda de 1px, e sombra só aparece em elemento flutuante (o modal).

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

**Tipografia:** Barlow Condensed (600/700) em caixa alta para títulos e rótulos; IBM Plex Sans (400/600) para a interface; IBM Plex Mono (500) para horários, valores e datas.

---

## Acessibilidade e responsividade

- HTML5 semântico: `nav`, `main`, `section`, tabelas com `thead` e `th scope`, `caption` explicando cada tabela.
- Todo campo tem `label` associado; erros aparecem ao lado do campo, em elementos com `aria-live`.
- Foco sempre visível; o modal prende o foco enquanto aberto e devolve ao elemento anterior ao fechar; `Esc` fecha.
- Link "pular para o conteúdo" no começo de cada página.
- `prefers-reduced-motion` respeitado.
- Responsivo até 380px: a barra lateral vira gaveta com véu, as tabelas rolam na horizontal dentro do próprio quadro e as grades passam a uma coluna.

---

## Cobertura dos requisitos

| Requisito | Onde |
| --- | --- |
| RF02 - autenticação | `index.html` e `/api/auth/login` |
| RF03 - serviços | `barbearia.html` e `/api/servicos` |
| RF04 - barbeiros | `barbearia.html` e `/api/barbeiros` |
| RF05 - novo agendamento | `agendamento.html` e `POST /api/agenda` |
| RF06 - editar e excluir | `agendamento.html` e `PUT / DELETE /api/agenda` |
| RF07 - iniciar e concluir | `agenda.html` e `/api/agenda/{id}/iniciar` / `/concluir` |
| RF08 - só pendentes na Home | `agenda.html` e `/api/agenda/hoje` |
| RF09 - histórico e relatórios | `historico.html`, `relatorios.html` e `/api/historico`, `/api/relatorios` |
| RF10 - conflito de horário | `agendamento.html` e `/api/agenda/conflito` |
| RF11 - classificação visual | régua e tabela via `js/classificacao.js` e `AgendamentoDTO` |
