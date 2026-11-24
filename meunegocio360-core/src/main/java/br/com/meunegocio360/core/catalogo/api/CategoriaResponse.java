package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.application.FichaDaCategoria;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/** Categoria como exposta na API (RNF-BE-02). */
public record CategoriaResponse(
    @Schema(example = "2f0a8c9e-1b3d-4e5f-a6b7-c8d9e0f1a2b3") UUID id,
    @Schema(example = "Informática") String nome,
    @Schema(example = "true") boolean ativo,
    @Schema(example = "2026-11-18T14:30:00Z") Instant criadoEm,
    @Schema(example = "2026-11-18T14:30:00Z") Instant atualizadoEm,
    @Schema(example = "0") long versao) {

  static CategoriaResponse de(FichaDaCategoria ficha) {
    return new CategoriaResponse(
        ficha.id(),
        ficha.nome(),
        ficha.ativo(),
        ficha.criadoEm(),
        ficha.atualizadoEm(),
        ficha.versao());
  }
}
