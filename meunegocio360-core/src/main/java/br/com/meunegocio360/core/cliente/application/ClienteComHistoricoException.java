package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-05 e RNF-097: cliente com vínculos só pode ser inativado. */
public class ClienteComHistoricoException extends ApiException {

  public ClienteComHistoricoException() {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "CUSTOMER_HAS_HISTORY",
        "cliente-com-historico",
        "Cliente com histórico",
        "O cliente possui vendas ou ordens de serviço vinculadas e não pode ser excluído."
            + " Inative o cliente.");
  }
}
