package br.com.meunegocio360.core.shared.web;

import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Monta a paginação a partir de {@code page}, {@code size} e {@code sort} (RNF-BE-13, RNF-BE-16). O
 * {@code sort} só aceita os campos da lista permitida de cada listagem, no formato {@code campo} ou
 * {@code campo,asc|desc}; o id entra como desempate, para que a ordem entre páginas seja estável.
 */
public final class Paginacao {

  public static final int TAMANHO_PADRAO = 20;
  public static final int TAMANHO_MAXIMO = 100;

  private Paginacao() {}

  /**
   * @param camposPermitidos nome do campo na API → nome da propriedade na entidade
   */
  public static Pageable de(int page, int size, String sort, Map<String, String> camposPermitidos) {
    if (page < 0) {
      throw new ParametroInvalidoException("page", "deve ser maior ou igual a 0");
    }
    if (size < 1 || size > TAMANHO_MAXIMO) {
      throw new ParametroInvalidoException("size", "deve estar entre 1 e " + TAMANHO_MAXIMO);
    }
    return PageRequest.of(page, size, ordenacao(sort, camposPermitidos).and(Sort.by("id")));
  }

  private static Sort ordenacao(String sort, Map<String, String> camposPermitidos) {
    String[] partes = sort.trim().split("\\s*,\\s*");
    String propriedade = camposPermitidos.get(partes[0]);
    if (propriedade == null || partes.length > 2) {
      throw new ParametroInvalidoException(
          "sort", "aceita apenas " + String.join(" ou ", camposPermitidos.keySet()));
    }
    Sort.Direction direcao = Sort.Direction.ASC;
    if (partes.length == 2) {
      direcao =
          Sort.Direction.fromOptionalString(partes[1].toUpperCase(Locale.ROOT))
              .orElseThrow(
                  () -> new ParametroInvalidoException("sort", "direção deve ser asc ou desc"));
    }
    return Sort.by(direcao, propriedade);
  }
}
