# 🌿 Barbershop - Evidências de Versionamento e Governança Git (Etapa 9)

**Projeto Integrador II - Etapa 9: Back-end Java Web Spring REST, Integração e Versionamento**  
**Autor:** Lucas Hochmann Rosa  
**Repositório Oficial:** [https://github.com/lucas-hochmann-rosa/barber-shop-suite](https://github.com/lucas-hochmann-rosa/barber-shop-suite)  
**Data:** 16/08/2026  

---

## 1. Estratégia de Versionamento

O projeto **Barbershop** adota uma governança de controle de versão moderna utilizando o padrão **Git Flow** adaptado para monorepo modular em Java/Web. Todas as alterações são rastreadas, versionadas de forma atômica e vinculadas às entregas do Projeto Integrador II.

### 1.1 Modelo de Branches

- **`main`**: Ramo de produção e distribuição de versões estáveis do sistema. Recebe integrações consolidadas a cada etapa validada.
- **`develop`**: Ramo de integração contínua. Todo o desenvolvimento diário, implementação de features, refatorações e testes ocorrem neste ramo antes de serem promovidos para a `main`.
- **Branches de Funcionalidade / Fix**: Criadas a partir de `develop` para isolamento de mudanças específicas e mescladas via merge sem perdas de histórico.

```
       (v1.0.0-etapa8)                      (v1.0.0-etapa9)
main      o----------------------------------------o [release final]
         /                                        /
develop o---o---o---o---o---o---o---o---o---o----o [integração contínua]
            \          /         \          /
feature      o--------o           o--------o [módulos api/web/core]
```

### 1.2 Padrão de Mensagens de Commit (Conventional Commits)

Os commits seguem uma convenção semântica padronizada:
- `feat:` Adição de novas funcionalidades ou módulos (ex: endpoints REST, telas).
- `fix:` Correção de defeitos identificados via bugtracking ou testes.
- `refactor:` Reestruturação de código sem alteração no comportamento externo (ex: modularização).
- `test:` Criação ou ajuste de testes automatizados (unitários, MockMvc, runners).
- `docs:` Criação e atualização de documentação técnica, wireframes e manuais.
- `chore:` Tarefas de manutenção de build, dependências Maven ou scripts.

---

## 2. Estrutura do Repositório Monorepo

O repositório `barber-shop-suite` organiza a solução em quatro módulos coesos e desacoplados:

```
barber-shop-suite/
├── pom.xml                     # POM pai com controle centralizado de dependências e plugins
├── core/                       # Módulo Java Puro: Domínio, DAOs, Serviços, Migrações e Regras (RF01-RF11)
├── desktop/                    # Módulo Java Desktop: Interface gráfica Swing FlatLaf
├── web/                        # Módulo Web: Front-end HTML5, CSS3 modular e JavaScript puro + Cliente REST
├── api/                        # Módulo Java Web: Spring Boot REST API (Etapa 9)
├── docs/                       # Documentação técnica e evidências de entrega
│   ├── wireframes/             # Wireframes monocromáticos em SVG (desktop e web)
│   ├── screenshots/            # Evidências visuais de execução
│   └── etapa9/                 # Documentos de entrega da Etapa 9 (testes, bugtracking, versionamento)
└── docker-compose.yml          # Infraestrutura conteinerizada para banco de dados MySQL 8
```

---

## 3. Histórico de Versões e Entregas do Projeto Integrador

| Versão / Tag | Etapa do PI | Foco Principal da Entrega | Principais Realizações |
| :--- | :---: | :--- | :--- |
| `v1.0.0-etapa6` | Etapa 6 | Arquitetura e Separação em Camadas | Desacoplamento da camada de domínio/regras em módulo independente (`core`). |
| `v1.0.0-etapa7` | Etapa 7 | Plano de Testes e Qualidade | Criação da suíte de 52 testes automatizados JUnit no módulo `core`. |
| `v1.0.0-etapa8` | Etapa 8 | Front-end Web e Wireframes | Interface web completa, CSS modular, régua visual do dia e wireframes SVG. |
| `v1.0.0-etapa9` | Etapa 9 | Back-end Java Web Spring REST | Criação do módulo `api`, endpoints REST, integração com o front-end, 14 testes MockMvc e matriz de bugtracking. |

---

## 4. Evidências do Histórico de Commits (Log Rastreável)

Abaixo é apresentado o extrato representativo dos commits que demonstram a evolução do projeto:

```text
* commit a8f9c12 (HEAD -> develop, main)
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     feat: modulo java web spring rest (api), integracao front-end e documentacao da etapa 9
|     
|     - Criacao do modulo api com Spring Boot 3.2.5 REST (controllers, DTOs, configs)
|     - Implementacao de endpoints para auth, barbearia, servicos, barbeiros, agenda, historico e relatorios
|     - Integracao do front-end web com a API REST via js/api.js com fallback local
|     - Criacao de 14 testes de integracao com MockMvc cobrindo todos os endpoints
|     - Registro da matriz de testes e bugtracking em docs/etapa9/testes-e-bugtracking.md
|     - Registro da governanca de versionamento em docs/etapa9/evidencias-versionamento.md
|     - Reorganizacao da arquitetura de diretorios para api, core, desktop e web
| 
* commit 74b21d5
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     docs: organizar wireframes monocromaticos em docs/wireframes e ajustar comentarios
| 
* commit 3c5e89a
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     feat: padronizacao nominal do monorepo barber-shop-suite e remocao de termos legados
| 
* commit 19e4a7b
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     feat: front-end web completo com regua visual do dia e verificador de paridade RF11
| 
* commit d2a10b4
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     test: implementacao de 52 testes unitarios com JUnit 5 cobrindo regras de negocio
| 
* commit 81f7c32
| Author: Lucas Hochmann Rosa <lucas_h_rosa@users.noreply.github.com>
| Date:   Sun Aug 16 2026
| 
|     refactor: desacoplamento do nucleo de dominio em modulo core independente
```

---

## 5. Instruções para Clone, Navegação e Validação

Para clonar e inspecionar o histórico completo de versionamento:

```bash
# 1. Clonar o repositório oficial
git clone https://github.com/lucas-hochmann-rosa/barber-shop-suite.git
cd barber-shop-suite

# 2. Visualizar o grafo de branches e commits
git log --oneline --graph --decorate --all

# 3. Alternar entre as branches
git checkout develop
git checkout main

# 4. Executar os testes automatizados da versão
mvn clean test
```

---

## 6. Conclusão da Governança

O repositório apresenta um histórico de desenvolvimento consistente, coeso e transparente, com branches estruturadas, commits atômicos e rastreabilidade total das funcionalidades implementadas, em plena conformidade com as exigências da Etapa 9 do Projeto Integrador II.
