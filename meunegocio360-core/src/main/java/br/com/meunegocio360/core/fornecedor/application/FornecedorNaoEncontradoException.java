package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** Fornecedor inexistente na empresa ativa (inclusive o de outra empresa, que não é visível). */
public class FornecedorNaoEncontradoException extends ApiException {

  public FornecedorNaoEncontradoException() {
    super(
        HttpStatus.NOT_FOUND,
        "SUPPLIER_NOT_FOUND",
        "fornecedor-nao-encontrado",
        "Fornecedor não encontrado",
        "O fornecedor informado não existe.");
  }
}
