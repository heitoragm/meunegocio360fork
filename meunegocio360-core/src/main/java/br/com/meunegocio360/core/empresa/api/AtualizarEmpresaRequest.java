package br.com.meunegocio360.core.empresa.api;

import br.com.meunegocio360.core.empresa.domain.DadosCadastrais;
import br.com.meunegocio360.core.shared.validation.Cnpj;
import br.com.meunegocio360.core.shared.validation.FusoHorario;
import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/** Corpo de PUT /empresa/{id}: os campos do cadastro mais a {@code versao} lida (RNF-BE-28). */
public record AtualizarEmpresaRequest(
    @Schema(example = "Comércio Exemplo Ltda")
        @NotBlank(message = "é obrigatória")
        @Size(min = 3, max = 150, message = "deve ter entre 3 e 150 caracteres")
        String razaoSocial,
    @Schema(example = "Exemplo Informática")
        @Size(max = 150, message = "deve ter no máximo 150 caracteres")
        String nomeFantasia,
    @Schema(example = "11222333000181", description = "Somente os 14 dígitos, sem máscara")
        @NotBlank(message = "é obrigatório")
        @Cnpj
        String cnpj,
    @Schema(example = "contato@exemplo.com.br")
        @Email(message = "deve ser um e-mail válido")
        @Size(max = 160, message = "deve ter no máximo 160 caracteres")
        String email,
    @Schema(example = "11987654321", description = "DDD e número, 10 ou 11 dígitos, sem máscara")
        @Pattern(regexp = "\\d{10,11}", message = "deve ter 10 ou 11 dígitos, sem máscara")
        String telefone,
    @Schema(example = "America/Sao_Paulo", defaultValue = "America/Sao_Paulo") @FusoHorario
        String fusoHorario,
    @Schema(example = "BRL", defaultValue = "BRL", allowableValues = "BRL")
        @Pattern(regexp = "BRL", message = "nesta versão só é aceita a moeda BRL")
        String moeda,
    @Schema(example = "0", description = "Versão lida na última consulta")
        @NotNull(message = "é obrigatória")
        @PositiveOrZero(message = "deve ser zero ou positiva")
        Long versao) {

  public AtualizarEmpresaRequest {
    razaoSocial = Textos.aparar(razaoSocial);
    nomeFantasia = Textos.opcional(nomeFantasia);
    cnpj = Textos.aparar(cnpj);
    email = Textos.opcional(email);
    telefone = Textos.opcional(telefone);
    fusoHorario = Textos.opcional(fusoHorario);
    moeda = Textos.opcional(moeda);
  }

  DadosCadastrais paraDados() {
    return new DadosCadastrais(
        razaoSocial, nomeFantasia, cnpj, email, telefone, fusoHorario, moeda);
  }
}
