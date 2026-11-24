package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo de POST /categorias (RN-06). */
public record CategoriaRequest(
    @Schema(example = "Informática")
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
        String nome) {

  public CategoriaRequest {
    nome = Textos.aparar(nome);
  }
}
