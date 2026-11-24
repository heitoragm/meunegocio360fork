package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.domain.DadosDoItem;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Corpo de PUT /itens-catalogo/{id}: todos os campos do cadastro mais a {@code versao} lida. O
 * {@code tipo} DEVE ser o atual (RN-02).
 */
public record AtualizarItemCatalogoRequest(
    @Schema(
            example = "PRODUTO",
            allowableValues = {"PRODUTO", "SERVICO"})
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = "PRODUTO|SERVICO", message = "deve ser PRODUTO ou SERVICO")
        String tipo,
    @Schema(example = "MSE-01") @Size(max = 40, message = "deve ter no máximo 40 caracteres")
        String codigo,
    @Schema(example = "Mouse sem fio")
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 150, message = "deve ter entre 2 e 150 caracteres")
        String nome,
    @Schema(example = "Mouse óptico sem fio, 1600 dpi")
        @Size(max = 500, message = "deve ter no máximo 500 caracteres")
        String descricao,
    @Schema(example = "UN")
        @Pattern(
            regexp = CamposDoItem.FORMATO_UNIDADE,
            message = "deve ter de 1 a 6 letras maiúsculas")
        String unidade,
    @Schema(example = "79.90")
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = CamposDoItem.FORMATO_PRECO, message = CamposDoItem.MENSAGEM_PRECO)
        String preco,
    @Schema(example = "2f0a8c9e-1b3d-4e5f-a6b7-c8d9e0f1a2b3") UUID categoriaId,
    @Schema(example = "0", description = "Versão lida na última consulta")
        @NotNull(message = "é obrigatória")
        @PositiveOrZero(message = "deve ser zero ou positiva")
        Long versao) {

  public AtualizarItemCatalogoRequest {
    tipo = Textos.aparar(tipo);
    codigo = Textos.opcional(codigo);
    nome = Textos.aparar(nome);
    descricao = Textos.opcional(descricao);
    unidade = Textos.opcional(unidade);
    preco = Textos.aparar(preco);
  }

  TipoItem tipoDoItem() {
    return CamposDoItem.tipo(tipo);
  }

  DadosDoItem paraDados() {
    return CamposDoItem.dados(codigo, nome, descricao, unidade, preco);
  }
}
