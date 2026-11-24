package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/**
 * RN-02: o documento é único na empresa, entre fornecedores ativos e inativos. A unicidade é por
 * entidade: o mesmo documento pode existir como cliente.
 */
public class DocumentoDuplicadoException extends ApiException {

  public DocumentoDuplicadoException() {
    super(
        HttpStatus.CONFLICT,
        "SUPPLIER_DOCUMENT_DUPLICATE",
        "documento-de-fornecedor-duplicado",
        "Fornecedor já cadastrado",
        "Já existe um fornecedor com este documento. Ele pode estar inativo: consulte a listagem com"
            + " a situação \"todos\".");
  }
}
