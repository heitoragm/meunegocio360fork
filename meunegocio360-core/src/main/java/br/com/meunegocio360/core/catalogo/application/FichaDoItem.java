package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Visão do item devolvida pelos casos de uso. Não tem estoque mínimo (RN-07): a entidade nem mapeia
 * a coluna.
 */
public record FichaDoItem(
    UUID id,
    TipoItem tipo,
    String codigo,
    String nome,
    String descricao,
    String unidade,
    BigDecimal preco,
    CategoriaDoItem categoria,
    boolean ativo,
    Instant criadoEm,
    Instant atualizadoEm,
    long versao) {

  /** Resumo da categoria dentro do item: {@code { id, nome }}. */
  public record CategoriaDoItem(UUID id, String nome) {}

  /** Deve ser chamado dentro da transação: a categoria é carregada sob demanda. */
  static FichaDoItem de(ItemCatalogo item) {
    Categoria categoria = item.getCategoria();
    return new FichaDoItem(
        item.getId(),
        item.getTipo(),
        item.getCodigo(),
        item.getNome(),
        item.getDescricao(),
        item.getUnidade(),
        item.getPreco(),
        categoria == null ? null : new CategoriaDoItem(categoria.getId(), categoria.getNome()),
        item.isAtivo(),
        item.getCriadoEm(),
        item.getAtualizadoEm(),
        item.getVersao());
  }
}
