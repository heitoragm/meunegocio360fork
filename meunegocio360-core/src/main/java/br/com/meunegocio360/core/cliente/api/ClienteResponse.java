package br.com.meunegocio360.core.cliente.api;

import br.com.meunegocio360.core.cliente.application.FichaDoCliente;
import br.com.meunegocio360.core.cliente.domain.TipoPessoa;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/** Cliente como exposto na API (RNF-BE-02); sem empresa_id. Datas em ISO 8601 UTC (RNF-033). */
public record ClienteResponse(
    @Schema(example = "6c1f0a52-3b7e-4f0e-9a3e-2f7a1d5b8c90") UUID id,
    @Schema(example = "FISICA") TipoPessoa tipoPessoa,
    @Schema(example = "Maria da Silva") String nome,
    @Schema(example = "52998224725") String documento,
    @Schema(example = "maria@exemplo.com.br") String email,
    @Schema(example = "11987654321") String telefone,
    @Schema(example = "true") boolean ativo,
    @Schema(example = "2026-11-18T14:30:00Z") Instant criadoEm,
    @Schema(example = "2026-11-18T14:30:00Z") Instant atualizadoEm,
    @Schema(example = "0") long versao) {

  static ClienteResponse de(FichaDoCliente ficha) {
    return new ClienteResponse(
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
    return "ClienteResponse[id=" + id + "]";
  }
}
