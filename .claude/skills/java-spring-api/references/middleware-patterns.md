# Middleware & Logging Patterns — Spring Boot

## GlobalExceptionHandler

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handle(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors(List.of(ex.getMessage())));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handle(DomainException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errors(ex.getErrors().stream().map(Error::message).toList()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handle(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors(List.of(ex.getMessage())));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handle(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors(List.of(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Erro inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(errors(List.of("Erro interno do servidor")));
    }

    private Map<String, Object> errors(List<String> messages) {
        return Map.of("errors", messages);
    }
}
```

---

## RequestIdFilter (`@Order(1)`)

- Gera UUID ou lê `X-Request-ID` do header
- Injeta no MDC (`requestId`) para rastreabilidade nos logs
- Devolve `X-Request-ID` na resposta

```java
@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String id = Optional.ofNullable(req.getHeader(HEADER))
                .filter(h -> !h.isBlank())
                .orElseGet(() -> UUID.randomUUID().toString());
        MDC.put("requestId", id);
        res.setHeader(HEADER, id);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}
```

---

## RequestLoggingFilter (`@Order(2)`)

- Loga `METHOD URI → STATUS (Xms)` ao final de cada request
- Ignora `/actuator`, `/swagger-ui`, `/v3/api-docs`

```java
@Component
@Order(2)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final List<String> IGNORED = List.of("/actuator", "/swagger-ui", "/v3/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        if (IGNORED.stream().anyMatch(req.getRequestURI()::startsWith)) {
            chain.doFilter(req, res);
            return;
        }
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            log.info("{} {} → {} ({}ms)",
                    req.getMethod(), req.getRequestURI(),
                    res.getStatus(), System.currentTimeMillis() - start);
        }
    }
}
```

---

## Logging (logback-spring.xml)

```xml
<property name="CONSOLE_PATTERN"
    value="%white(%d{HH:mm:ss.SSS}) %highlight(%-5level) %magenta([%-15.15thread]) %green([%X{requestId:-no-id}]) %cyan(%-36.36logger{36}) %white(—) %msg%n"/>
```

**Perfis:**
- `default`/`dev`: apenas console
- `prod`: console + arquivo rotativo (`logs/app.log`, 50MB, 30 dias)

**Níveis:** `com.{empresa}.{app}` → `DEBUG`; frameworks → `WARN`/`INFO`
