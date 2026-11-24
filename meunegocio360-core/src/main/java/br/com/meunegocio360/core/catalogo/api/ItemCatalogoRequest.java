package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.domain.DadosDoItem;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Corpo de POST /itens-catalogo. A empresa NUNCA vem daqui (RN-01) e não há estoque mínimo (RN-07).
 * Textos sofrem trim e opcionais vazios viram nulo.
 */
public record ItemCatalogoRequest(
    @Schema(
            example = "PRODUTO",
            allowableValues = {"PRODUTO", "SERVICO"})
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = "PRODUTO|SERVICO", message = "deve ser PRODUTO ou SERVICO")
        String tipo,
    @Schema(example = "MSE-01", description = "Opcional; único na empresa quando informado")
        @Size(max = 40, message = "deve ter no máximo 40 caracteres")
        String codigo,
    @Schema(example = "Mouse sem fio")
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 150, message = "deve ter entre 2 e 150 caracteres")
        String nome,
    @Schema(example = "Mouse óptico sem fio, 1600 dpi")
        @Size(max = 500, message = "deve ter no máximo 500 caracteres")
        String descricao,
    @Schema(example = "UN", description = "Padrão: UN para produto e H para serviço")
        @Pattern(
            regexp = CamposDoItem.FORMATO_UNIDADE,
            message = "deve ter de 1 a 6 letras maiúsculas")
        String unidade,
    @Schema(example = "89.90", description = "Decimal em string, não negativo, até 2 casas")
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = CamposDoItem.FORMATO_PRECO, message = CamposDoItem.MENSAGEM_PRECO)
        String preco,
    @Schema(example = "2f0a8c9e-1b3d-4e5f-a6b7-c8d9e0f1a2b3", description = "Opcional")
        UUID categoriaId) {

  public ItemCatalogoRequest {
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
