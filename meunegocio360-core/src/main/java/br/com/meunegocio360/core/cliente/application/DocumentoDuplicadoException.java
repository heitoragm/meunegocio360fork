package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-03: o documento é único na empresa, entre clientes ativos e inativos. */
public class DocumentoDuplicadoException extends ApiException {

  public DocumentoDuplicadoException() {
    super(
        HttpStatus.CONFLICT,
        "CUSTOMER_DOCUMENT_DUPLICATE",
        "documento-de-cliente-duplicado",
        "Cliente já cadastrado",
        "Já existe um cliente com este documento. Ele pode estar inativo: consulte a listagem com"
            + " a situação \"todos\".");
  }
}
