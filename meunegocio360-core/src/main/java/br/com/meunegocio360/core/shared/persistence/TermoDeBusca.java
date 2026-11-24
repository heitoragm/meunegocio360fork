package br.com.meunegocio360.core.shared.persistence;

/**
 * Converte o parâmetro {@code q} das listagens nos padrões LIKE da busca por nome ou documento
 * (RNF-BE-16): o nome "contém" o texto; o documento "começa com" os dígitos, só quando o texto
 * parece um documento (dígitos e máscara). Os curingas digitados ({@code %}, {@code _}) são
 * escapados com {@code !}; a consulta DEVE usar {@code like ... escape '!'} no nome.
 */
public record TermoDeBusca(String padraoNome, String padraoDocumento) {

  private static final String PARECE_DOCUMENTO = "[0-9.\\-/\\s]*[0-9][0-9.\\-/\\s]*";

  public static TermoDeBusca de(String q) {
    if (q == null || q.isBlank()) {
      return new TermoDeBusca(null, null);
    }
    String termo = q.trim();
    String padraoDocumento =
        termo.matches(PARECE_DOCUMENTO) ? termo.replaceAll("\\D", "") + "%" : null;
    return new TermoDeBusca(padraoContem(termo), padraoDocumento);
  }

  /**
   * Padrão "contém" para um texto livre (ex.: nome ou código do item, RF-BE-005 RN-10), ou {@code
   * null} quando não há busca. A consulta DEVE usar {@code like ... escape '!'}.
   */
  public static String padraoContem(String q) {
    if (q == null || q.isBlank()) {
      return null;
    }
    return "%" + escapar(q.trim()) + "%";
  }

  private static String escapar(String texto) {
    return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }
}
