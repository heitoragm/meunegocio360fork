package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.application.AlterarSituacaoDoItem;
import br.com.meunegocio360.core.catalogo.application.AtualizarItemCatalogo;
import br.com.meunegocio360.core.catalogo.application.CadastrarItemCatalogo;
import br.com.meunegocio360.core.catalogo.application.ConsultarItemCatalogo;
import br.com.meunegocio360.core.catalogo.application.ExcluirItemCatalogo;
import br.com.meunegocio360.core.catalogo.application.ListarItensCatalogo;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
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
 * Itens do catálogo (RF-BE-005): produtos e serviços no mesmo recurso, distinguidos por {@code
 * tipo}. ADMIN e OPERADOR_COMERCIAL; OPERADOR_FINANCEIRO recebe 403 (RNF-BE-47).
 */
@RestController
@RequestMapping("/api/v1/itens-catalogo")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR_COMERCIAL')")
@Tag(name = "Catálogo: itens", description = "Produtos e serviços vendáveis")
@SecurityRequirement(name = OpenApiConfig.BEARER)
@ApiResponse(
    responseCode = "401",
    description = "Token de acesso ausente, inválido ou expirado",
    content = @Content(mediaType = ItemCatalogoController.PROBLEM_JSON))
@ApiResponse(
    responseCode = "403",
    description = "Perfil sem permissão (OPERADOR_FINANCEIRO)",
    content =
        @Content(
            mediaType = ItemCatalogoController.PROBLEM_JSON,
            examples = @ExampleObject(value = ExemplosDeErro.ACESSO_NEGADO)))
public class ItemCatalogoController {

  static final String PROBLEM_JSON = "application/problem+json";
  static final String EMPRESA_NAO_ATIVA = "#/components/responses/EmpresaNaoAtiva";

  /** RNF-BE-16: o {@code sort} só aceita estes campos. */
  private static final Map<String, String> ORDENACOES =
      Map.of("nome", "nome", "preco", "preco", "criadoEm", "criadoEm");

  private final ListarItensCatalogo listar;
  private final ConsultarItemCatalogo consultar;
  private final CadastrarItemCatalogo cadastrar;
  private final AtualizarItemCatalogo atualizar;
  private final AlterarSituacaoDoItem alterarSituacao;
  private final ExcluirItemCatalogo excluir;

  public ItemCatalogoController(
      ListarItensCatalogo listar,
      ConsultarItemCatalogo consultar,
      CadastrarItemCatalogo cadastrar,
      AtualizarItemCatalogo atualizar,
      AlterarSituacaoDoItem alterarSituacao,
      ExcluirItemCatalogo excluir) {
    this.listar = listar;
    this.consultar = consultar;
    this.cadastrar = cadastrar;
    this.atualizar = atualizar;
    this.alterarSituacao = alterarSituacao;
    this.excluir = excluir;
  }

