# dTeacher API — Guia de testes no Postman

Base URL: `http://localhost:8080`

---

## Pré-requisitos

1. Docker rodando: `docker compose -f docker/docker-compose.yml up -d`
2. Aplicação rodando pelo IntelliJ ou `mvn spring-boot:run`

---

## Auth

### POST /api/auth/register

Cria um novo usuário e retorna um JWT.

**Request**
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123",
  "passwordConfirmation": "senha123"
}
```

**Respostas**

| Status | Situação |
|--------|----------|
| 201 | Usuário criado — body contém `id`, `email`, `name` |
| 409 | E-mail já cadastrado |
| 422 | Erros de validação — body lista todos os erros |

**Exemplo de resposta 201**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "joao@email.com",
  "name": "João Silva"
}
```

**Casos de erro para testar**

- Senha sem número → `422` com `"Senha deve conter pelo menos 1 número"`
- Senha com menos de 8 caracteres → `422`
- Confirmação diferente da senha → `422`
- E-mail inválido → `422`
- Repetir o mesmo e-mail → `409`

---

### POST /api/auth/login

Autentica um usuário existente e retorna JWT com dados do usuário.

**Request**
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

```json
{
  "email": "joao@email.com",
  "password": "senha123"
}
```

**Respostas**

| Status | Situação |
|--------|----------|
| 200 | Login bem-sucedido — body contém `accessToken`, `expiresIn`, `user` |
| 401 | Credenciais inválidas |

**Exemplo de resposta 200**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 86400,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "João Silva",
    "email": "joao@email.com"
  }
}
```

**Casos de erro para testar**

- E-mail não cadastrado → `401` com `"E-mail ou senha inválidos"`
- Senha incorreta → `401` com `"E-mail ou senha inválidos"`
- Body vazio ou campos em branco → `401`

> A mensagem de erro é propositalmente genérica em ambos os casos para não revelar se o e-mail existe na base.

---

### POST /api/auth/logout

Encerra a sessão do usuário autenticado. A invalidação do token ocorre no cliente.

**Request**
```
POST http://localhost:8080/api/auth/logout
Authorization: Bearer <token>
```

**Respostas**

| Status | Situação |
|--------|----------|
| 204 | Logout realizado — sem body |
| 403 | Token ausente ou inválido |

**Casos de erro para testar**

- Sem header `Authorization` → `403`
- Token expirado ou malformado → `403`

---

## Endpoints autenticados

Todos os endpoints fora de `/api/auth/register` e `/api/auth/login` exigem o header:

```
Authorization: Bearer <token>
```

Para configurar no Postman:
1. Copie o `accessToken` retornado no login
2. Na aba **Authorization** da requisição, selecione **Bearer Token**
3. Cole o token no campo

---

## Me

### GET /api/me/preferences

Retorna as preferências de interface do usuário autenticado.

**Request**
```
GET http://localhost:8080/api/me/preferences
Authorization: Bearer <token>
```

**Respostas**

| Status | Situação |
|--------|----------|
| 200 | Preferências retornadas |
| 403 | Token ausente ou inválido |

**Exemplo de resposta 200**
```json
{
  "accent": "lime",
  "density": "cozy",
  "uppercaseLevel": "labels",
  "showStreakBar": true,
  "defaultCefr": "B1",
  "lastActiveTab": "generator"
}
```

---

### PUT /api/me/preferences

Atualiza as preferências do usuário autenticado. Todos os campos são opcionais — apenas os campos enviados são atualizados.

**Request**
```
PUT http://localhost:8080/api/me/preferences
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "accent": "amber",
  "density": "compact",
  "uppercaseLevel": "off",
  "showStreakBar": false,
  "defaultCefr": "B2",
  "lastActiveTab": "flashcards"
}
```

**Valores aceitos**

| Campo | Valores |
|-------|---------|
| `accent` | `lime`, `amber`, `cyan`, `magenta`, `white` |
| `density` | `compact`, `cozy`, `roomy` |
| `uppercaseLevel` | `labels`, `headings`, `off` |
| `showStreakBar` | `true`, `false` |
| `defaultCefr` | `A1`, `A2`, `B1`, `B2`, `C1`, `C2` |
| `lastActiveTab` | `generator`, `flashcards`, `vocabulary`, `textStudy`, `tutor`, `fillin`, `phonetics`, `immersion` |

**Respostas**

| Status | Situação |
|--------|----------|
| 200 | Preferências atualizadas — body contém os valores salvos |
| 422 | Valor inválido — body lista os erros |
| 403 | Token ausente ou inválido |

**Casos de erro para testar**

- `"accent": "purple"` → `422` com mensagem descrevendo os valores aceitos
- `"density": "dense"` → `422`
- `"defaultCefr": "D1"` → `422`

---

## Flashcards

### GET /api/flashcards

Retorna todos os flashcards do deck do usuário autenticado, ordenados por `nextReview` crescente (mais urgentes primeiro), com contagem de pendentes.

**Request**
```
GET http://localhost:8080/api/flashcards
Authorization: Bearer <token>
```

**Respostas**

| Status | Situação |
|--------|----------|
| 200 | Lista retornada — body contém `cards`, `total` e `totalDue` |
| 403 | Token ausente ou inválido |

**Exemplo de resposta 200**
```json
{
  "cards": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "english": "The flight was delayed by two hours.",
      "portuguese": "O voo atrasou duas horas.",
      "source": "generator",
      "nextReview": "2025-04-28T10:00:00Z",
      "intervalDays": 0,
      "easeFactor": 2.50,
      "repetitions": 0,
      "createdAt": "2025-04-27T09:00:00Z"
    }
  ],
  "total": 5,
  "totalDue": 2
}
```

> `totalDue` conta os cartões com `nextReview ≤ now` — são os que o algoritmo SRS já considera prontos para revisão.

---

### POST /api/flashcards

Adiciona um par inglês/português ao deck do usuário autenticado com valores SRS padrão.

**Request**
```
POST http://localhost:8080/api/flashcards
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "english": "The flight was delayed by two hours.",
  "portuguese": "O voo atrasou duas horas.",
  "source": "generator"
}
```

> `source` é opcional — omitir ou enviar `null` usa `"manual"` como padrão. Valores comuns: `generator`, `text-study`, `vocabulary`.

**Respostas**

| Status | Situação |
|--------|----------|
| 201 | Flashcard criado — body contém o registro completo |
| 422 | Erros de validação |
| 403 | Token ausente ou inválido |

**Exemplo de resposta 201**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "english": "The flight was delayed by two hours.",
  "portuguese": "O voo atrasou duas horas.",
  "source": "generator",
  "nextReview": "2025-04-28T12:00:00Z",
  "intervalDays": 0,
  "easeFactor": 2.50,
  "repetitions": 0,
  "createdAt": "2025-04-28T12:00:00Z"
}
```

