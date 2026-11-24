package br.com.meunegocio360.core.shared.web;

/**
 * Parâmetro de consulta inválido; vira 400 {@code VALIDATION_ERROR} com o campo em {@code errors}.
 */
public class ParametroInvalidoException extends RuntimeException {

  private final String campo;

  public ParametroInvalidoException(String campo, String mensagem) {
    super(mensagem, null, false, false);
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }
}
