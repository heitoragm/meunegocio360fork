package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-02: o tipo (produto ou serviço) não muda depois da criação. */
public class TipoDeItemImutavelException extends ApiException {

  public TipoDeItemImutavelException() {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "ITEM_TYPE_IMMUTABLE",
        "tipo-de-item-imutavel",
        "Tipo não pode ser alterado",
        "O tipo do item (produto ou serviço) não pode ser alterado depois do cadastro. Cadastre"
            + " um novo item e inative este.");
  }
}
