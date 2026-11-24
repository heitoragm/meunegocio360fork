package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.cliente.domain.TipoPessoa;
import java.time.Instant;
import java.util.UUID;

/** Visão do cliente devolvida pelos casos de uso; a entidade JPA não sai desta camada. */
public record FichaDoCliente(
    UUID id,
    TipoPessoa tipoPessoa,
    String nome,
    String documento,
    String email,
    String telefone,
    boolean ativo,
    Instant criadoEm,
    Instant atualizadoEm,
    long versao) {

  static FichaDoCliente de(Cliente cliente) {
    return new FichaDoCliente(
        cliente.getId(),
        cliente.getTipoPessoa(),
        cliente.getNome(),
        cliente.getDocumento(),
        cliente.getEmail(),
        cliente.getTelefone(),
        cliente.isAtivo(),
        cliente.getCriadoEm(),
        cliente.getAtualizadoEm(),
        cliente.getVersao());
  }

  /** Dados pessoais (RNF-025) nunca aparecem em logs. */
  @Override
  public String toString() {
    return "FichaDoCliente[id=" + id + "]";
  }
}
