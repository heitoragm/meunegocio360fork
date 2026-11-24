package br.com.meunegocio360.core.shared.web;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * Formato padrão das listagens paginadas (RNF-BE-13, RNF-054): {@code { content, page, size,
 * totalElements, totalPages }}.
 */
public record PaginaResponse<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {

  public static <E, T> PaginaResponse<T> de(Page<E> pagina, Function<E, T> conversor) {
    return new PaginaResponse<>(
        pagina.getContent().stream().map(conversor).toList(),
        pagina.getNumber(),
        pagina.getSize(),
        pagina.getTotalElements(),
        pagina.getTotalPages());
  }
}
