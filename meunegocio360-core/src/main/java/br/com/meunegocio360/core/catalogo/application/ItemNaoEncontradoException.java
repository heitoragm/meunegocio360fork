package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** Item inexistente na empresa ativa. */
public class ItemNaoEncontradoException extends ApiException {

  public ItemNaoEncontradoException() {
    super(
        HttpStatus.NOT_FOUND,
        "ITEM_NOT_FOUND",
        "item-nao-encontrado",
        "Item não encontrado",
        "O item do catálogo informado não existe.");
  }
}