  @GetMapping
  @Operation(
      summary = "Lista e busca itens do catálogo",
      description =
          "Filtros combinados por E. tipo=PRODUTO nunca retorna serviços: telas e serviços de"
              + " estoque DEVEM consultar com tipo=PRODUTO. Por padrão, só ativos e por nome.")
  @ApiResponse(responseCode = "200", description = "Página de itens")
  @ApiResponse(
      responseCode = "400",
      description = "Parâmetro inválido (tipo, situacao, page, size, sort)",
      content = @Content(mediaType = PROBLEM_JSON))
  public PaginaResponse<ItemCatalogoResponse> listar(
      @Parameter(description = "Nome ou código (contém)", example = "mouse")
          @RequestParam(required = false)
          @Size(max = 150, message = "deve ter no máximo 150 caracteres")
          String q,
      @Parameter(description = "PRODUTO ou SERVICO") @RequestParam(required = false) TipoItem tipo,
      @Parameter(description = "Id da categoria") @RequestParam(required = false) UUID categoriaId,
      @Parameter(description = "ATIVO, INATIVO ou TODOS") @RequestParam(defaultValue = "ATIVO")
          SituacaoDoFiltro situacao,
      @Parameter(description = "Página, a partir de 0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Itens por página, de 1 a 100") @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "nome, preco ou criadoEm, opcionalmente com ,asc ou ,desc")
          @RequestParam(defaultValue = "nome")
          String sort) {
    return PaginaResponse.de(
        listar.executar(q, tipo, categoriaId, situacao, Paginacao.de(page, size, sort, ORDENACOES)),
        ItemCatalogoResponse::de);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta um item do catálogo, ativo ou inativo")
  @ApiResponse(responseCode = "200", description = "Item")
  @ApiResponse(
      responseCode = "404",
      description = "Item inexistente na empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.ITEM_NAO_ENCONTRADO)))
  public ItemCatalogoResponse consultar(@PathVariable UUID id) {
    return ItemCatalogoResponse.de(consultar.executar(id));
  }

  @PostMapping
  @Operation(
      summary = "Cadastra um produto ou serviço",
      description =
          "Unidade padrão: UN (produto) e H (serviço). preco é decimal em string, não negativo.")
  @ApiResponse(
      responseCode = "201",
      description = "Item cadastrado",
      headers = @Header(name = HttpHeaders.LOCATION, description = "/api/v1/itens-catalogo/{id}"))
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.PRECO_INVALIDO)))
  @ApiResponse(
      responseCode = "409",
      description = "Código já usado na empresa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CODIGO_DUPLICADO)))
  @ApiResponse(
      responseCode = "422",
      description = "Categoria inexistente ou inativa, ou sem empresa ativa (COMPANY_NOT_ACTIVE)",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CATEGORIA_INVALIDA)))
  public ResponseEntity<ItemCatalogoResponse> cadastrar(
      @Valid @RequestBody ItemCatalogoRequest requisicao) {
    ItemCatalogoResponse item =
        ItemCatalogoResponse.de(
            cadastrar.executar(
                requisicao.tipoDoItem(), requisicao.paraDados(), requisicao.categoriaId()));
    return ResponseEntity.created(URI.create("/api/v1/itens-catalogo/" + item.id())).body(item);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Edita um item do catálogo",
      description = "Exige a versao lida. O tipo enviado DEVE ser igual ao atual.")
  @ApiResponse(responseCode = "200", description = "Item atualizado")
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "404",
      description = "Item inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada ou código já usado",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = {
                @ExampleObject(name = "Versão desatualizada", value = ExemplosDeErro.VERSAO),
                @ExampleObject(name = "Código duplicado", value = ExemplosDeErro.CODIGO_DUPLICADO)
              }))
  @ApiResponse(
      responseCode = "422",
      description = "Tipo alterado, categoria inválida ou sem empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = {
                @ExampleObject(name = "Tipo imutável", value = ExemplosDeErro.TIPO_IMUTAVEL),
                @ExampleObject(
                    name = "Categoria inválida",
                    value = ExemplosDeErro.CATEGORIA_INVALIDA)
              }))
  public ItemCatalogoResponse atualizar(
      @PathVariable UUID id, @Valid @RequestBody AtualizarItemCatalogoRequest requisicao) {
    return ItemCatalogoResponse.de(
        atualizar.executar(
            id,
            requisicao.tipoDoItem(),
            requisicao.paraDados(),
            requisicao.categoriaId(),
            requisicao.versao()));
  }

  @PatchMapping("/{id}/situacao")
  @Operation(summary = "Inativa ou reativa um item", description = "Idempotente.")
  @ApiResponse(responseCode = "200", description = "Situação do item")
  @ApiResponse(
      responseCode = "404",
      description = "Item inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.VERSAO)))
  @ApiResponse(responseCode = "422", ref = EMPRESA_NAO_ATIVA)
  public ItemCatalogoResponse alterarSituacao(
      @PathVariable UUID id, @Valid @RequestBody SituacaoCatalogoRequest requisicao) {
    return ItemCatalogoResponse.de(
        alterarSituacao.executar(id, requisicao.ativo(), requisicao.versao()));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Exclui um item sem vínculos",
      description = "Item com vendas ou compras só pode ser inativado.")
  @ApiResponse(responseCode = "204", description = "Item excluído")
  @ApiResponse(
      responseCode = "404",
      description = "Item inexistente",
      content = @Content(mediaType = PROBLEM_JSON))
  @ApiResponse(
      responseCode = "422",
      description = "Item com histórico (ITEM_HAS_HISTORY) ou sem empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.ITEM_COM_HISTORICO)))
  public ResponseEntity<Void> excluir(@PathVariable UUID id) {
    excluir.executar(id);
    return ResponseEntity.noContent().build();
  }
}
