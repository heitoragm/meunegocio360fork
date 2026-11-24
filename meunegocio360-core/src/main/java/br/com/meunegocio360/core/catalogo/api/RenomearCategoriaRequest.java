package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Corpo de PUT /categorias/{id}: novo nome e a {@code versao} lida. */
public record RenomearCategoriaRequest(
    @Schema(example = "Informática e Acessórios")
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
        String nome,
    @Schema(example = "0", description = "Versão lida na última consulta")
        @NotNull(message = "é obrigatória")
        @PositiveOrZero(message = "deve ser zero ou positiva")
        Long versao) {

  public RenomearCategoriaRequest {
    nome = Textos.aparar(nome);
  }
}
