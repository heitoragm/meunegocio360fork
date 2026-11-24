package br.com.meunegocio360.core.fornecedor.api;

import br.com.meunegocio360.core.fornecedor.application.FichaDoFornecedor;
import br.com.meunegocio360.core.shared.domain.TipoPessoa;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/** Fornecedor como exposto na API (RNF-BE-02); sem empresa_id. Datas em ISO 8601 UTC (RNF-033). */
public record FornecedorResponse(
    @Schema(example = "8a2d4c71-5e9f-4b3a-b1c6-7d0e2f3a9b45") UUID id,
    @Schema(example = "JURIDICA") TipoPessoa tipoPessoa,
    @Schema(example = "Distribuidora Alfa Ltda") String nome,
    @Schema(example = "11222333000181") String documento,
    @Schema(example = "compras@alfa.com.br") String email,
    @Schema(example = "11987654321") String telefone,
    @Schema(example = "true") boolean ativo,
    @Schema(example = "2026-11-18T14:30:00Z") Instant criadoEm,
    @Schema(example = "2026-11-18T14:30:00Z") Instant atualizadoEm,
    @Schema(example = "0") long versao) {

  static FornecedorResponse de(FichaDoFornecedor ficha) {
    return new FornecedorResponse(
        ficha.id(),
        ficha.tipoPessoa(),
        ficha.nome(),
        ficha.documento(),
        ficha.email(),
        ficha.telefone(),
        ficha.ativo(),
        ficha.criadoEm(),
        ficha.atualizadoEm(),
        ficha.versao());
  }

  /** Dados pessoais nunca em logs (RNF-025). */
  @Override
  public String toString() {
    return "FornecedorResponse[id=" + id + "]";
  }
}
