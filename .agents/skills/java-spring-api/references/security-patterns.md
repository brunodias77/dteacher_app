# Security Patterns — Spring Boot

## SecurityConfig

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
```

---

## JwtAuthenticationFilter

- Estende `OncePerRequestFilter`
- Lê `Authorization: Bearer <token>`
- Valida token via `JwtService`
- Popula `SecurityContextHolder` se válido

---

## application.yaml (JWT)

```yaml
jwt:
  secret: ${JWT_SECRET:chave-base64-de-256-bits}
  expiration-ms: 86400000
```

---

## CORS

```yaml
cors:
  allowed-origins: ${CORS_ALLOWED_ORIGINS:*}
```

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(allowedOrigins);
    config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Request-ID"));
    config.setExposedHeaders(List.of("X-Request-ID"));
    config.setAllowCredentials(!allowedOrigins.contains("*"));
    config.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## Swagger / OpenAPI

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
            .info(new Info().title("API").version("v1"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                    new SecurityScheme().type(HTTP).scheme("bearer").bearerFormat("JWT")));
}
```

Rotas liberadas: `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-ui.html`
