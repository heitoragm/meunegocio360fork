package br.com.meunegocio360.core.cliente.api;

import br.com.meunegocio360.core.cliente.domain.DadosDoCliente;
import br.com.meunegocio360.core.cliente.domain.TipoPessoa;
import java.util.Arrays;

/** Conversões comuns aos corpos de cadastro e de edição de cliente. */
final class ClienteRequests {

  private ClienteRequests() {}

  /** {@code null} quando o valor não é um tipo de pessoa conhecido (a validação acusa o erro). */
  static TipoPessoa tipo(String valor) {
    return Arrays.stream(TipoPessoa.values())
        .filter(t -> t.name().equals(valor))
        .findFirst()
        .orElse(null);
  }

  static DadosDoCliente dados(
      String tipoPessoa, String nome, String documento, String email, String telefone) {
    return new DadosDoCliente(tipo(tipoPessoa), nome, documento, email, telefone);
  }
}
