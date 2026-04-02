# Especificação Funcional — SpendWise

## 1. Visão Geral do Sistema

O **SpendWise** é uma API RESTful para gestão de finanças pessoais, permitindo ao usuário registrar, categorizar e acompanhar receitas e despesas. A aplicação oferece controle total do fluxo financeiro por meio de transações associadas a categorias personalizadas, autenticação segura via JWT e um conjunto de endpoints para consulta, criação, atualização e remoção de dados financeiros.

O sistema mantém os dados de cada usuário de forma isolada, garante integridade referencial entre transações e categorias, e fornece as informações necessárias para que frontends possam exibir dashboards analíticos com saldos, receitas, despesas e histórico de movimentações.

---

## 2. Atores do Sistema

| Ator | Descrição |
|------|-----------|
| **Usuário Regular** | Indivíduo autenticado que gerencia suas próprias categorias e transações. |
| **Administrador** | Usuário com perfil `ADMIN` que, além das operações regulares, pode criar e gerenciar categorias globais visíveis para todos os usuários. |

---

## 3. Lista de Requisitos Funcionais (RF)

### Gestão de Transações

| ID | Requisito |
|----|-----------|
| RF-001 | O sistema deve permitir ao usuário autenticado registrar uma nova transação informando descrição, valor e categoria. |
| RF-002 | O sistema deve permitir ao usuário autenticado listar todas as suas transações. |
| RF-003 | O sistema deve permitir ao usuário autenticado consultar os detalhes de uma transação específica pelo seu identificador. |
| RF-004 | O sistema deve permitir ao usuário autenticado atualizar a descrição, o valor e/ou a categoria de uma transação existente. |
| RF-005 | O sistema deve permitir ao usuário autenticado excluir uma transação existente. |

### Gestão de Categorias

| ID | Requisito |
|----|-----------|
| RF-006 | O sistema deve permitir ao usuário autenticado criar categorias personalizadas do tipo `INCOME` (receita) ou `EXPENSE` (despesa). |
| RF-007 | O sistema deve permitir ao administrador criar categorias globais disponíveis para todos os usuários. |
| RF-008 | O sistema deve permitir ao usuário autenticado listar todas as categorias disponíveis (próprias + globais). |
| RF-009 | O sistema deve permitir ao usuário filtrar categorias por tipo (`INCOME` ou `EXPENSE`). |
| RF-010 | O sistema deve permitir ao usuário listar apenas suas categorias pessoais. |
| RF-011 | O sistema deve permitir ao usuário consultar os detalhes de uma categoria específica pelo seu identificador. |
| RF-012 | O sistema deve permitir ao usuário atualizar o nome e/ou o tipo de uma categoria pessoal. |
| RF-013 | O sistema deve permitir ao usuário excluir uma categoria pessoal. |

### Autenticação e Gestão de Usuários

| ID | Requisito |
|----|-----------|
| RF-014 | O sistema deve permitir o cadastro de novos usuários informando nome, e-mail e senha. |
| RF-015 | O sistema deve permitir ao usuário autenticar-se com e-mail e senha, recebendo um token JWT via cookie HttpOnly. |
| RF-016 | O sistema deve permitir ao usuário encerrar a sessão (logout), invalidando o cookie de autenticação. |
| RF-017 | O sistema deve permitir ao usuário autenticado consultar seus dados de perfil. |
| RF-018 | O sistema deve permitir ao usuário autenticado atualizar seu nome. |
| RF-019 | O sistema deve permitir ao usuário autenticado atualizar seu e-mail. |
| RF-020 | O sistema deve permitir ao usuário autenticado excluir sua conta, removendo em cascata todas as suas categorias e transações. |

---

## 4. Lista de Requisitos Não Funcionais (RNF)

