package br.com.meunegocio360.core.cliente.api;

import br.com.meunegocio360.core.cliente.domain.DadosDoCliente;
import br.com.meunegocio360.core.shared.validation.DocumentoFiscal;
import br.com.meunegocio360.core.shared.web.Textos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Corpo de POST /clientes. A empresa NUNCA vem daqui (RN-01). Textos sofrem trim, opcionais vazios
 * viram nulo e a máscara do documento é removida antes da validação (RN-02, RN-04).
 */
@DocumentoCompativel
public record ClienteRequest(
    @Schema(
            example = "FISICA",
            allowableValues = {"FISICA", "JURIDICA"})
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = "FISICA|JURIDICA", message = "deve ser FISICA ou JURIDICA")
        String tipoPessoa,
    @Schema(example = "Maria da Silva")
        @NotBlank(message = "é obrigatório")
        @Size(min = 2, max = 150, message = "deve ter entre 2 e 150 caracteres")
        String nome,
    @Schema(
            example = "52998224725",
            description = "CPF (11 dígitos) ou CNPJ (14 dígitos); a máscara é removida")
        @NotBlank(message = "é obrigatório")
        String documento,
    @Schema(example = "maria@exemplo.com.br")
        @Email(message = "deve ser um e-mail válido")
        @Size(max = 160, message = "deve ter no máximo 160 caracteres")
        String email,
    @Schema(example = "11987654321", description = "DDD e número, 10 ou 11 dígitos, sem máscara")
        @Pattern(regexp = "\\d{10,11}", message = "deve ter 10 ou 11 dígitos, sem máscara")
        String telefone)
    implements DocumentoCompativel.ComDocumento {

  public ClienteRequest {
    tipoPessoa = Textos.aparar(tipoPessoa);
    nome = Textos.aparar(nome);
    documento = DocumentoFiscal.removerMascara(documento);
    email = Textos.opcional(email);
    telefone = Textos.opcional(telefone);
  }

  DadosDoCliente paraDados() {
    return ClienteRequests.dados(tipoPessoa, nome, documento, email, telefone);
  }

  /** Dados pessoais nunca em logs (RNF-025). */
  @Override
  public String toString() {
    return "ClienteRequest[tipoPessoa=" + tipoPessoa + "]";
  }
}
