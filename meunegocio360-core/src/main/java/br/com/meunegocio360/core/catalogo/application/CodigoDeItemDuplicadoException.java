package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-04: o código do item, quando informado, é único na empresa. */
public class CodigoDeItemDuplicadoException extends ApiException {

  public CodigoDeItemDuplicadoException() {
    super(
        HttpStatus.CONFLICT,
        "ITEM_CODE_DUPLICATE",
        "codigo-de-item-duplicado",
        "Código já usado",
        "Já existe um item do catálogo com este código. Ele pode estar inativo.");
  }
}