| ID | Categoria | Requisito |
|----|-----------|-----------|
| RNF-001 | **Performance** | As operações de criação, leitura, atualização e exclusão de transações e categorias devem ser executadas em até 300 ms sob condições normais de carga. |
| RNF-002 | **Segurança** | Todos os endpoints, exceto cadastro e login, devem exigir autenticação JWT válida. O token deve ser armazenado em cookie HttpOnly para evitar ataques XSS. |
| RNF-003 | **Responsividade** | A API deve suportar consumo por clientes web e mobile, expondo cabeçalhos CORS configuráveis. |
| RNF-004 | **Persistência** | Os dados devem ser persistidos em banco de dados relacional PostgreSQL com integridade referencial garantida por chaves estrangeiras. |
| RNF-005 | **Disponibilidade** | A aplicação deve expor endpoint de health check (`/actuator/health`) para monitoramento de disponibilidade. |
| RNF-006 | **Integridade** | Não deve haver duplicidade de e-mails no cadastro de usuários. Identificadores de transações e categorias devem ser únicos (UUID). |
| RNF-007 | **Documentação** | A API deve expor documentação interativa via Swagger UI em `/swagger-ui.html` e especificação OpenAPI em `/v3/api-docs`. |
| RNF-008 | **Isolamento de Dados** | Cada usuário deve ter acesso exclusivamente às suas próprias transações e categorias pessoais. Categorias globais são somente de leitura para usuários regulares. |

---

## 5. Regras de Negócio (RN)

| ID | Regra |
|----|-------|
| RN-001 | Toda transação deve possuir um identificador único (UUID gerado automaticamente). |
| RN-002 | O valor (`amount`) de uma transação deve ser um número positivo maior que zero. |
| RN-003 | Toda transação deve estar associada a uma categoria existente e acessível ao usuário. |
| RN-004 | O tipo financeiro de uma transação (receita ou despesa) é determinado pelo tipo (`INCOME`/`EXPENSE`) da categoria à qual está vinculada. |
| RN-005 | O saldo do usuário é calculado como: `Σ(transações de categorias INCOME) − Σ(transações de categorias EXPENSE)`. |
| RN-006 | Ao excluir uma categoria, todas as transações associadas a ela são removidas permanentemente (cascade delete). |
| RN-007 | Ao excluir um usuário, todas as suas categorias e transações são removidas permanentemente (cascade delete). |
| RN-008 | Categorias globais podem ser visualizadas por qualquer usuário autenticado, mas somente administradores podem criá-las, editá-las ou excluí-las. |
| RN-009 | Um usuário não pode excluir ou editar categorias pertencentes a outro usuário ou categorias globais (somente administrador). |
| RN-010 | Um usuário não pode acessar, editar ou excluir transações de outro usuário. |
| RN-011 | O e-mail utilizado no cadastro deve ser único no sistema; tentativas de duplicação retornam erro `409 Conflict`. |
| RN-012 | A senha do usuário deve ter no mínimo 8 caracteres e conter ao menos 1 dígito numérico; é armazenada com hash BCrypt. |

---

## 6. Modelagem de Dados

### Entidades

#### Usuário (`users`)

| Atributo | Tipo | Restrições | Descrição |
|----------|------|-----------|-----------|
| `id` | UUID | PK, not null | Identificador único gerado automaticamente |
| `name` | VARCHAR | not null, min 2 chars | Nome completo do usuário |
| `email` | VARCHAR | not null, unique | Endereço de e-mail (login) |
| `password` | VARCHAR | not null, min 8 chars, >= 1 dígito | Senha com hash BCrypt |
| `role` | ENUM | not null, default `REGULAR` | Papel: `ADMIN` ou `REGULAR` |
| `created_at` | TIMESTAMP | not null, auto | Data/hora de criação do registro |
| `updated_at` | TIMESTAMP | not null, auto | Data/hora da última atualização |

#### Categoria (`category`)

| Atributo | Tipo | Restrições | Descrição |
|----------|------|-----------|-----------|
| `id` | UUID | PK, not null | Identificador único |
| `name` | VARCHAR | not null, min 3 chars | Nome da categoria |
| `type` | ENUM | not null | Tipo: `INCOME` ou `EXPENSE` |
| `is_global` | BOOLEAN | not null, default `false` | Se `true`, visível para todos os usuários |
| `user_id` | UUID | FK → users(id), nullable | Dono da categoria; `null` para categorias globais |

