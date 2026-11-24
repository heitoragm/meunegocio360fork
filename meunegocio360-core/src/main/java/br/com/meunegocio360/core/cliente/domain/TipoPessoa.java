package br.com.meunegocio360.core.cliente.domain;

import br.com.meunegocio360.core.shared.validation.DocumentoFiscal;

/** RN-02: pessoa física usa CPF (11 dígitos); pessoa jurídica, CNPJ (14 dígitos). */
public enum TipoPessoa {
  FISICA,
  JURIDICA;

  /** Documento só com dígitos, válido pelos dígitos verificadores e compatível com o tipo. */
  public boolean aceita(String documento) {
    return this == FISICA
        ? DocumentoFiscal.cpfValido(documento)
        : DocumentoFiscal.cnpjValido(documento);
  }
}
