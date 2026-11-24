package br.com.meunegocio360.core.fornecedor.domain;

import br.com.meunegocio360.core.shared.domain.TipoPessoa;

/**
 * Campos editáveis do fornecedor, já validados e normalizados: textos aparados, opcionais vazios
 * como nulo e documento só com dígitos (RN-02 e RN-04 de RF-BE-003).
 */
public record DadosDoFornecedor(
    TipoPessoa tipoPessoa, String nome, String documento, String email, String telefone) {

  /** O documento é dado pessoal (RNF-025): nunca aparece em logs nem em mensagens. */
  @Override
  public String toString() {
    return "DadosDoFornecedor[tipoPessoa=" + tipoPessoa + "]";
  }
}