#### Transação (`transaction`)

| Atributo | Tipo | Restrições | Descrição |
|----------|------|-----------|-----------|
| `id` | UUID | PK, not null | Identificador único |
| `description` | VARCHAR | not null | Descrição da movimentação |
| `amount` | DECIMAL | not null, > 0 | Valor da transação |
| `category_id` | UUID | FK → category(id), not null | Categoria associada |
| `user_id` | UUID | FK → users(id), not null | Usuário dono da transação |
| `created_at` | TIMESTAMP | not null, auto | Data/hora de criação |
| `updated_at` | TIMESTAMP | not null, auto | Data/hora da última atualização |

### Relacionamentos

```
User (1) ──< Category (N)       [cascade delete]
User (1) ──< Transaction (N)    [cascade delete]
Category (1) ──< Transaction (N) [cascade delete]
```

### Diagrama Entidade-Relacionamento (Simplificado)

```
┌──────────────┐        ┌────────────────┐        ┌──────────────────┐
│    users     │        │    category    │        │   transaction    │
├──────────────┤        ├────────────────┤        ├──────────────────┤
│ id (PK)      │◄───────│ user_id (FK)   │◄───────│ category_id (FK) │
│ name         │        │ id (PK)        │        │ id (PK)          │
│ email        │        │ name           │        │ description      │
│ password     │        │ type           │        │ amount           │
│ role         │        │ is_global      │        │ user_id (FK) ────┼──►users.id
│ created_at   │        └────────────────┘        │ created_at       │
│ updated_at   │                                  │ updated_at       │
└──────────────┘                                  └──────────────────┘
```

---

## 7. Fluxos de Usuário

### Fluxo 1 — Registrar Transação

1. Usuário realiza login e obtém token JWT (cookie HttpOnly).
2. Usuário acessa a listagem de categorias disponíveis (`GET /categories`).
3. Usuário seleciona a categoria desejada e aciona criação de transação.
4. Usuário informa descrição, valor e o identificador da categoria.
5. Sistema valida os dados (valor positivo, categoria existente e acessível).
6. Sistema persiste a transação associada ao usuário autenticado.
7. Sistema retorna os dados da transação criada com status `201 Created`.

### Fluxo 2 — Editar Transação

1. Usuário solicita a listagem das suas transações (`GET /transactions`).
2. Usuário seleciona a transação desejada.
3. Usuário aciona a edição informando novos valores de descrição, valor e/ou categoria.
4. Sistema valida os dados e verifica que a transação pertence ao usuário.
5. Sistema atualiza o registro e retorna os dados atualizados.

### Fluxo 3 — Excluir Transação

1. Usuário localiza a transação na listagem.
2. Usuário aciona a exclusão (`DELETE /transactions/{id}`).
3. Sistema verifica a propriedade da transação.
4. Sistema remove permanentemente o registro e retorna `204 No Content`.

### Fluxo 4 — Criar Categoria

1. Usuário aciona a criação de categoria informando nome e tipo (`INCOME` ou `EXPENSE`).
2. Sistema persiste a categoria vinculada ao usuário autenticado.
3. Sistema retorna os dados da categoria criada com status `201 Created`.

### Fluxo 5 — Visualizar Dashboard

1. Usuário solicita a listagem das suas transações (`GET /transactions`).
2. Frontend filtra e agrupa as transações por tipo de categoria, período e/ou categoria específica.
3. Frontend calcula saldo (`Σ INCOME − Σ EXPENSE`), total de receitas e total de despesas.
4. Frontend exibe cartões de resumo, gráficos e lista de transações recentes.

### Fluxo 6 — Autenticação

1. Usuário acessa a tela de login.
2. Usuário informa e-mail e senha (`POST /users/login`).
3. Sistema valida as credenciais e gera um JWT com validade de 2 horas.
4. Sistema armazena o token em cookie HttpOnly (`Authorization`) na resposta.
5. Usuário é redirecionado ao dashboard.
6. Para encerrar a sessão, usuário aciona logout (`POST /users/logout`), e o sistema limpa o cookie.

