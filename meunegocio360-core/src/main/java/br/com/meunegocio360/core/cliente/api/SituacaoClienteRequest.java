package br.com.meunegocio360.core.cliente.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Corpo de PATCH /clientes/{id}/situacao. */
public record SituacaoClienteRequest(
    @Schema(example = "false", description = "false inativa; true reativa")
        @NotNull(message = "é obrigatório")
        Boolean ativo,
    @Schema(example = "2", description = "Versão lida na última consulta")
        @NotNull(message = "é obrigatória")
        @PositiveOrZero(message = "deve ser zero ou positiva")
        Long versao) {}
