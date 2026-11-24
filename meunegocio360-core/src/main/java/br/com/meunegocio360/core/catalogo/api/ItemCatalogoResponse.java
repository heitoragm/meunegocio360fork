package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.application.FichaDoItem;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * Item como exposto na API (RNF-BE-02). {@code preco} é decimal em string (RNF-033) e NÃO há
 * estoque mínimo (RN-07).
 */
public record ItemCatalogoResponse(
    @Schema(example = "9d3e7c10-5c2a-4a8e-8d1b-3a9f6e2b7c44") UUID id,
    @Schema(example = "PRODUTO") TipoItem tipo,
    @Schema(example = "NOTE-I5-16") String codigo,
    @Schema(example = "Notebook 15\" i5 16 GB") String nome,
    @Schema(example = "Notebook para uso profissional") String descricao,
    @Schema(example = "UN") String unidade,
    @Schema(example = "3499.90", description = "Decimal em string, com 2 casas") String preco,
    CategoriaDoItemResponse categoria,
    @Schema(example = "true") boolean ativo,
    @Schema(example = "2026-11-18T14:30:00Z") Instant criadoEm,
    @Schema(example = "2026-11-18T14:30:00Z") Instant atualizadoEm,
    @Schema(example = "0") long versao) {

  /** {@code { id, nome }} da categoria do item, ou {@code null} sem categoria. */
  public record CategoriaDoItemResponse(
      @Schema(example = "2f0a8c9e-1b3d-4e5f-a6b7-c8d9e0f1a2b3") UUID id,
      @Schema(example = "Informática") String nome) {}

  static ItemCatalogoResponse de(FichaDoItem ficha) {
    return new ItemCatalogoResponse(
        ficha.id(),
        ficha.tipo(),
        ficha.codigo(),
        ficha.nome(),
        ficha.descricao(),
        ficha.unidade(),
        CamposDoItem.formatar(ficha.preco()),
        ficha.categoria() == null
            ? null
            : new CategoriaDoItemResponse(ficha.categoria().id(), ficha.categoria().nome()),
        ficha.ativo(),
        ficha.criadoEm(),
        ficha.atualizadoEm(),
        ficha.versao());
  }
}