---

## 8. Estados da Transação (Máquina de Estados)

> **Nota**: Na versão atual da API, as transações não possuem campo de status explícito. Os estados abaixo descrevem o ciclo de vida funcional da transação e servem de referência para implementações futuras e para o frontend.

### Estados Possíveis

| Estado | Descrição |
|--------|-----------|
| `PENDENTE` | Transação registrada, aguardando confirmação ou processamento |
| `CONFIRMADA` | Transação efetivada e computada no saldo |
| `CANCELADA` | Transação anulada pelo usuário, não afeta o saldo |

### Transições

```
PENDENTE ──[confirmar]──► CONFIRMADA
PENDENTE ──[cancelar]───► CANCELADA
CONFIRMADA ──[excluir]──► (removida)
CANCELADA ──[excluir]───► (removida)
```

### Restrições

- Não é permitida a transição de `CONFIRMADA` para `PENDENTE`.
- Transações `CANCELADA` não são computadas no cálculo de saldo.
- A exclusão de uma transação é permanente e irreversível.

---

## 9. Especificação do Dashboard

### Cartões de Resumo

| Cartão | Cálculo | Cor sugerida |
|--------|---------|--------------|
| **Saldo Atual** | `Σ(INCOME) − Σ(EXPENSE)` | Azul / Verde (positivo), Vermelho (negativo) |
| **Total de Receitas** | `Σ(transações com categoria INCOME)` | Verde |
| **Total de Despesas** | `Σ(transações com categoria EXPENSE)` | Vermelho |

### Gráficos de Análise

| Gráfico | Tipo | Dados |
|---------|------|-------|
| Distribuição por categoria | Pizza (Pie) | Valor por categoria no período selecionado |
| Evolução do saldo | Linha (Line) | Saldo acumulado por dia/semana/mês |
| Receitas × Despesas | Barras agrupadas | Comparativo mensal ou semanal |

### Filtros Temporais

| Filtro | Agrupamento |
|--------|-------------|
| Dia | Por data (`YYYY-MM-DD`) |
| Semana | Por semana ISO (`YYYY-WW`) |
| Mês | Por mês (`YYYY-MM`) |
| Ano | Por ano (`YYYY`) |

### Lista de Transações Recentes

- Exibe as N transações mais recentes do usuário.
- Campos exibidos: data, descrição, categoria, valor (com indicador de tipo).
- Permite acesso rápido às ações de edição e exclusão.

### Comportamento Esperado

- O dashboard deve atualizar seus dados em tempo real após qualquer operação de criação, atualização ou exclusão de transação.
- Quando não há transações no período selecionado, os cartões devem exibir `R$ 0,00` e os gráficos devem exibir estado vazio.
- Os dados devem refletir corretamente a data de criação das transações (`created_at`).

---

## 10. Casos de Uso

### Caso de Uso 1 — Registrar Despesa

**Ator**: Usuário Regular  
**Pré-condição**: Usuário autenticado; categoria de despesa cadastrada.

**Fluxo Principal**:
1. Usuário acessa o formulário de nova transação.
2. Informa descrição (ex.: "Supermercado"), valor (ex.: `350.00`) e seleciona categoria (ex.: "Alimentação" — `EXPENSE`).
3. Confirma o registro.
4. Sistema persiste e retorna os dados da transação criada.
5. Frontend atualiza saldo e lista de transações.

**Fluxos Alternativos**:
- **2a.** Valor igual ou menor a zero → sistema retorna `400 Bad Request`.
- **2b.** Categoria não encontrada ou inacessível → sistema retorna `404 Not Found`.

---

### Caso de Uso 2 — Analisar Gastos Mensais

**Ator**: Usuário Regular  
**Pré-condição**: Usuário autenticado com ao menos uma transação registrada.

**Fluxo Principal**:
1. Usuário acessa o dashboard e seleciona o filtro "Mês".
2. Frontend solicita todas as transações do usuário (`GET /transactions`).
3. Frontend filtra transações pelo mês corrente e agrupa por categoria.
4. Frontend exibe gráfico de pizza com distribuição de despesas e total gasto no mês.
5. Usuário identifica categoria com maior gasto e toma decisão financeira.

