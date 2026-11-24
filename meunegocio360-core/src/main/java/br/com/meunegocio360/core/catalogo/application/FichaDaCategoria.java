package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import java.time.Instant;
import java.util.UUID;

/** Visão da categoria devolvida pelos casos de uso; a entidade JPA não sai desta camada. */
public record FichaDaCategoria(
    UUID id, String nome, boolean ativo, Instant criadoEm, Instant atualizadoEm, long versao) {

  static FichaDaCategoria de(Categoria categoria) {
    return new FichaDaCategoria(
        categoria.getId(),
        categoria.getNome(),
        categoria.isAtivo(),
        categoria.getCriadoEm(),
        categoria.getAtualizadoEm(),
        categoria.getVersao());
  }
}
