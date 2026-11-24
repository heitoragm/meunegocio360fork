package br.com.meunegocio360.core.fornecedor.api;

import br.com.meunegocio360.core.fornecedor.application.AlterarSituacaoDoFornecedor;
import br.com.meunegocio360.core.fornecedor.application.AtualizarFornecedor;
import br.com.meunegocio360.core.fornecedor.application.CadastrarFornecedor;
import br.com.meunegocio360.core.fornecedor.application.ConsultarFornecedor;
import br.com.meunegocio360.core.fornecedor.application.ExcluirFornecedor;
import br.com.meunegocio360.core.fornecedor.application.ListarFornecedores;
import br.com.meunegocio360.core.shared.config.OpenApiConfig;
import br.com.meunegocio360.core.shared.web.PaginaResponse;
import br.com.meunegocio360.core.shared.web.Paginacao;
import br.com.meunegocio360.core.shared.web.SituacaoDoFiltro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fornecedores da empresa ativa (RF-BE-004). Matriz de autorização (RNF-BE-47): ADMIN e
 * OPERADOR_FINANCEIRO em todas as operações; OPERADOR_COMERCIAL recebe 403 (RN-05).
 */
@RestController
@RequestMapping("/api/v1/fornecedores")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR_FINANCEIRO')")
@Tag(name = "Fornecedores", description = "Cadastro, busca, situação e exclusão condicionada")
@SecurityRequirement(name = OpenApiConfig.BEARER)
@ApiResponse(
    responseCode = "401",
    description = "Token de acesso ausente, inválido ou expirado",
    content = @Content(mediaType = FornecedorController.PROBLEM_JSON))
@ApiResponse(
    responseCode = "403",
    description = "Perfil sem permissão (OPERADOR_COMERCIAL)",
    content =
        @Content(
            mediaType = FornecedorController.PROBLEM_JSON,
            examples = @ExampleObject(value = ExemplosDeErro.ACESSO_NEGADO)))
public class FornecedorController {

  static final String PROBLEM_JSON = "application/problem+json";
  private static final String EMPRESA_NAO_ATIVA = "#/components/responses/EmpresaNaoAtiva";

  /** RNF-BE-16: o {@code sort} só aceita estes campos. */
  private static final Map<String, String> ORDENACOES =
      Map.of("nome", "nome", "criadoEm", "criadoEm");

  private final ListarFornecedores listarFornecedores;
  private final ConsultarFornecedor consultarFornecedor;
  private final CadastrarFornecedor cadastrarFornecedor;
  private final AtualizarFornecedor atualizarFornecedor;
  private final AlterarSituacaoDoFornecedor alterarSituacaoDoFornecedor;
  private final ExcluirFornecedor excluirFornecedor;

  public FornecedorController(
      ListarFornecedores listarFornecedores,
      ConsultarFornecedor consultarFornecedor,
      CadastrarFornecedor cadastrarFornecedor,
      AtualizarFornecedor atualizarFornecedor,
      AlterarSituacaoDoFornecedor alterarSituacaoDoFornecedor,
      ExcluirFornecedor excluirFornecedor) {
    this.listarFornecedores = listarFornecedores;
    this.consultarFornecedor = consultarFornecedor;
    this.cadastrarFornecedor = cadastrarFornecedor;
    this.atualizarFornecedor = atualizarFornecedor;
    this.alterarSituacaoDoFornecedor = alterarSituacaoDoFornecedor;
    this.excluirFornecedor = excluirFornecedor;
  }