**Fluxos Alternativos**:
- **2a.** Nenhuma transação no mês → exibir gráfico vazio e valores zerados.

---

### Caso de Uso 3 — Gerenciar Categorias Pessoais

**Ator**: Usuário Regular  
**Pré-condição**: Usuário autenticado.

**Fluxo Principal**:
1. Usuário acessa a área de categorias (`GET /categories/user`).
2. Usuário cria nova categoria (ex.: "Streaming" — `EXPENSE`).
3. Usuário edita o nome de uma categoria existente (ex.: "Netflix" → "Assinaturas").
4. Usuário exclui uma categoria não utilizada.
5. Sistema confirma cada operação com resposta de sucesso.

**Fluxos Alternativos**:
- **4a.** Categoria possui transações associadas → exclusão remove também as transações vinculadas (cascade).

---

## 11. Definição de Persona

**Persona Primária: Profissional em Transição Financeira**

| Atributo | Detalhe |
|----------|---------|
| **Idade** | 25–40 anos |
| **Perfil** | Profissional assalariado ou autônomo que deseja ter mais controle sobre seu orçamento pessoal |
| **Objetivo Principal** | Entender para onde vai o dinheiro e reduzir gastos desnecessários |
| **Comportamento Digital** | Alta familiaridade com aplicativos mobile e web; utiliza internet banking e fintechs |
| **Necessidades** | Registrar receitas e despesas rapidamente; visualizar saldo e histórico; categorizar gastos |
| **Dores** | Não saber o saldo real no final do mês; dificuldade em identificar onde está gastando mais; planilhas trabalhosas |
| **Motivação** | Conquistar independência financeira; poupar para objetivos de médio prazo (viagem, imóvel, reserva de emergência) |

---

## 12. Especificação de Interface (UI/UX)

> Esta seção serve como guia para o desenvolvimento do frontend consumidor desta API.

### 12.1 Princípios de Design

- **Clareza financeira**: informações de saldo e fluxo devem ser imediatamente visíveis.
- **Ação rápida**: registrar uma transação em no máximo 3 interações.
- **Feedback imediato**: toda operação deve gerar resposta visual ao usuário.
- **Hierarquia visual**: saldo em destaque, seguido de receitas/despesas, depois detalhes.

### 12.2 Estrutura de Layout

```
┌─────────────────────────────────────────────────────────┐
│  Header Fixo: Logo | Navegação | Perfil | Logout        │
├─────────────────────────────────────────────────────────┤
│  [Saldo]     [Receitas]     [Despesas]   (cartões topo) │
├─────────────────────────────────────────────────────────┤
│  Gráfico de Gastos   │  Gráfico de Evolução do Saldo    │
├─────────────────────────────────────────────────────────┤
│  Transações Recentes          │  Filtros Temporais      │
│  (lista com ações inline)     │  Dia / Semana / Mês     │
└─────────────────────────────────────────────────────────┘
```

### 12.3 Componentes de Interface

**Cartão de Resumo Financeiro**
- Exibe: rótulo, valor monetário formatado, variação percentual (opcional)
- Ações: clique expande para detalhes do período

**Formulário de Transação**
- Campo: Descrição (obrigatório)
- Campo: Valor (numérico, obrigatório, positivo)
- Seletor: Categoria (dropdown com tipo indicado por ícone)
- Botão: "Salvar Transação"

**Item de Transação (lista)**
- Exibe: ícone de tipo, descrição, categoria, data, valor com cor (verde/vermelho)
- Ações inline: Editar, Excluir

**Formulário de Categoria**
- Campo: Nome (obrigatório, mín. 3 chars)
- Seletor: Tipo — Receita / Despesa

### 12.4 Paleta de Cores

