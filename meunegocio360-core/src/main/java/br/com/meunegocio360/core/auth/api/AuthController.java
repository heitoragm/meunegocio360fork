package br.com.meunegocio360.core.auth.api;

import br.com.meunegocio360.core.auth.application.AutenticarUsuario;
import br.com.meunegocio360.core.auth.application.ConsultarUsuarioAutenticado;
import br.com.meunegocio360.core.auth.application.EncerrarSessao;
import br.com.meunegocio360.core.auth.application.RefreshTokenInvalidoException;
import br.com.meunegocio360.core.auth.application.RenovarSessao;
import br.com.meunegocio360.core.auth.application.SessaoAutenticada;
import br.com.meunegocio360.core.shared.config.OpenApiConfig;
import br.com.meunegocio360.core.shared.security.VerificadorDeOrigem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticação (RF-BE-001). Controller fino: recebe DTO, delega ao caso de uso e
 * devolve DTO (RNF-BE-01).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Login, renovação de sessão, logout e usuário atual")
public class AuthController {

  private static final String PROBLEM_JSON = "application/problem+json";

  private final AutenticarUsuario autenticarUsuario;
  private final RenovarSessao renovarSessao;
  private final EncerrarSessao encerrarSessao;
  private final ConsultarUsuarioAutenticado consultarUsuarioAutenticado;
  private final CookieDeRefresh cookieDeRefresh;
  private final VerificadorDeOrigem verificadorDeOrigem;

  public AuthController(
      AutenticarUsuario autenticarUsuario,
      RenovarSessao renovarSessao,
      EncerrarSessao encerrarSessao,
      ConsultarUsuarioAutenticado consultarUsuarioAutenticado,
      CookieDeRefresh cookieDeRefresh,
      VerificadorDeOrigem verificadorDeOrigem) {
    this.autenticarUsuario = autenticarUsuario;
    this.renovarSessao = renovarSessao;
    this.encerrarSessao = encerrarSessao;
    this.consultarUsuarioAutenticado = consultarUsuarioAutenticado;
    this.cookieDeRefresh = cookieDeRefresh;
    this.verificadorDeOrigem = verificadorDeOrigem;
  }

  @PostMapping("/login")
  @Operation(
      summary = "Autentica por e-mail e senha",
      description =
          "Devolve o token de acesso no corpo e o refresh token somente no cookie HttpOnly"
              + " mn360_refresh.")
  @ApiResponse(
      responseCode = "200",
      description = "Autenticado",
      headers = @Header(name = HttpHeaders.SET_COOKIE, description = "Cookie mn360_refresh"))
  @ApiResponse(
      responseCode = "400",
      description = "Dados inválidos",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.VALIDACAO)))
  @ApiResponse(
      responseCode = "401",
      description = "Credenciais inválidas",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CREDENCIAIS_INVALIDAS)))
  @ApiResponse(
      responseCode = "429",
      description = "Conta temporariamente bloqueada",
      headers = @Header(name = HttpHeaders.RETRY_AFTER, description = "Segundos até o desbloqueio"),
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.CONTA_BLOQUEADA)))
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest requisicao) {
    SessaoAutenticada sessao = autenticarUsuario.executar(requisicao.email(), requisicao.senha());
    return comCookie(sessao);
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Renova a sessão",
      description =
          "Usa o cookie mn360_refresh, revoga o token apresentado e emite um novo (rotação).")
  @ApiResponse(responseCode = "200", description = "Sessão renovada")
  @ApiResponse(
      responseCode = "401",
      description = "Refresh token ausente, inválido, expirado ou reutilizado",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.REFRESH_INVALIDO)))
  @ApiResponse(
      responseCode = "403",
      description = "Origem não permitida",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.ORIGEM_NAO_PERMITIDA)))
  public ResponseEntity<LoginResponse> refresh(
      @Parameter(hidden = true) @CookieValue(name = CookieDeRefresh.NOME, required = false)
          String refreshToken,
      @Parameter(hidden = true) @RequestHeader(name = HttpHeaders.ORIGIN, required = false)
          String origem,
      HttpServletResponse resposta) {
    verificadorDeOrigem.verificar(origem);
    try {
      return comCookie(renovarSessao.executar(refreshToken));
    } catch (RefreshTokenInvalidoException e) {
      resposta.addHeader(HttpHeaders.SET_COOKIE, cookieDeRefresh.expirar());
      throw e;
    }
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Encerra a sessão",
      description = "Revoga a família do refresh token e expira o cookie. Sempre 204.")
  @ApiResponse(responseCode = "204", description = "Sessão encerrada")
  @ApiResponse(
      responseCode = "403",
      description = "Origem não permitida",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.ORIGEM_NAO_PERMITIDA)))
  public ResponseEntity<Void> logout(
      @Parameter(hidden = true) @CookieValue(name = CookieDeRefresh.NOME, required = false)
          String refreshToken,
      @Parameter(hidden = true) @RequestHeader(name = HttpHeaders.ORIGIN, required = false)
          String origem) {
    verificadorDeOrigem.verificar(origem);
    encerrarSessao.executar(refreshToken);
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, cookieDeRefresh.expirar())
        .build();
  }

  @GetMapping("/me")
  @Operation(summary = "Dados do usuário autenticado")
  @SecurityRequirement(name = OpenApiConfig.BEARER)
  @ApiResponse(responseCode = "200", description = "Usuário autenticado")
  @ApiResponse(
      responseCode = "401",
      description = "Token de acesso ausente, inválido ou expirado",
      content =
          @Content(
              mediaType = PROBLEM_JSON,
              examples = @ExampleObject(value = ExemplosDeErro.NAO_AUTENTICADO)))
  public UsuarioResponse me(@AuthenticationPrincipal Jwt jwt) {
    return UsuarioResponse.de(
        consultarUsuarioAutenticado.executar(UUID.fromString(jwt.getSubject())));
  }

  private ResponseEntity<LoginResponse> comCookie(SessaoAutenticada sessao) {
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            cookieDeRefresh.criar(sessao.refreshToken(), sessao.validadeDoRefreshToken()))
        .body(LoginResponse.de(sessao));
  }
}
