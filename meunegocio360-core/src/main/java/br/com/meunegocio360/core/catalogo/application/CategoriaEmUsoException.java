package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-06: categoria com itens não pode ser excluída; o detail informa a quantidade. */
public class CategoriaEmUsoException extends ApiException {

  public CategoriaEmUsoException(long quantidadeDeItens) {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "CATEGORY_IN_USE",
        "categoria-em-uso",
        "Categoria em uso",
        "A categoria possui "
            + quantidadeDeItens
            + (quantidadeDeItens == 1 ? " item vinculado" : " itens vinculados")
            + " e não pode ser excluída. Inative a categoria.");
  }
}
