package br.com.meunegocio360.core.fornecedor.api;

/**
 * Exemplos de problem+json dos fornecedores exibidos no OpenAPI (RNF-030). São constantes de
 * compilação porque são usadas dentro de anotações.
 */
final class ExemplosDeErro {

  private ExemplosDeErro() {}

  private static final String TIPO = "{\"type\":\"https://meunegocio360.com.br/problemas/";
  private static final String TRACE = ",\"traceId\":\"9f1c2e7a-0000-0000-0000-000000000000\"";
  private static final String FORNECEDORES = "/api/v1/fornecedores";
  private static final String FORNECEDOR_POR_ID =
      "/api/v1/fornecedores/8a2d4c71-5e9f-4b3a-b1c6-7d0e2f3a9b45";

  static final String NAO_ENCONTRADO =
      TIPO
          + "fornecedor-nao-encontrado\",\"title\":\"Fornecedor não encontrado\",\"status\":404,"
          + "\"detail\":\"O fornecedor informado não existe.\",\"instance\":\""
          + FORNECEDOR_POR_ID
          + "\",\"code\":\"SUPPLIER_NOT_FOUND\""
          + TRACE
          + "}";

  static final String DOCUMENTO_INVALIDO =
      TIPO
          + "validacao\",\"title\":\"Dados inválidos\",\"status\":400,"
          + "\"detail\":\"Um ou mais campos são inválidos.\",\"instance\":\""
          + FORNECEDORES
          + "\",\"code\":\"VALIDATION_ERROR\""
          + TRACE
          + ",\"errors\":[{\"field\":\"documento\","
          + "\"message\":\"deve ser um CPF válido (11 dígitos) para pessoa física\"}]}";

  static final String DOCUMENTO_DUPLICADO =
      TIPO
          + "documento-de-fornecedor-duplicado\",\"title\":\"Fornecedor já cadastrado\",\"status\":409,"
          + "\"detail\":\"Já existe um fornecedor com este documento. Ele pode estar inativo:"
          + " consulte a listagem com a situação \\\"todos\\\".\",\"instance\":\""
          + FORNECEDORES
          + "\",\"code\":\"SUPPLIER_DOCUMENT_DUPLICATE\""
          + TRACE
          + "}";

  static final String COM_HISTORICO =
      TIPO
          + "fornecedor-com-historico\",\"title\":\"Fornecedor com histórico\",\"status\":422,"
          + "\"detail\":\"O fornecedor possui pedidos de compra ou contas a pagar vinculados e não pode"
          + " ser excluído. Inative o fornecedor.\",\"instance\":\""
          + FORNECEDOR_POR_ID
          + "\",\"code\":\"SUPPLIER_HAS_HISTORY\""
          + TRACE
          + "}";

  static final String VERSAO =
      TIPO
          + "conflito-de-versao\",\"title\":\"Conflito de versão\",\"status\":409,"
          + "\"detail\":\"O registro foi alterado por outra pessoa. Recarregue os dados e tente"
          + " novamente.\",\"instance\":\""
          + FORNECEDOR_POR_ID
          + "\",\"code\":\"VERSION_CONFLICT\""
          + TRACE
          + "}";

  static final String ACESSO_NEGADO =
      TIPO
          + "acesso-negado\",\"title\":\"Acesso negado\",\"status\":403,"
          + "\"detail\":\"Você não tem permissão para acessar este recurso.\",\"instance\":\""
          + FORNECEDORES
          + "\",\"code\":\"ACCESS_DENIED\""
          + TRACE
          + "}";
}
