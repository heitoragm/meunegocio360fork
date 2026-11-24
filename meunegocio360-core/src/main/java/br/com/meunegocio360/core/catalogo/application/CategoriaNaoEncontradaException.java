package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** Categoria inexistente na empresa ativa (operações sobre a própria categoria). */
public class CategoriaNaoEncontradaException extends ApiException {

  public CategoriaNaoEncontradaException() {
    super(
        HttpStatus.NOT_FOUND,
        "CATEGORY_NOT_FOUND",
        "categoria-nao-encontrada",
        "Categoria não encontrada",
        "A categoria informada não existe.");
  }
}
