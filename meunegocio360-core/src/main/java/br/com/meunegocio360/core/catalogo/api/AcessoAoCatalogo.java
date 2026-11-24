package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.shared.security.RegrasDeAcesso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Matriz de autorização do catálogo (RNF-BE-47) aplicada por URL: ADMIN e OPERADOR_COMERCIAL em
 * categorias e itens; OPERADOR_FINANCEIRO recebe 403 antes de qualquer validação do corpo.
 */
@Component
class AcessoAoCatalogo implements RegrasDeAcesso {

  @Override
  public void aplicar(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          regras) {
    regras
        .requestMatchers(
            "/api/v1/itens-catalogo",
            "/api/v1/itens-catalogo/**",
            "/api/v1/categorias",
            "/api/v1/categorias/**")
        .hasAnyRole("ADMIN", "OPERADOR_COMERCIAL");
  }
}
