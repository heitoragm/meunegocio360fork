package br.com.meunegocio360.core.shared.domain;

import br.com.meunegocio360.core.shared.validation.DocumentoFiscal;
import java.util.Arrays;

/**
 * Pessoa física usa CPF (11 dígitos); pessoa jurídica, CNPJ (14 dígitos). Comum aos cadastros de
 * clientes (RF-BE-003, RN-02) e fornecedores (RF-BE-004, RN-01).
 */
public enum TipoPessoa {
  FISICA,
  JURIDICA;

  /** Documento só com dígitos, válido pelos dígitos verificadores e compatível com o tipo. */
  public boolean aceita(String documento) {
    return this == FISICA
        ? DocumentoFiscal.cpfValido(documento)
        : DocumentoFiscal.cnpjValido(documento);
  }

  /** {@code null} quando o texto não é um tipo de pessoa conhecido (a validação acusa o erro). */
  public static TipoPessoa deTexto(String valor) {
    return Arrays.stream(values()).filter(t -> t.name().equals(valor)).findFirst().orElse(null);
  }
}
