# dTeacher Backend — CLAUDE.md

## Visão geral

API REST de um tutor de inglês com IA. Backend em **Spring Boot 4** + **Java 25**, banco **PostgreSQL 17**, autenticação via **JWT**.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Linguagem | Java 25 |
| Banco de dados | PostgreSQL 17 (porta 5437) |
| ORM | Hibernate 7 / Spring Data JPA |
| Migrations | Flyway 11 |
| Segurança | Spring Security 7 + JJWT 0.12.6 |
| Utilitários | Lombok, Vavr 0.10.4 |
| Documentação | SpringDoc OpenAPI (Swagger UI) |
| Testes | JUnit 5, AssertJ, Testcontainers |

---

## Infraestrutura local

```bash
# Subir o banco
docker compose -f docker/docker-compose.yml up -d

# Rodar a aplicação
mvn spring-boot:run

# Build + testes unitários
mvn clean install
```

O Flyway roda automaticamente no startup e aplica todas as migrations em `src/main/resources/db/migration/`.

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

---

## Estrutura de pacotes

```
com.dias.dteacher
├── configuration/      # SecurityConfig, JwtAuthenticationFilter, OpenApiConfig
├── contract/           # UseCase<IN, OUT> — interface base para casos de uso
├── controller/         # AuthController — endpoints REST
├── error/              # UserError — constantes de mensagens de erro em PT-BR
├── exception/          # DomainException, NotFoundException, ConflictException, UnauthorizedException
├── middlewares/        # RequestIdFilter, RequestLoggingFilter, GlobalExceptionHandler
├── model/              # Entidades JPA
├── repository/         # Interfaces Spring Data JPA
├── service/            # JwtService, CustomUserDetailsService
├── usecase/
│   └── auth/
│       ├── login/      # LoginUserUseCase, LoginUserRequest, LoginUserResponse
│       └── register/   # RegisterUserUseCase, RegisterUserRequest, RegisterUserResponse
├── validation/         # Error, ValidationHandler, Notification, Validator, ThrowsValidationHandler
└── validator/          # UserValidator
```

---

## Padrões de implementação

### Use Cases

Todo caso de uso implementa `UseCase<IN, OUT>` e retorna `Either<Notification, OUT>` do Vavr:

- `Either.left(Notification)` → erro de validação ou regra de negócio
- `Either.right(Response)` → sucesso

```java
@Service
@RequiredArgsConstructor
public class MeuUseCase implements UseCase<MeuRequest, MeuResponse> {
    @Override
    @Transactional
    public Either<Notification, MeuResponse> execute(MeuRequest request) { ... }
}
```

### Validação

O projeto usa o padrão `Validator` + `Notification` para acumular erros sem lançar exceções:

```java
Notification notification = Notification.create();
new UserValidator(user, notification).validate();
if (notification.hasError()) return Either.left(notification);
```

- `Notification` — acumula todos os erros (use em casos de uso)
- `ThrowsValidationHandler` — lança `DomainException` na primeira falha
- `UserError` — constantes de mensagens em PT-BR para o domínio User

### Erros de domínio centralizados

Mensagens ficam em classes `*Error` no pacote `error/`. Sempre em **português**.

```java
public static final Error EMAIL_REQUIRED = new Error("E-mail é obrigatório");
```

### Exceções HTTP

| Exceção | HTTP | Quando usar |
|---|---|---|
| `DomainException` | 422 | Violação de regra de domínio |
| `NotFoundException` | 404 | Entidade não encontrada — usar `NotFoundException.of("User", id)` |
| `ConflictException` | 409 | Recurso duplicado |
| `UnauthorizedException` | 401 | Credenciais inválidas |

O `GlobalExceptionHandler` captura todas e retorna `{ "errors": ["..."] }`.

### Controllers

Controllers apenas orquestram: recebem o request, chamam o use case e mapeiam o `Either` para `ResponseEntity`.

```java
@PostMapping("/alguma-rota")
public ResponseEntity<?> action(@RequestBody MeuRequest request) {
    return meuUseCase.execute(request).fold(
            notification -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(notification),
            body -> ResponseEntity.ok(body)
    );
}
```

### Modelos JPA

- UUIDs gerados via `@UuidGenerator` (Hibernate 6+)
- `TIMESTAMPTZ` → `OffsetDateTime`
- `DATE` → `LocalDate`
- `NUMERIC(4,2)` → `BigDecimal`
- `JSONB` → `String` com `@JdbcTypeCode(SqlTypes.JSON)`
- `@PrePersist` / `@PreUpdate` gerenciam `createdAt` / `updatedAt` (os triggers do banco fazem o mesmo para segurança)
- Chave composta: `@Embeddable` + `@EmbeddedId` (ex: `DailyStudyLogId`)

---

## Segurança

- Autenticação via **JWT Bearer token** — stateless, sem sessão
- Rotas públicas: `POST /api/auth/register`, `POST /api/auth/login`, `/actuator/health`, `/swagger-ui/**`, `/v3/api-docs/**`
- CORS configurável via env var `CORS_ALLOWED_ORIGINS` (padrão `*` em dev)
- JWT configurável via env vars:
  - `JWT_SECRET` — chave Base64 de ≥ 256 bits
  - `jwt.expiration-ms` — TTL em ms (padrão 86400000 = 24h)

---

## Migrations Flyway

Arquivos em `src/main/resources/db/migration/`, nomeados `V{N}__{descricao}.sql`:

| Migration | Conteúdo |
|---|---|
| V1 | Extensão pgcrypto |
| V2 | Tabela users |
| V3 | Tabela user_preferences |
| V4 | Tabela flashcards + índice |
| V5 | Tabela vocabulary_words + índices |
| V6 | Tabelas study_streaks e daily_study_log |
| V7 | Tabelas chat_sessions e chat_messages |
| V8 | Tabela text_analyses (JSONB) |
| V9 | Tabela fillin_sessions |
| V10 | Triggers de updated_at |

**Nunca editar migrations já aplicadas.** Sempre criar uma nova versão.

---

## Middlewares (ordem de execução)

1. **`RequestIdFilter`** (`@Order(1)`) — gera/lê `X-Request-ID`, injeta no MDC para rastreabilidade nos logs
2. **`RequestLoggingFilter`** (`@Order(2)`) — loga `METHOD URI → STATUS (Xms)` por requisição
3. **`JwtAuthenticationFilter`** — valida Bearer token e popula o `SecurityContext`
4. **`GlobalExceptionHandler`** — captura todas as exceções e retorna JSON padronizado

---

## Logging

Configurado em `src/main/resources/logback-spring.xml`:

- **Dev (default):** apenas console com cores
- **Prod (`--spring.profiles.active=prod`):** console + arquivo rotativo em `logs/dteacher.log` (diário, 50 MB/arquivo, 30 dias, cap 500 MB)
- `requestId` do MDC aparece em verde em cada linha de log
- Pacote `com.dias.dteacher` logado em `DEBUG`; frameworks em `WARN`/`INFO`

---

## Testes

- Testes unitários de model em `src/test/java/com/dias/dteacher/model/`
- Sem Spring context — instanciação direta + reflection para `@PrePersist`/`@PreUpdate`
- `StartupTests` excluído do Surefire (requer Docker/Testcontainers)
- Rodar apenas unitários: `mvn test -Dtest="UserTest,FlashcardTest,..."`

---

## Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SECRET` | chave embutida (só dev) | Chave HMAC Base64 ≥ 256 bits |
| `CORS_ALLOWED_ORIGINS` | `*` | Origins permitidas pelo CORS |