**Casos de erro para testar**

- `english` vazio → `422` com `"Texto em inglês é obrigatório"`
- `portuguese` vazio → `422` com `"Tradução em português é obrigatória"`
- `source` com mais de 30 caracteres → `422`

---

## Generator

### POST /api/generator/sentences

Gera frases em inglês com tradução em português usando Gemini Flash, com base nas palavras fornecidas.

**Request**
```
POST http://localhost:8080/api/generator/sentences
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "words": "travel, airport, ticket"
}
```

> `words` aceita até 200 caracteres. Pode ser uma ou mais palavras/temas separados por vírgula.

**Respostas**

| Status | Situação |
|--------|----------|
| 200 | Frases geradas — body contém lista de pares inglês/português |
| 422 | Erros de validação (campo vazio ou muito longo) |
| 403 | Token ausente ou inválido |

**Exemplo de resposta 200**
```json
{
  "sentences": [
    {
      "english": "She arrived at the airport three hours before her flight.",
      "portuguese": "Ela chegou ao aeroporto três horas antes do voo."
    },
    {
      "english": "He bought a round-trip ticket to London.",
      "portuguese": "Ele comprou uma passagem de ida e volta para Londres."
    }
  ]
}
```

**Casos de erro para testar**

- `words` vazio ou `null` → `422` com `"Palavras são obrigatórias"`
- `words` com mais de 200 caracteres → `422` com `"Palavras são muito longas"`
- Gemini indisponível ou chave inválida → `422` com `"Serviço de IA temporariamente indisponível"`

> Requer a variável de ambiente `GEMINI_API_KEY` configurada no backend.

---

## Swagger UI

Todos os endpoints podem ser explorados e testados diretamente pelo Swagger:

```
http://localhost:8080/swagger-ui.html
```

Para autenticar no Swagger:
1. Clique no botão **Authorize** (cadeado)
2. Cole o token retornado pelo login (sem o prefixo `Bearer`)
3. Clique em **Authorize**