  @GetMapping
  @Operation(
      summary = "Lista e busca fornecedores",
      description =
          "Busca por nome (contém, sem distinguir maiúsculas e acentos) ou por documento (começa"
              + " com os dígitos, com ou sem máscara). Por padrão, só ativos e ordenados por nome.")
  @ApiResponse(responseCode = "200", description = "Página de fornecedores")
  @ApiResponse(
      responseCode = "400",
      description = "Parâmetro inválido (page, size, sort ou situacao)",
      content = @Content(mediaType = PROBLEM_JSON))
  public PaginaResponse<FornecedorResponse> listar(
      @Parameter(description = "Nome ou documento", example = "distribuidora")
          @RequestParam(required = false)
          @Size(max = 150, message = "deve ter no máximo 150 caracteres")
          String q,
      @Parameter(description = "ATIVO, INATIVO ou TODOS") @RequestParam(defaultValue = "ATIVO")
          SituacaoDoFiltro situacao,
      @Parameter(description = "Página, a partir de 0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Itens por página, de 1 a 100") @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "nome ou criadoEm, opcionalmente com ,asc ou ,desc")
          @RequestParam(defaultValue = "nome")
          String sort) {
    return PaginaResponse.de(
        listarFornecedores.executar(q, situacao, Paginacao.de(page, size, sort, ORDENACOES)),
        FornecedorResponse::de);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta um fornecedor, ativo ou inativo")
  @ApiResponse(responseCode = "200", description = "Fornecedor")
  @ApiResponse(
      responseCode = "404",
      description = "Fornecedor inexistente na empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.NAO_ENCONTRADO)))
  public FornecedorResponse consultar(@PathVariable UUID id) {
    return FornecedorResponse.de(consultarFornecedor.executar(id));
  }

  @PostMapping
  @Operation(
      summary = "Cadastra um fornecedor",
      description = "O fornecedor pertence à empresa ativa, resolvida no servidor, e nasce ativo.")
  @ApiResponse(
      responseCode = "201",
      description = "Fornecedor cadastrado",
      headers = @Header(name = HttpHeaders.LOCATION, description = "/api/v1/fornecedores/{id}"))
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.DOCUMENTO_INVALIDO)))
  @ApiResponse(
      responseCode = "409",
      description = "Documento já cadastrado na empresa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.DOCUMENTO_DUPLICADO)))
  @ApiResponse(responseCode = "422", ref = EMPRESA_NAO_ATIVA)
  public ResponseEntity<FornecedorResponse> cadastrar(
      @Valid @RequestBody FornecedorRequest requisicao) {
    FornecedorResponse fornecedor =
        FornecedorResponse.de(cadastrarFornecedor.executar(requisicao.paraDados()));
    return ResponseEntity.created(URI.create("/api/v1/fornecedores/" + fornecedor.id()))
        .body(fornecedor);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Edita um fornecedor", description = "Exige a versao lida.")
  @ApiResponse(responseCode = "200", description = "Fornecedor atualizado")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "404",
      description = "Fornecedor inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada ou documento já cadastrado",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = {
                @ExampleObject(name = "Versão desatualizada", value = ExemplosDeErro.VERSAO),
                @ExampleObject(
                    name = "Documento duplicado",
                    value = ExemplosDeErro.DOCUMENTO_DUPLICADO)
              }))
  @ApiResponse(responseCode = "422", ref = EMPRESA_NAO_ATIVA)
  public FornecedorResponse atualizar(
      @PathVariable UUID id, @Valid @RequestBody AtualizarFornecedorRequest requisicao) {
    return FornecedorResponse.de(
        atualizarFornecedor.executar(id, requisicao.paraDados(), requisicao.versao()));
  }

  @PatchMapping("/{id}/situacao")
  @Operation(
      summary = "Inativa ou reativa um fornecedor",
      description = "Idempotente: pedir a situação atual não altera nada.")
  @ApiResponse(responseCode = "200", description = "Situação do fornecedor")
  @ApiResponse(
      responseCode = "404",
      description = "Fornecedor inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.VERSAO)))
  @ApiResponse(responseCode = "422", ref = EMPRESA_NAO_ATIVA)
  public FornecedorResponse alterarSituacao(
      @PathVariable UUID id, @Valid @RequestBody SituacaoFornecedorRequest requisicao) {
    return FornecedorResponse.de(
        alterarSituacaoDoFornecedor.executar(id, requisicao.ativo(), requisicao.versao()));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Exclui um fornecedor sem vínculos",
      description = "Fornecedor com pedidos de compra ou contas a pagar só pode ser inativado.")
  @ApiResponse(responseCode = "204", description = "Fornecedor excluído")
  @ApiResponse(
      responseCode = "404",
      description = "Fornecedor inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "422",
      description = "Fornecedor com histórico (SUPPLIER_HAS_HISTORY) ou sem empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.COM_HISTORICO)))
  public ResponseEntity<Void> excluir(@PathVariable UUID id) {
    excluirFornecedor.executar(id);
    return ResponseEntity.noContent().build();
  }
}
