package br.com.meunegocio360.core.fornecedor.api;

import br.com.meunegocio360.core.shared.security.RegrasDeAcesso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Matriz de autorização dos fornecedores (RNF-BE-47) aplicada por URL: ADMIN e OPERADOR_FINANCEIRO
 * em todas as operações; OPERADOR_COMERCIAL recebe 403 (RN-05) antes de qualquer validação do
 * corpo.
 */
@Component
class AcessoAosFornecedores implements RegrasDeAcesso {

  @Override
  public void aplicar(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          regras) {
    regras
        .requestMatchers("/api/v1/fornecedores", "/api/v1/fornecedores/**")
        .hasAnyRole("ADMIN", "OPERADOR_FINANCEIRO");
  }
}
