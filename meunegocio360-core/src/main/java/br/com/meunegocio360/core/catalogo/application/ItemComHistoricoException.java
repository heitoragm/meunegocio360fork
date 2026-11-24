package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-09 e RNF-097: item com vínculos só pode ser inativado. */
public class ItemComHistoricoException extends ApiException {

  public ItemComHistoricoException() {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "ITEM_HAS_HISTORY",
        "item-com-historico",
        "Item com histórico",
        "O item possui vendas ou compras vinculadas e não pode ser excluído. Inative o item.");
  }
}
