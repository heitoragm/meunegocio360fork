package br.com.meunegocio360.core.shared.security;

import jakarta.servlet.DispatcherType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Cadeia de segurança explícita, stateless e negada por padrão (RNF-021, RNF-BE-40).
 *
 * <p>Exceções públicas, todas documentadas aqui:
 *
 * <ul>
 *   <li>{@code POST /api/v1/auth/login}, {@code /refresh} e {@code /logout} — autenticação; refresh
 *       e logout são protegidos por cookie SameSite=Strict, Path restrito e verificação de Origin;
 *   <li>{@code GET /actuator/health} — health check do Render, sem detalhes (RNF-104);
 *   <li>Swagger UI e OpenAPI — somente quando o springdoc está habilitado (fora de produção,
 *       RNF-030);
 *   <li>despacho interno de erro do servlet.
 * </ul>
 *
 * <p>O CSRF do Spring fica desabilitado porque as APIs se autenticam pelo cabeçalho Authorization;
 * os endpoints com cookie usam as proteções acima (RNF-BE-40).
 *
 * <p>A autorização por perfil fica nos endpoints, com {@code @PreAuthorize} (RNF-BE-47); a falta de
 * permissão responde 403 {@code ACCESS_DENIED} pelo tratador único de erros.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private static final String[] ROTAS_SWAGGER = {
    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      ProblemaDeAutenticacaoEntryPoint entryPoint,
      @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
      CorsConfigurationSource corsConfigurationSource,
      ObjectProvider<RegrasDeAcesso> regrasDasFeatures,
      @Value("${springdoc.api-docs.enabled:true}") boolean swaggerHabilitado)
      throws Exception {
    CorsFilter corsFilter = new CorsFilter(corsConfigurationSource);
    corsFilter.setCorsProcessor(new ProcessadorCorsComProblema(resolver));
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .addFilterBefore(corsFilter, CsrfFilter.class)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(b -> b.disable())
        .formLogin(f -> f.disable())
        .logout(l -> l.disable())
        .requestCache(c -> c.disable())
        .authorizeHttpRequests(auth -> {
          auth.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll();
          auth.requestMatchers(
                  HttpMethod.POST,
                  "/api/v1/auth/login",
                  "/api/v1/auth/refresh",
                  "/api/v1/auth/logout")
              .permitAll();
          auth.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll();
          if (swaggerHabilitado) {
            auth.requestMatchers(HttpMethod.GET, ROTAS_SWAGGER).permitAll();
          }
          // Regras de perfil de cada feature, avaliadas antes da leitura do corpo (RNF-BE-47).
          regrasDasFeatures.orderedStream().forEach(regras -> regras.aplicar(auth));
          auth.anyRequest().authenticated();
        })
        .oauth2ResourceServer(rs ->
            rs.jwt(jwt -> jwt.jwtAuthenticationConverter(conversorDePerfil()))
                .authenticationEntryPoint(entryPoint))
        .exceptionHandling(e ->
            e.authenticationEntryPoint(entryPoint)
                .accessDeniedHandler((request, response, ex) ->
                    resolver.resolveException(request, response, null, ex)));
    return http.build();
  }

  /**
   * Converte a claim {@code perfil} do JWT na autoridade {@code ROLE_<perfil>}, usada por
   * {@code @PreAuthorize("hasRole('ADMIN')")} nos endpoints (RNF-BE-47).
   */
  private static JwtAuthenticationConverter conversorDePerfil() {
    JwtGrantedAuthoritiesConverter autoridades = new JwtGrantedAuthoritiesConverter();
    autoridades.setAuthoritiesClaimName("perfil");
    autoridades.setAuthorityPrefix("ROLE_");
    JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
    conversor.setJwtGrantedAuthoritiesConverter(autoridades);
    return conversor;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // BCrypt com prefixo {bcrypt}, permitindo evoluir o algoritmo no futuro (RNF-023).
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(CorsProperties cors) {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(new ArrayList<>(cors.allowedOrigins()));
    config.setAllowCredentials(true);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-Id"));
    config.setExposedHeaders(List.of("X-Request-Id", "Retry-After", "Location"));
    config.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }
}
