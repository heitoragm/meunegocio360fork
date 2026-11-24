package br.com.meunegocio360.core.catalogo.api;

/**
 * Exemplos de problem+json do catálogo exibidos no OpenAPI (RNF-030). São constantes de compilação
 * porque são usadas dentro de anotações.
 */
final class ExemplosDeErro {

  private ExemplosDeErro() {}

  private static final String TIPO = "{\"type\":\"https://meunegocio360.com.br/problemas/";
  private static final String TRACE = ",\"traceId\":\"9f1c2e7a-0000-0000-0000-000000000000\"";
  private static final String ITENS = "/api/v1/itens-catalogo";
  private static final String ITEM_POR_ID =
      "/api/v1/itens-catalogo/9d3e7c10-5c2a-4a8e-8d1b-3a9f6e2b7c44";
  private static final String CATEGORIAS = "/api/v1/categorias";
  private static final String CATEGORIA_POR_ID =
      "/api/v1/categorias/2f0a8c9e-1b3d-4e5f-a6b7-c8d9e0f1a2b3";

  static final String PRECO_INVALIDO =
      TIPO
          + "validacao\",\"title\":\"Dados inválidos\",\"status\":400,"
          + "\"detail\":\"Um ou mais campos são inválidos.\",\"instance\":\""
          + ITENS
          + "\",\"code\":\"VALIDATION_ERROR\""
          + TRACE
          + ",\"errors\":[{\"field\":\"preco\",\"message\":\"deve ser um decimal não negativo"
          + " em string, com ponto e até 2 casas (ex.: \\\"89.90\\\"), até 9999999999.99\"}]}";

  static final String ITEM_NAO_ENCONTRADO =
      TIPO
          + "item-nao-encontrado\",\"title\":\"Item não encontrado\",\"status\":404,"
          + "\"detail\":\"O item do catálogo informado não existe.\",\"instance\":\""
          + ITEM_POR_ID
          + "\",\"code\":\"ITEM_NOT_FOUND\""
          + TRACE
          + "}";

  static final String CODIGO_DUPLICADO =
      TIPO
          + "codigo-de-item-duplicado\",\"title\":\"Código já usado\",\"status\":409,"
          + "\"detail\":\"Já existe um item do catálogo com este código. Ele pode estar"
          + " inativo.\",\"instance\":\""
          + ITENS
          + "\",\"code\":\"ITEM_CODE_DUPLICATE\""
          + TRACE
          + "}";

  static final String CATEGORIA_INVALIDA =
      TIPO
          + "categoria-invalida\",\"title\":\"Categoria inválida\",\"status\":422,"
          + "\"detail\":\"A categoria informada não existe ou está inativa.\",\"instance\":\""
          + ITENS
          + "\",\"code\":\"CATEGORY_INVALID\""
          + TRACE
          + "}";

  static final String TIPO_IMUTAVEL =
      TIPO
          + "tipo-de-item-imutavel\",\"title\":\"Tipo não pode ser alterado\",\"status\":422,"
          + "\"detail\":\"O tipo do item (produto ou serviço) não pode ser alterado depois do"
          + " cadastro. Cadastre um novo item e inative este.\",\"instance\":\""
          + ITEM_POR_ID
          + "\",\"code\":\"ITEM_TYPE_IMMUTABLE\""
          + TRACE
          + "}";

  static final String ITEM_COM_HISTORICO =
      TIPO
          + "item-com-historico\",\"title\":\"Item com histórico\",\"status\":422,"
          + "\"detail\":\"O item possui vendas ou compras vinculadas e não pode ser excluído."
          + " Inative o item.\",\"instance\":\""
          + ITEM_POR_ID
          + "\",\"code\":\"ITEM_HAS_HISTORY\""
          + TRACE
          + "}";

  static final String CATEGORIA_NAO_ENCONTRADA =
      TIPO
          + "categoria-nao-encontrada\",\"title\":\"Categoria não encontrada\",\"status\":404,"
          + "\"detail\":\"A categoria informada não existe.\",\"instance\":\""
          + CATEGORIA_POR_ID
          + "\",\"code\":\"CATEGORY_NOT_FOUND\""
          + TRACE
          + "}";

  static final String NOME_DE_CATEGORIA_DUPLICADO =
      TIPO
          + "nome-de-categoria-duplicado\",\"title\":\"Categoria já cadastrada\",\"status\":409,"
          + "\"detail\":\"Já existe uma categoria com este nome (sem considerar maiúsculas e"
          + " acentos). Ela pode estar inativa.\",\"instance\":\""
          + CATEGORIAS
          + "\",\"code\":\"CATEGORY_NAME_DUPLICATE\""
          + TRACE
          + "}";

  static final String CATEGORIA_EM_USO =
      TIPO
          + "categoria-em-uso\",\"title\":\"Categoria em uso\",\"status\":422,"
          + "\"detail\":\"A categoria possui 3 itens vinculados e não pode ser excluída."
          + " Inative a categoria.\",\"instance\":\""
          + CATEGORIA_POR_ID
          + "\",\"code\":\"CATEGORY_IN_USE\""
          + TRACE
          + "}";

  static final String VERSAO =
      TIPO
          + "conflito-de-versao\",\"title\":\"Conflito de versão\",\"status\":409,"
          + "\"detail\":\"O registro foi alterado por outra pessoa. Recarregue os dados e tente"
          + " novamente.\",\"instance\":\""
          + ITEM_POR_ID
          + "\",\"code\":\"VERSION_CONFLICT\""
          + TRACE
          + "}";

  static final String ACESSO_NEGADO =
      TIPO
          + "acesso-negado\",\"title\":\"Acesso negado\",\"status\":403,"
          + "\"detail\":\"Você não tem permissão para acessar este recurso.\",\"instance\":\""
          + ITENS
          + "\",\"code\":\"ACCESS_DENIED\""
          + TRACE
          + "}";
}