| Papel | Cor | Hex |
|-------|-----|-----|
| Primária | Azul | `#2563EB` |
| Receita / Positivo | Verde | `#10B981` |
| Despesa / Negativo | Vermelho | `#EF4444` |
| Fundo principal | Branco | `#FFFFFF` |
| Fundo secundário | Cinza claro | `#F3F4F6` |
| Texto principal | Preto | `#111827` |
| Texto secundário | Cinza | `#6B7280` |
| Destaque / Ação destrutiva | Vermelho | `#EF4444` |
| Borda suave | Cinza muito claro | `#E5E7EB` |

### 12.5 Tipografia

| Nível | Tamanho | Peso | Uso |
|-------|---------|------|-----|
| Título principal | 24–28px | Semibold (600) | Saldo do dashboard |
| Título de seção | 18–20px | Medium (500) | Cabeçalhos de cards e seções |
| Texto normal | 14–16px | Regular (400) | Descrições e listas |
| Texto auxiliar | 12px | Regular (400) | Datas, rótulos secundários |

- **Fonte**: Inter, Roboto ou similar sans-serif
- **Espaçamento de linha**: 1.5
- **Grid**: Sistema de 8px para margens e paddings

### 12.6 Estados Visuais

| Estado | Aparência |
|--------|-----------|
| Transação de receita | Valor em verde (`#10B981`) |
| Transação de despesa | Valor em vermelho (`#EF4444`) |
| Item em hover | Fundo levemente cinza (`#F9FAFB`) |
| Ação destrutiva (excluir) | Ícone e texto em vermelho, com confirmação |
| Saldo negativo | Valor em vermelho, destaque de alerta |
| Loading | Skeleton screens nos cartões e listas |
| Estado vazio | Ilustração + texto orientativo |

### 12.7 Microcopy

**Botões e ações**:
- `"Nova Transação"` / `"Salvar"` / `"Cancelar"`
- `"Editar"` / `"Excluir"` / `"Confirmar Exclusão"`
- `"Nova Categoria"` / `"Entrar"` / `"Sair"`

**Estados vazios**:
- `"Nenhuma transação registrada ainda. Comece registrando sua primeira receita ou despesa."`
- `"Nenhuma transação neste período."`
- `"Você ainda não tem categorias personalizadas."`

**Feedback de operações**:
- `"Transação registrada com sucesso!"`
- `"Transação atualizada."`
- `"Transação excluída."`
- `"Categoria criada com sucesso!"`
- `"E-mail já cadastrado. Tente outro endereço."`
- `"Credenciais inválidas. Verifique seu e-mail e senha."`

---

## 13. Arquitetura da Informação

### 13.1 Estrutura Geral

A aplicação possui duas grandes áreas funcionais:
1. **Gestão Financeira** — transações e categorias
2. **Conta do Usuário** — perfil e autenticação

### 13.2 Mapa do Site (Frontend)

```
[Login / Cadastro]
├── Login
└── Cadastro

[Dashboard] (autenticado)
├── Resumo Financeiro
│   ├── Saldo atual
│   ├── Total de receitas
│   └── Total de despesas
├── Gráficos
│   ├── Distribuição por categoria
│   └── Evolução temporal
└── Transações Recentes

[Transações]
├── Listar transações
│   ├── Editar transação
│   └── Excluir transação
└── Nova Transação

[Categorias]
├── Listar categorias (pessoais + globais)
│   ├── Editar categoria
│   └── Excluir categoria
└── Nova Categoria

[Perfil]
├── Ver dados do perfil
├── Editar nome
├── Editar e-mail
└── Excluir conta
```

### 13.3 Navegação

- Navegação principal fixa no header (Dashboard, Transações, Categorias, Perfil)
- Transição sem recarregamento de página (SPA)
- Ações de edição e exclusão disponíveis diretamente no item da lista (no máximo 2 cliques)

### 13.4 Hierarquia de Informação

1. Saldo atual (destaque máximo)
2. Resumo de receitas e despesas
3. Lista de transações recentes
4. Acesso a categorias e perfil

### 13.5 Regras de Navegação

- Qualquer funcionalidade principal deve ser acessível em no máximo 2 cliques a partir do dashboard
- O botão de "Nova Transação" deve estar sempre visível na área de transações
- O logout deve estar acessível a partir de qualquer tela

