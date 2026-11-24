package br.com.meunegocio360.core.shared.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Regras de acesso por perfil que uma feature registra na cadeia de segurança (RNF-BE-47). Elas são
 * avaliadas por URL, ANTES da leitura e da validação do corpo: um perfil sem permissão recebe 403
 * mesmo que o corpo seja inválido, sem receber mensagens de validação do recurso. O
 * {@code @PreAuthorize} nos endpoints continua como segunda barreira.
 */
@FunctionalInterface
public interface RegrasDeAcesso {

  void aplicar(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          regras);
}
