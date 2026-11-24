package br.com.meunegocio360.core.shared.web;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Tratamento único de erros em application/problem+json, RFC 9457 (RNF-BE-14). Nenhuma resposta
 * expõe stack trace, SQL ou nomes de classe (RNF-024).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String BASE_TIPO = "https://meunegocio360.com.br/problemas/";

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ProblemDetail> tratarApi(ApiException ex, HttpServletRequest request) {
    ResponseEntity.BodyBuilder resposta = ResponseEntity.status(ex.getStatus());
    ex.getCabecalhos().forEach(resposta::header);
    return resposta
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(
            problema(
                ex.getStatus(),
                ex.getTipo(),
                ex.getTitulo(),
                ex.getMessage(),
                ex.getCode(),
                request));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> tratarValidacao(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ErroDeCampo> erros =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ErroDeCampo(e.getField(), e.getDefaultMessage()))
            .toList();
    return validacao(erros, request);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ProblemDetail> tratarValidacaoDeParametros(
      HandlerMethodValidationException ex, HttpServletRequest request) {
    List<ErroDeCampo> erros =
        ex.getParameterValidationResults().stream()
            .flatMap(r ->
                r.getResolvableErrors().stream()
                    .map(e ->
                        new ErroDeCampo(
                            r.getMethodParameter().getParameterName(), e.getDefaultMessage())))
            .toList();
    return validacao(erros, request);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> tratarCorpoIlegivel(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return validacao(
        List.of(new ErroDeCampo("corpo", "corpo da requisição ausente ou malformado")), request);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ProblemDetail> tratarNaoAutenticado(
      AuthenticationException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(
            problema(
                HttpStatus.UNAUTHORIZED,
                "nao-autenticado",
                "Não autenticado",
                "Autenticação necessária ou token de acesso inválido.",
                "UNAUTHORIZED",
                request));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> tratarAcessoNegado(
      AccessDeniedException ex, HttpServletRequest request) {
    return resposta(
        HttpStatus.FORBIDDEN,
        "acesso-negado",
        "Acesso negado",
        "Você não tem permissão para acessar este recurso.",
        "ACCESS_DENIED",
        request);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ProblemDetail> tratarNaoEncontrado(
      NoResourceFoundException ex, HttpServletRequest request) {
    return resposta(
        HttpStatus.NOT_FOUND,
        "nao-encontrado",
        "Recurso não encontrado",
        "O recurso solicitado não existe.",
        "NOT_FOUND",
        request);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> tratarMetodoNaoPermitido(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    return resposta(
        HttpStatus.METHOD_NOT_ALLOWED,
        "metodo-nao-permitido",
        "Método não permitido",
        "O método HTTP não é suportado por este recurso.",
        "METHOD_NOT_ALLOWED",
        request);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ProblemDetail> tratarTipoDeMidia(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
    return resposta(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "tipo-de-midia-nao-suportado",
        "Tipo de mídia não suportado",
        "Envie o corpo como application/json.",
        "UNSUPPORTED_MEDIA_TYPE",
        request);
  }

  @ExceptionHandler(ParametroInvalidoException.class)
  public ResponseEntity<ProblemDetail> tratarParametroDeConsulta(
      ParametroInvalidoException ex, HttpServletRequest request) {
    return validacao(List.of(new ErroDeCampo(ex.getCampo(), ex.getMessage())), request);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> tratarParametroInvalido(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    return validacao(List.of(new ErroDeCampo(ex.getName(), "formato inválido")), request);
  }

  /** Duas pessoas alteraram o mesmo registro ao mesmo tempo (RNF-BE-28). */
  @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
  public ResponseEntity<ProblemDetail> tratarConflitoDeVersao(
      ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
    return tratarApi(new VersaoDesatualizadaException(), request);
  }

  /**
   * Rede de segurança: cada caso de uso traduz as restrições que conhece no erro de negócio
   * específico; o que escapar vira 409, nunca 500 (RNF-BE-15).
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> tratarViolacaoDeIntegridade(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    log.warn("Violação de integridade não traduzida em {}", request.getRequestURI());
    return resposta(
        HttpStatus.CONFLICT,
        "conflito-de-dados",
        "Conflito de dados",
        "A operação conflita com dados já existentes.",
        "DATA_CONFLICT",
        request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> tratarInesperado(Exception ex, HttpServletRequest request) {
    log.error(
        "Erro inesperado ao processar {} {}", request.getMethod(), request.getRequestURI(), ex);
    return resposta(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "erro-interno",
        "Erro interno",
        "Ocorreu um erro inesperado. Tente novamente mais tarde.",
        "INTERNAL_ERROR",
        request);
  }

  private ResponseEntity<ProblemDetail> validacao(
      List<ErroDeCampo> erros, HttpServletRequest request) {
    ProblemDetail problema =
        problema(
            HttpStatus.BAD_REQUEST,
            "validacao",
            "Dados inválidos",
            "Um ou mais campos são inválidos.",
            "VALIDATION_ERROR",
            request);
    problema.setProperty("errors", erros);
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problema);
  }

  private ResponseEntity<ProblemDetail> resposta(
      HttpStatus status,
      String tipo,
      String titulo,
      String detalhe,
      String code,
      HttpServletRequest request) {
    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problema(status, tipo, titulo, detalhe, code, request));
  }

  private ProblemDetail problema(
      HttpStatus status,
      String tipo,
      String titulo,
      String detalhe,
      String code,
      HttpServletRequest request) {
    ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
    problema.setType(URI.create(BASE_TIPO + tipo));
    problema.setTitle(titulo);
    problema.setInstance(URI.create(request.getRequestURI()));
    problema.setProperty("code", code);
    problema.setProperty("traceId", MDC.get(RequestIdFilter.CHAVE_MDC));
    return problema;
  }

  /** Item da lista {@code errors} do problem+json. */
  public record ErroDeCampo(String field, String message) {}
}
