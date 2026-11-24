package br.com.meunegocio360.core.cliente.api;

import br.com.meunegocio360.core.shared.security.RegrasDeAcesso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Matriz de autorização dos clientes (RNF-BE-47) aplicada por URL: ADMIN e OPERADOR_COMERCIAL em
 * todas as operações; OPERADOR_FINANCEIRO recebe 403 antes de qualquer validação do corpo.
 */
@Component
class AcessoAosClientes implements RegrasDeAcesso {

  @Override
  public void aplicar(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          regras) {
    regras
        .requestMatchers("/api/v1/clientes", "/api/v1/clientes/**")
        .hasAnyRole("ADMIN", "OPERADOR_COMERCIAL");
  }
}
