package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-05: a categoria associada deve existir na empresa ativa e estar ativa. */
public class CategoriaInvalidaException extends ApiException {

  public CategoriaInvalidaException() {
    super(
        HttpStatus.UNPROCESSABLE_CONTENT,
        "CATEGORY_INVALID",
        "categoria-invalida",
        "Categoria inválida",
        "A categoria informada não existe ou está inativa.");
  }
}
