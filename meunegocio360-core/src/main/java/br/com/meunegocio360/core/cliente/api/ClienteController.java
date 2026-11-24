package br.com.meunegocio360.core.cliente.api;

import br.com.meunegocio360.core.cliente.application.AlterarSituacaoDoCliente;
import br.com.meunegocio360.core.cliente.application.AtualizarCliente;
import br.com.meunegocio360.core.cliente.application.CadastrarCliente;
import br.com.meunegocio360.core.cliente.application.ConsultarCliente;
import br.com.meunegocio360.core.cliente.application.ExcluirCliente;
import br.com.meunegocio360.core.cliente.application.ListarClientes;
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
 * Clientes da empresa ativa (RF-BE-003). Matriz de autorização (RNF-BE-47): ADMIN e
 * OPERADOR_COMERCIAL em todas as operações; OPERADOR_FINANCEIRO recebe 403.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR_COMERCIAL')")
@Tag(name = "Clientes", description = "Cadastro, busca, situação e exclusão condicionada")
@SecurityRequirement(name = OpenApiConfig.BEARER)
@ApiResponse(
    responseCode = "401",
    description = "Token de acesso ausente, inválido ou expirado",
    content = @Content(mediaType = ClienteController.PROBLEM_JSON))
@ApiResponse(
    responseCode = "403",
    description = "Perfil sem permissão (OPERADOR_FINANCEIRO)",
    content =
        @Content(
            mediaType = ClienteController.PROBLEM_JSON,
            examples = @ExampleObject(value = ExemplosDeErro.ACESSO_NEGADO)))
public class ClienteController {

  static final String PROBLEM_JSON = "application/problem+json";
  private static final String EMPRESA_NAO_ATIVA = "#/components/responses/EmpresaNaoAtiva";

  /** RNF-BE-16: o {@code sort} só aceita estes campos. */
  private static final Map<String, String> ORDENACOES =
      Map.of("nome", "nome", "criadoEm", "criadoEm");

  private final ListarClientes listarClientes;
  private final ConsultarCliente consultarCliente;
  private final CadastrarCliente cadastrarCliente;
  private final AtualizarCliente atualizarCliente;
  private final AlterarSituacaoDoCliente alterarSituacaoDoCliente;
  private final ExcluirCliente excluirCliente;

  public ClienteController(
      ListarClientes listarClientes,
      ConsultarCliente consultarCliente,
      CadastrarCliente cadastrarCliente,
      AtualizarCliente atualizarCliente,
      AlterarSituacaoDoCliente alterarSituacaoDoCliente,
      ExcluirCliente excluirCliente) {
    this.listarClientes = listarClientes;
    this.consultarCliente = consultarCliente;
    this.cadastrarCliente = cadastrarCliente;
    this.atualizarCliente = atualizarCliente;
    this.alterarSituacaoDoCliente = alterarSituacaoDoCliente;
    this.excluirCliente = excluirCliente;
  }

  @GetMapping
  @Operation(
      summary = "Lista e busca clientes",
      description =
          "Busca por nome (contém, sem distinguir maiúsculas e acentos) ou por documento (começa"
              + " com os dígitos, com ou sem máscara). Por padrão, só ativos e ordenados por nome.")
  @ApiResponse(responseCode = "200", description = "Página de clientes")
  @ApiResponse(
      responseCode = "400",
      description = "Parâmetro inválido (page, size, sort ou situacao)",
      content = @Content(mediaType = PROBLEM_JSON))
  public PaginaResponse<ClienteResponse> listar(
      @Parameter(description = "Nome ou documento", example = "maria")
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
        listarClientes.executar(q, situacao, Paginacao.de(page, size, sort, ORDENACOES)),
        ClienteResponse::de);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta um cliente, ativo ou inativo")
  @ApiResponse(responseCode = "200", description = "Cliente")
  @ApiResponse(
      responseCode = "404",
      description = "Cliente inexistente na empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.NAO_ENCONTRADO)))
  public ClienteResponse consultar(@PathVariable UUID id) {
    return ClienteResponse.de(consultarCliente.executar(id));
  }

  @PostMapping
  @Operation(
      summary = "Cadastra um cliente",
      description = "O cliente pertence à empresa ativa, resolvida no servidor, e nasce ativo.")
  @ApiResponse(
      responseCode = "201",
      description = "Cliente cadastrado",
      headers = @Header(name = HttpHeaders.LOCATION, description = "/api/v1/clientes/{id}"))
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
  public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody ClienteRequest requisicao) {
    ClienteResponse cliente = ClienteResponse.de(cadastrarCliente.executar(requisicao.paraDados()));
    return ResponseEntity.created(URI.create("/api/v1/clientes/" + cliente.id())).body(cliente);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Edita um cliente", description = "Exige a versao lida.")
  @ApiResponse(responseCode = "200", description = "Cliente atualizado")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "404",
      description = "Cliente inexistente",
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
  public ClienteResponse atualizar(
      @PathVariable UUID id, @Valid @RequestBody AtualizarClienteRequest requisicao) {
    return ClienteResponse.de(
        atualizarCliente.executar(id, requisicao.paraDados(), requisicao.versao()));
  }

  @PatchMapping("/{id}/situacao")
  @Operation(
      summary = "Inativa ou reativa um cliente",
      description = "Idempotente: pedir a situação atual não altera nada.")
  @ApiResponse(responseCode = "200", description = "Situação do cliente")
  @ApiResponse(
      responseCode = "404",
      description = "Cliente inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.VERSAO)))
  @ApiResponse(responseCode = "422", ref = EMPRESA_NAO_ATIVA)
  public ClienteResponse alterarSituacao(
      @PathVariable UUID id, @Valid @RequestBody SituacaoClienteRequest requisicao) {
    return ClienteResponse.de(
        alterarSituacaoDoCliente.executar(id, requisicao.ativo(), requisicao.versao()));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Exclui um cliente sem vínculos",
      description = "Cliente com vendas ou ordens de serviço só pode ser inativado.")
  @ApiResponse(responseCode = "204", description = "Cliente excluído")
  @ApiResponse(
      responseCode = "404",
      description = "Cliente inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "422",
      description = "Cliente com histórico (CUSTOMER_HAS_HISTORY) ou sem empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.COM_HISTORICO)))
  public ResponseEntity<Void> excluir(@PathVariable UUID id) {
    excluirCliente.executar(id);
    return ResponseEntity.noContent().build();
  }
}
