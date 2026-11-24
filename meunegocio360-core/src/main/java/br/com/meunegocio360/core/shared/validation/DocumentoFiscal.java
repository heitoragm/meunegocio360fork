package br.com.meunegocio360.core.shared.validation;

/**
 * Validação de documentos fiscais pelos dígitos verificadores (RNF-BE-27). Os documentos trafegam e
 * são armazenados somente com dígitos; a máscara é responsabilidade do frontend.
 */
public final class DocumentoFiscal {

  private static final int TAMANHO_CPF = 11;
  private static final int TAMANHO_CNPJ = 14;
  private static final int[] PESOS_PRIMEIRO_DIGITO_CPF = {10, 9, 8, 7, 6, 5, 4, 3, 2};
  private static final int[] PESOS_SEGUNDO_DIGITO_CPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
  private static final int[] PESOS_PRIMEIRO_DIGITO_CNPJ = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
  private static final int[] PESOS_SEGUNDO_DIGITO_CNPJ = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

  /** Dígitos e os separadores usuais das máscaras de CPF e CNPJ (ex.: 529.982.247-25). */
  private static final String DOCUMENTO_COM_MASCARA = "[0-9.\\-/\\s]+";

  private DocumentoFiscal() {}

  /**
   * Remove a máscara (pontos, traço, barra e espaços). Textos com outros caracteres são devolvidos
   * apenas aparados, para que a validação os rejeite em vez de "aproveitar" os dígitos.
   */
  public static String removerMascara(String documento) {
    if (documento == null) {
      return null;
    }
    String aparado = documento.trim();
    return aparado.matches(DOCUMENTO_COM_MASCARA) ? aparado.replaceAll("\\D", "") : aparado;
  }

  /** CPF com 11 dígitos, sem sequência repetida (ex.: 11111111111) e dígitos verificadores ok. */
  public static boolean cpfValido(String cpf) {
    return digitosConferem(cpf, TAMANHO_CPF, PESOS_PRIMEIRO_DIGITO_CPF, PESOS_SEGUNDO_DIGITO_CPF);
  }

  /**
   * CNPJ com 14 dígitos, sem sequência repetida (ex.: 00000000000000) e dígitos verificadores ok.
   */
  public static boolean cnpjValido(String cnpj) {
    return digitosConferem(
        cnpj, TAMANHO_CNPJ, PESOS_PRIMEIRO_DIGITO_CNPJ, PESOS_SEGUNDO_DIGITO_CNPJ);
  }

  private static boolean digitosConferem(
      String documento, int tamanho, int[] pesosPrimeiro, int[] pesosSegundo) {
    if (documento == null || !documento.matches("\\d{" + tamanho + "}") || todosIguais(documento)) {
      return false;
    }
    int primeiro = digitoVerificador(documento, pesosPrimeiro);
    int segundo = digitoVerificador(documento, pesosSegundo);
    return primeiro == Character.getNumericValue(documento.charAt(tamanho - 2))
        && segundo == Character.getNumericValue(documento.charAt(tamanho - 1));
  }

  private static int digitoVerificador(String documento, int[] pesos) {
    int soma = 0;
    for (int i = 0; i < pesos.length; i++) {
      soma += Character.getNumericValue(documento.charAt(i)) * pesos[i];
    }
    int resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
  }

  private static boolean todosIguais(String documento) {
    return documento.chars().allMatch(c -> c == documento.charAt(0));
  }
}
