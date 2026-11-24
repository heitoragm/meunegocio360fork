package br.com.meunegocio360.core.cliente.api;

/**
 * Exemplos de problem+json dos clientes exibidos no OpenAPI (RNF-030). São constantes de compilação
 * porque são usadas dentro de anotações.
 */
final class ExemplosDeErro {

  private ExemplosDeErro() {}

  private static final String TIPO = "{\"type\":\"https://meunegocio360.com.br/problemas/";
  private static final String TRACE = ",\"traceId\":\"9f1c2e7a-0000-0000-0000-000000000000\"";
  private static final String CLIENTES = "/api/v1/clientes";
  private static final String CLIENTE_POR_ID =
      "/api/v1/clientes/6c1f0a52-3b7e-4f0e-9a3e-2f7a1d5b8c90";

  static final String NAO_ENCONTRADO =
      TIPO
          + "cliente-nao-encontrado\",\"title\":\"Cliente não encontrado\",\"status\":404,"
          + "\"detail\":\"O cliente informado não existe.\",\"instance\":\""
          + CLIENTE_POR_ID
          + "\",\"code\":\"CUSTOMER_NOT_FOUND\""
          + TRACE
          + "}";

  static final String DOCUMENTO_INVALIDO =
      TIPO
          + "validacao\",\"title\":\"Dados inválidos\",\"status\":400,"
          + "\"detail\":\"Um ou mais campos são inválidos.\",\"instance\":\""
          + CLIENTES
          + "\",\"code\":\"VALIDATION_ERROR\""
          + TRACE
          + ",\"errors\":[{\"field\":\"documento\","
          + "\"message\":\"deve ser um CPF válido (11 dígitos) para pessoa física\"}]}";

  static final String DOCUMENTO_DUPLICADO =
      TIPO
          + "documento-de-cliente-duplicado\",\"title\":\"Cliente já cadastrado\",\"status\":409,"
          + "\"detail\":\"Já existe um cliente com este documento. Ele pode estar inativo:"
          + " consulte a listagem com a situação \\\"todos\\\".\",\"instance\":\""
          + CLIENTES
          + "\",\"code\":\"CUSTOMER_DOCUMENT_DUPLICATE\""
          + TRACE
          + "}";

  static final String COM_HISTORICO =
      TIPO
          + "cliente-com-historico\",\"title\":\"Cliente com histórico\",\"status\":422,"
          + "\"detail\":\"O cliente possui vendas ou ordens de serviço vinculadas e não pode ser"
          + " excluído. Inative o cliente.\",\"instance\":\""
          + CLIENTE_POR_ID
          + "\",\"code\":\"CUSTOMER_HAS_HISTORY\""
          + TRACE
          + "}";

  static final String VERSAO =
      TIPO
          + "conflito-de-versao\",\"title\":\"Conflito de versão\",\"status\":409,"
          + "\"detail\":\"O registro foi alterado por outra pessoa. Recarregue os dados e tente"
          + " novamente.\",\"instance\":\""
          + CLIENTE_POR_ID
          + "\",\"code\":\"VERSION_CONFLICT\""
          + TRACE
          + "}";

  static final String ACESSO_NEGADO =
      TIPO
          + "acesso-negado\",\"title\":\"Acesso negado\",\"status\":403,"
          + "\"detail\":\"Você não tem permissão para acessar este recurso.\",\"instance\":\""
          + CLIENTES
          + "\",\"code\":\"ACCESS_DENIED\""
          + TRACE
          + "}";
}
