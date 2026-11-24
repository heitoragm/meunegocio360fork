package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-03 e RNF-097: fornecedor com vínculos só pode ser inativado. */
public class FornecedorComHistoricoException extends ApiException {

  public FornecedorComHistoricoException() {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "SUPPLIER_HAS_HISTORY",
        "fornecedor-com-historico",
        "Fornecedor com histórico",
        "O fornecedor possui pedidos de compra ou contas a pagar vinculados e não pode ser"
            + " excluído. Inative o fornecedor.");
  }
}
