package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.shared.domain.TipoPessoa;
import java.time.Instant;
import java.util.UUID;

/** Visão do fornecedor devolvida pelos casos de uso; a entidade JPA não sai desta camada. */
public record FichaDoFornecedor(
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

  static FichaDoFornecedor de(Fornecedor fornecedor) {
    return new FichaDoFornecedor(
        fornecedor.getId(),
        fornecedor.getTipoPessoa(),
        fornecedor.getNome(),
        fornecedor.getDocumento(),
        fornecedor.getEmail(),
        fornecedor.getTelefone(),
        fornecedor.isAtivo(),
        fornecedor.getCriadoEm(),
        fornecedor.getAtualizadoEm(),
        fornecedor.getVersao());
  }

  /** Dados pessoais (RNF-025) nunca aparecem em logs. */
  @Override
  public String toString() {
    return "FichaDoFornecedor[id=" + id + "]";
  }
}
