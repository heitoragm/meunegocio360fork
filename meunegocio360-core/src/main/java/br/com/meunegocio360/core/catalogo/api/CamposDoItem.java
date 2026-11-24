package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.domain.DadosDoItem;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import java.math.BigDecimal;

/**
 * Regras de formato comuns ao cadastro e à edição de itens (RN-03, RN-04) e conversão do preço, que
 * trafega como decimal em string (RNF-033), nunca como número de ponto flutuante.
 */
final class CamposDoItem {

  /** Até 10 dígitos inteiros e 2 decimais, com ponto; sem sinal (o preço não é negativo). */
  static final String FORMATO_PRECO = "\\d{1,10}(\\.\\d{1,2})?";

  static final String MENSAGEM_PRECO =
      "deve ser um decimal não negativo em string, com ponto e até 2 casas (ex.: \"89.90\"),"
          + " até 9999999999.99";

  static final String FORMATO_UNIDADE = "[A-Z]{1,6}";

  private CamposDoItem() {}

  /** O formato já foi validado: a conversão é exata, sem arredondamento. */
  static DadosDoItem dados(
      String codigo, String nome, String descricao, String unidade, String preco) {
    return new DadosDoItem(codigo, nome, descricao, unidade, new BigDecimal(preco).setScale(2));
  }

  static TipoItem tipo(String tipo) {
    return TipoItem.valueOf(tipo);
  }

  /** Sempre com 2 casas, sem notação científica: {@code "89.90"}, {@code "0.00"}. */
  static String formatar(BigDecimal preco) {
    return preco.setScale(2).toPlainString();
  }
}