---

## 14. Requisitos Técnicos

### Backend (implementado)

| Tecnologia | Versão | Função |
|------------|--------|--------|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.5.0 | Framework web e IoC |
| Spring Security | 3.5.0 | Autenticação e autorização |
| Spring Data JPA / Hibernate | 3.5.0 | ORM e persistência |
| PostgreSQL | 17.5 | Banco de dados relacional |
| JWT (JJWT) | 0.11.5 | Geração e validação de tokens |
| MapStruct | 1.5.5 | Mapeamento DTO ↔ Entidade |
| Lombok | Latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.7.0 | Documentação Swagger |
| Maven | 3.x | Gerenciamento de build |
| Docker / Docker Compose | - | Containerização do banco |

### Frontend (recomendado)

| Tecnologia | Sugestão | Função |
|------------|----------|--------|
| Framework | React, Vue.js ou Angular | SPA com roteamento |
| Gráficos | Chart.js, Recharts ou ApexCharts | Dashboards visuais |
| HTTP Client | Axios ou Fetch API | Consumo da API REST |
| Estilização | Tailwind CSS ou Material UI | Design system |

### Endpoints Principais da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/users` | Cadastro de usuário |
| `POST` | `/users/login` | Autenticação |
| `POST` | `/users/logout` | Logout |
| `GET` | `/users/authenticated` | Perfil do usuário autenticado |
| `PATCH` | `/users/{id}/name` | Atualizar nome |
| `PATCH` | `/users/{id}/email` | Atualizar e-mail |
| `DELETE` | `/users/{id}` | Excluir conta |
| `GET` | `/categories` | Listar todas as categorias |
| `GET` | `/categories/type?type=INCOME` | Filtrar por tipo |
| `GET` | `/categories/user` | Listar categorias pessoais |
| `POST` | `/categories` | Criar categoria pessoal |
| `POST` | `/categories/global` | Criar categoria global (admin) |
| `PATCH` | `/categories/{id}/name` | Atualizar nome da categoria |
| `PATCH` | `/categories/{id}/type` | Atualizar tipo da categoria |
| `DELETE` | `/categories/{id}` | Excluir categoria |
| `GET` | `/transactions` | Listar transações do usuário |
| `GET` | `/transactions/{id}` | Detalhe de transação |
| `POST` | `/transactions` | Criar transação |
| `PATCH` | `/transactions/{id}` | Atualizar transação |
| `DELETE` | `/transactions/{id}` | Excluir transação |

### Segurança

- Autenticação via JWT armazenado em cookie HttpOnly
- Validade do token: 2 horas (`jwt.validity=7200000`)
- Senhas armazenadas com BCrypt
- CSRF desabilitado (API stateless)
- CORS configurável via `CorsConfig`
- Controle de acesso baseado em papéis (`ADMIN` / `REGULAR`)

---

## 15. Critérios de Aceitação

| ID | Critério | Como Verificar |
|----|----------|----------------|
| CA-001 | Usuário consegue criar uma transação de despesa em até 3 interações | Teste de usabilidade / fluxo de ponta a ponta |
| CA-002 | O saldo exibido no dashboard é sempre igual a `Σ(INCOME) − Σ(EXPENSE)` para o período selecionado | Teste unitário e de integração |
| CA-003 | Tentativa de acesso a endpoints protegidos sem token retorna `401 Unauthorized` | Teste de segurança automatizado |
| CA-004 | Usuário não consegue visualizar, editar ou excluir transações de outro usuário | Teste de autorização |
| CA-005 | Exclusão de categoria remove em cascata as transações associadas | Teste de integração com verificação no banco |
| CA-006 | Cadastro com e-mail já existente retorna `409 Conflict` com mensagem descritiva | Teste de validação de entrada |
| CA-007 | Todas as operações CRUD de transações retornam resposta em até 300 ms em ambiente de desenvolvimento | Teste de performance com dados de exemplo |
| CA-008 | A documentação Swagger em `/swagger-ui.html` lista todos os endpoints com exemplos de request/response | Verificação manual após deploy |
