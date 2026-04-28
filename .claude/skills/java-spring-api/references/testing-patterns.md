# Testing Patterns — Spring Boot

## Testes unitários de model (sem Spring context)

```java
class EntidadeTest {

    @Test
    void shouldBuildWithAllFields() {
        Entidade e = Entidade.builder().campo("valor").build();
        assertThat(e.getCampo()).isEqualTo("valor");
    }

    @Test
    void prePersistShouldSetTimestamps() throws Exception {
        Entidade e = new Entidade();
        invokePrivate(e, "prePersist");
        assertThat(e.getCreatedAt()).isNotNull();
        assertThat(e.getUpdatedAt()).isNotNull();
    }

    @Test
    void preUpdateShouldRefreshUpdatedAt() throws Exception {
        Entidade e = new Entidade();
        invokePrivate(e, "prePersist");
        OffsetDateTime created = e.getUpdatedAt();
        invokePrivate(e, "preUpdate");
        assertThat(e.getUpdatedAt()).isAfterOrEqualTo(created);
    }

    private void invokePrivate(Object target, String method) throws Exception {
        var m = target.getClass().getDeclaredMethod(method);
        m.setAccessible(true);
        m.invoke(target);
    }
}
```

---

## Testes de UseCase

```java
@ExtendWith(MockitoExtension.class)
class CriarAlgoUseCaseTest {

    @Mock
    private EntidadeRepository repository;

    private CriarAlgoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CriarAlgoUseCase(repository);
    }

    @Test
    void shouldReturnRightWhenRequestIsValid() {
        var request = new CriarAlgoRequest("valor válido");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(request);

        assertThat(result.isRight()).isTrue();
        assertThat(result.get().campo()).isEqualTo("valor válido");
    }

    @Test
    void shouldReturnLeftWhenCampoIsBlank() {
        var request = new CriarAlgoRequest("");

        var result = useCase.execute(request);

        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft().hasError()).isTrue();
    }
}
```

---

## Testes de Controller (MockMvc)

```java
@WebMvcTest(EntidadeController.class)
@Import(SecurityConfig.class)
class EntidadeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CriarAlgoUseCase criarAlgoUseCase;

    @Test
    void shouldReturn201WhenCreated() throws Exception {
        var response = new CriarAlgoResponse(UUID.randomUUID(), "valor");
        when(criarAlgoUseCase.execute(any())).thenReturn(Either.right(response));

        mvc.perform(post("/api/entidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            { "campo": "valor" }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.campo").value("valor"));
    }

    @Test
    void shouldReturn422WhenValidationFails() throws Exception {
        var notification = Notification.create();
        notification.append(EntidadeError.CAMPO_REQUIRED);
        when(criarAlgoUseCase.execute(any())).thenReturn(Either.left(notification));

        mvc.perform(post("/api/entidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }
}
```

---

## Testes de Integração (Testcontainers)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class StartupTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
        // verifica que o contexto sobe sem erros e as migrations rodam corretamente
    }
}
```

**Regras:**
- Sem Spring context em testes unitários — instanciação direta
- `@PrePersist`/`@PreUpdate` testados via reflection
- `StartupTests` (integração com Testcontainers) excluído do Surefire com `@Tag("integration")`
