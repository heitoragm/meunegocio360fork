package br.com.meunegocio360.core.shared.web;

/** Normalização dos textos recebidos pela API: trim e opcionais em branco como nulo. */
public final class Textos {

  private Textos() {}

  public static String aparar(String valor) {
    return valor == null ? null : valor.trim();
  }

  /** Campo opcional: em branco equivale a não informado. */
  public static String opcional(String valor) {
    String aparado = aparar(valor);
    return aparado == null || aparado.isEmpty() ? null : aparado;
  }
}
