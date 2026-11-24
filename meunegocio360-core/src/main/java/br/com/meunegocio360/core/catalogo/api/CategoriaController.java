package br.com.meunegocio360.core.catalogo.api;

import br.com.meunegocio360.core.catalogo.application.AlterarSituacaoDaCategoria;
import br.com.meunegocio360.core.catalogo.application.CadastrarCategoria;
import br.com.meunegocio360.core.catalogo.application.ConsultarCategoria;
import br.com.meunegocio360.core.catalogo.application.ExcluirCategoria;
import br.com.meunegocio360.core.catalogo.application.ListarCategorias;
import br.com.meunegocio360.core.catalogo.application.RenomearCategoria;
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
 * Categorias do catálogo (RF-BE-005, RN-06). ADMIN e OPERADOR_COMERCIAL; OPERADOR_FINANCEIRO recebe
 * 403 (RNF-BE-47).
 */
@RestController
@RequestMapping("/api/v1/categorias")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR_COMERCIAL')")
@Tag(name = "Catálogo: categorias", description = "Categorias opcionais dos itens")
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
public class CategoriaController {

  private static final String PROBLEM_JSON = ItemCatalogoController.PROBLEM_JSON;

  /** RNF-BE-16: categorias só ordenam por nome. */
  private static final Map<String, String> ORDENACOES = Map.of("nome", "nome");

  private final ListarCategorias listar;
  private final ConsultarCategoria consultar;
  private final CadastrarCategoria cadastrar;
  private final RenomearCategoria renomear;
  private final AlterarSituacaoDaCategoria alterarSituacao;
  private final ExcluirCategoria excluir;

  public CategoriaController(
      ListarCategorias listar,
      ConsultarCategoria consultar,
      CadastrarCategoria cadastrar,
      RenomearCategoria renomear,
      AlterarSituacaoDaCategoria alterarSituacao,
      ExcluirCategoria excluir) {
    this.listar = listar;
    this.consultar = consultar;
    this.cadastrar = cadastrar;
    this.renomear = renomear;
    this.alterarSituacao = alterarSituacao;
    this.excluir = excluir;
  }

  @GetMapping
  @Operation(summary = "Lista e busca categorias", description = "Por padrão, só ativas.")
  @ApiResponse(responseCode = "200", description = "Página de categorias")
  public PaginaResponse<CategoriaResponse> listar(
      @Parameter(description = "Parte do nome", example = "informatica")
          @RequestParam(required = false)
          @Size(max = 80, message = "deve ter no máximo 80 caracteres")
          String q,
      @Parameter(description = "ATIVO, INATIVO ou TODOS") @RequestParam(defaultValue = "ATIVO")
          SituacaoDoFiltro situacao,
      @Parameter(description = "Página, a partir de 0") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Itens por página, de 1 a 100") @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "nome, opcionalmente com ,asc ou ,desc")
          @RequestParam(defaultValue = "nome")
          String sort) {
    return PaginaResponse.de(
        listar.executar(q, situacao, Paginacao.de(page, size, sort, ORDENACOES)),
        CategoriaResponse::de);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Consulta uma categoria")
  @ApiResponse(responseCode = "200", description = "Categoria")
  @ApiResponse(
      responseCode = "404",
      description = "Categoria inexistente na empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CATEGORIA_NAO_ENCONTRADA)))
  public CategoriaResponse consultar(@PathVariable UUID id) {
    return CategoriaResponse.de(consultar.executar(id));
  }

  @PostMapping
  @Operation(summary = "Cadastra uma categoria")
  @ApiResponse(
      responseCode = "201",
      description = "Categoria cadastrada",
      headers = @Header(name = HttpHeaders.LOCATION, description = "/api/v1/categorias/{id}"))
  @ApiResponse(
      responseCode = "409",
      description = "Nome já usado na empresa (sem considerar maiúsculas e acentos)",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.NOME_DE_CATEGORIA_DUPLICADO)))
  @ApiResponse(responseCode = "422", ref = ItemCatalogoController.EMPRESA_NAO_ATIVA)
  public ResponseEntity<CategoriaResponse> cadastrar(
      @Valid @RequestBody CategoriaRequest requisicao) {
    CategoriaResponse categoria = CategoriaResponse.de(cadastrar.executar(requisicao.nome()));
    return ResponseEntity.created(URI.create("/api/v1/categorias/" + categoria.id()))
        .body(categoria);
  }

  @PutMapping("/{id}")
  @Operation(summary = "Renomeia uma categoria", description = "Exige a versao lida.")
  @ApiResponse(responseCode = "200", description = "Categoria renomeada")
  @ApiResponse(
      responseCode = "409",
      description = "Versão desatualizada ou nome já usado",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.NOME_DE_CATEGORIA_DUPLICADO)))
  public CategoriaResponse renomear(
      @PathVariable UUID id, @Valid @RequestBody RenomearCategoriaRequest requisicao) {
    return CategoriaResponse.de(renomear.executar(id, requisicao.nome(), requisicao.versao()));
  }

  @PatchMapping("/{id}/situacao")
  @Operation(
      summary = "Inativa ou reativa uma categoria",
      description = "Idempotente. Inativar não altera os itens que usam a categoria.")
  @ApiResponse(responseCode = "200", description = "Situação da categoria")
  public CategoriaResponse alterarSituacao(
      @PathVariable UUID id, @Valid @RequestBody SituacaoCatalogoRequest requisicao) {
    return CategoriaResponse.de(
        alterarSituacao.executar(id, requisicao.ativo(), requisicao.versao()));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Exclui uma categoria sem itens",
      description = "Com itens vinculados, a categoria só pode ser inativada.")
  @ApiResponse(responseCode = "204", description = "Categoria excluída")
  @ApiResponse(
      responseCode = "422",
      description = "Categoria com itens (CATEGORY_IN_USE) ou sem empresa ativa",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CATEGORIA_EM_USO)))
  public ResponseEntity<Void> excluir(@PathVariable UUID id) {
    excluir.executar(id);
    return ResponseEntity.noContent().build();
  }
}
