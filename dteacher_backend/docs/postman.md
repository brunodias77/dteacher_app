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

## Endpoints autenticados

Todos os endpoints que não sejam `/api/auth/**` exigem o header:

```
Authorization: Bearer <token>
```

Para configurar no Postman:
1. Copie o `accessToken` retornado no login
2. Na aba **Authorization** da requisição, selecione **Bearer Token**
3. Cole o token no campo

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
