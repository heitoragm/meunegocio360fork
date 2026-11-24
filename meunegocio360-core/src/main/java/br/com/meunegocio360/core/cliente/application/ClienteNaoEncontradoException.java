package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** Cliente inexistente na empresa ativa (inclusive o de outra empresa, que não é visível). */
public class ClienteNaoEncontradoException extends ApiException {

  public ClienteNaoEncontradoException() {
    super(
        HttpStatus.NOT_FOUND,
        "CUSTOMER_NOT_FOUND",
        "cliente-nao-encontrado",
        "Cliente não encontrado",
        "O cliente informado não existe.");
  }
}
