---
name: java-spring-api
description: Senior Java/Spring Boot developer for REST APIs using Clean Architecture. Use when the user asks to create, implement, scaffold, code, or refactor use cases, entities, validators, repositories, controllers, filters, exceptions, migrations, or tests in a Spring Boot project. Triggers on mentions of UseCase, Notification, Either, Validator, JPA entity, Flyway migration, JWT, SecurityConfig, GlobalExceptionHandler, controller endpoint, or Spring Boot patterns.
when_to_use: Use when implementing any Java Spring Boot feature, creating new classes, fixing compilation errors, or when the user asks how to structure code in this stack.
allowed-tools: Bash(mvn *) Bash(./mvnw *)
---

# Java Spring Boot — Padrões e Convenções

## Stack

| Camada | Tecnologia |
|---|---|
| Framework | Spring Boot 4.x |
| Linguagem | Java 21+ (records, sealed classes, pattern matching) |
| ORM | Hibernate 7 / Spring Data JPA |
| Migrations | Flyway 11 |
| Segurança | Spring Security 7 + JJWT 0.12.6 |
| Utilitários | Lombok, Vavr 0.10.4 |
| Documentação | SpringDoc OpenAPI 2.x (Swagger UI) |
| Testes | JUnit 5, AssertJ, Testcontainers |

---

## Estrutura de pacotes

```
com.{empresa}.{app}
├── configuration/      # SecurityConfig, JwtAuthenticationFilter, OpenApiConfig
├── contract/           # UseCase<IN, OUT>
├── controller/         # Controllers REST
├── error/              # *Error — constantes de mensagens em PT-BR
├── exception/          # DomainException, NotFoundException, ConflictException, UnauthorizedException
├── middlewares/        # RequestIdFilter, RequestLoggingFilter, GlobalExceptionHandler
├── model/              # Entidades JPA
├── repository/         # Interfaces Spring Data JPA
├── service/            # Serviços de infraestrutura (JwtService, UserDetailsService)
├── usecase/            # Casos de uso agrupados por domínio
│   └── {dominio}/
│       ├── {Acao}Request.java
│       ├── {Acao}Response.java
│       └── {Acao}UseCase.java
├── validation/         # Error, ValidationHandler, Notification, Validator, ThrowsValidationHandler
└── validator/          # {Entidade}Validator
```

---

## Contrato UseCase

Todo caso de uso implementa `UseCase<IN, OUT>` e retorna `Either<Notification, OUT>` do Vavr:

```java
public interface UseCase<IN, OUT> {
    Either<Notification, OUT> execute(IN in);
}
```

- `Either.left(Notification)` → erro de validação ou regra de negócio
- `Either.right(Response)` → sucesso

### Implementação padrão

```java
@Service
@RequiredArgsConstructor
public class CriarAlgoUseCase implements UseCase<CriarAlgoRequest, CriarAlgoResponse> {

    @Override
    @Transactional
    public Either<Notification, CriarAlgoResponse> execute(CriarAlgoRequest request) {
        Notification notification = Notification.create();

        validateRequest(request, notification);
        if (notification.hasError()) return Either.left(notification);

        // lógica de negócio

        return Either.right(new CriarAlgoResponse(...));
    }
}
```

### Request e Response sempre como records

```java
public record CriarAlgoRequest(String campo1, String campo2) {}
public record CriarAlgoResponse(UUID id, String campo1) {}
```

---

## Validação

### Padrão Notification (acumula todos os erros)

```java
Notification notification = Notification.create();
new EntidadeValidator(entidade, notification).validate();
if (notification.hasError()) return Either.left(notification);
```

### Padrão ThrowsValidationHandler (lança na primeira falha)

```java
new EntidadeValidator(entidade, new ThrowsValidationHandler()).validate();
```

### Implementar um Validator

```java
public class EntidadeValidator extends Validator {

    private final Entidade entidade;

    public EntidadeValidator(final Entidade entidade, final Notification notification) {
        super(notification);
        this.entidade = entidade;
    }

    @Override
    public void validate() {
        validateCampo();
    }

    private void validateCampo() {
        if (entidade.getCampo() == null || entidade.getCampo().isBlank()) {
            validationHandler().append(EntidadeError.CAMPO_REQUIRED);
        }
    }
}
```

