package br.com.meunegocio360.core.cliente.domain;

/**
 * Campos editáveis do cliente, já validados e normalizados: textos aparados, opcionais vazios como
 * nulo e documento só com dígitos (RN-02, RN-04).
 */
public record DadosDoCliente(
    TipoPessoa tipoPessoa, String nome, String documento, String email, String telefone) {

  /** O documento é dado pessoal (RNF-025): nunca aparece em logs nem em mensagens. */
  @Override
  public String toString() {
    return "DadosDoCliente[tipoPessoa=" + tipoPessoa + "]";
  }
}
