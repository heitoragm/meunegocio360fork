package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.shared.web.ApiException;
import org.springframework.http.HttpStatus;

/** RN-06: nome de categoria único na empresa, sem distinguir maiúsculas e acentos. */
public class NomeDeCategoriaDuplicadoException extends ApiException {

  public NomeDeCategoriaDuplicadoException() {
    super(
        HttpStatus.CONFLICT,
        "CATEGORY_NAME_DUPLICATE",
        "nome-de-categoria-duplicado",
        "Categoria já cadastrada",
        "Já existe uma categoria com este nome (sem considerar maiúsculas e acentos). Ela pode"
            + " estar inativa.");
  }
}