### Classe de erros (mensagens em português)

```java
public final class EntidadeError {
    private EntidadeError() {}

    public static final Error CAMPO_REQUIRED      = new Error("Campo é obrigatório");
    public static final Error CAMPO_TOO_LONG      = new Error("Campo deve ter no máximo X caracteres");
    public static final Error EMAIL_INVALID       = new Error("E-mail deve ser um endereço válido");
    public static final Error EMAIL_ALREADY_EXISTS = new Error("E-mail já está em uso");
    public static final Error NOT_FOUND           = new Error("Entidade não encontrada");
    public static final Error INVALID_CREDENTIALS = new Error("E-mail ou senha inválidos");
}
```

**Regras:** sempre em português · classe `final` com construtor privado · constantes `public static final Error`

---

## Exceções HTTP

| Exceção | HTTP | Quando usar |
|---|---|---|
| `DomainException(List<Error>)` | 422 | Violação de regra de domínio |
| `NotFoundException(msg)` | 404 | Entidade não encontrada — usar `NotFoundException.of("User", id)` |
| `ConflictException(msg)` | 409 | Recurso duplicado |
| `UnauthorizedException(msg)` | 401 | Credenciais inválidas |

O `GlobalExceptionHandler` captura todas e retorna `{ "errors": ["..."] }`.

---

## Controllers

Controllers apenas orquestram — recebem request, chamam use case, mapeiam `Either` para `ResponseEntity`.

```java
@RestController
@RequestMapping("/api/{recurso}")
@RequiredArgsConstructor
public class EntidadeController {

    private final CriarAlgoUseCase criarAlgoUseCase;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarAlgoRequest request) {
        return criarAlgoUseCase.execute(request).fold(
                notification -> ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(notification),
                body -> ResponseEntity.status(HttpStatus.CREATED).body(body)
        );
    }
}
```

**Regras:** sem lógica de negócio · usar `.fold()` para mapear `Either` · `201` para criação, `200` para leitura/atualização

---

## Entidades JPA

```java
@Entity
@Table(name = "nome_tabela")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Entidade {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String campo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    private void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
```

**Convenções de tipos:**

| SQL | Java |
|---|---|
| `TIMESTAMPTZ` | `OffsetDateTime` |
| `DATE` | `LocalDate` |
| `NUMERIC(4,2)` | `BigDecimal` |
| `JSONB` | `String` com `@JdbcTypeCode(SqlTypes.JSON)` |

UUID via `@UuidGenerator` · snake_case para nomes de coluna · chave composta: `@Embeddable` + `@EmbeddedId`

---

## Repositórios

```java
public interface EntidadeRepository extends JpaRepository<Entidade, UUID> {
    Optional<Entidade> findByEmail(String email);
}
```

---

## Migrations Flyway

Arquivos em `src/main/resources/db/migration/`, nomeados `V{N}__{descricao}.sql`:

```sql
-- V2__create_users.sql
CREATE TABLE users (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name  VARCHAR(100),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

**Regras:** nunca editar migrations já aplicadas · `ddl-auto: validate` em produção · extensão `pgcrypto` na V1

---

## Variáveis de ambiente

| Variável | Descrição |
|---|---|
| `JWT_SECRET` | Chave HMAC Base64 ≥ 256 bits |
| `CORS_ALLOWED_ORIGINS` | Origins permitidas (padrão `*` em dev) |
| `SPRING_DATASOURCE_URL` | URL do banco em produção |

---

## 📚 Additional References

Load these files when the task requires detailed patterns:

- **Segurança**: SecurityConfig, JwtAuthenticationFilter, application.yaml JWT, CORS, Swagger/OpenAPI → [references/security-patterns.md](references/security-patterns.md)
- **Middlewares & Logging**: RequestIdFilter, RequestLoggingFilter, GlobalExceptionHandler, logback → [references/middleware-patterns.md](references/middleware-patterns.md)
- **Testes**: JUnit 5, AssertJ, unit tests de model, Testcontainers → [references/testing-patterns.md](references/testing-patterns.md)
