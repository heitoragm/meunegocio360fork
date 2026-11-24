package br.com.meunegocio360.core.cliente.application;

/**
 * Converte o parâmetro {@code q} nos padrões LIKE da busca (RN-08): o nome "contém" o texto; o
 * documento "começa com" os dígitos, só quando o texto parece um documento (dígitos e máscara). Os
 * curingas digitados ({@code %}, {@code _}) são escapados com {@code !}, para valerem como texto.
 */
record TermoDeBusca(String padraoNome, String padraoDocumento) {

  private static final String PARECE_DOCUMENTO = "[0-9.\\-/\\s]*[0-9][0-9.\\-/\\s]*";

  static TermoDeBusca de(String q) {
    if (q == null || q.isBlank()) {
      return new TermoDeBusca(null, null);
    }
    String termo = q.trim();
    String padraoNome = "%" + escapar(termo) + "%";
    String padraoDocumento =
        termo.matches(PARECE_DOCUMENTO) ? termo.replaceAll("\\D", "") + "%" : null;
    return new TermoDeBusca(padraoNome, padraoDocumento);
  }

  private static String escapar(String texto) {
    return texto.replace("!", "!!").replace("%", "!%").replace("_", "!_");
  }
}
