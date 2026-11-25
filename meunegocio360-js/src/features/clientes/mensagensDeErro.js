import { ApiError } from '../../shared/lib/http'

// Mensagens por `code` (RNF-FE-14, RNF-FE-29), conforme a tabela "Comportamentos e mensagens" do
// RF-FE-003. A interface decide pelo código, nunca pelo texto do servidor.

export const MENSAGEM_DOCUMENTO_DUPLICADO =
  'Já existe um cliente com este documento. Ele pode estar inativo; busque por ele e reative.'
export const MENSAGEM_CONFLITO_DE_VERSAO =
  'Os dados deste cliente foram alterados em outra sessão depois que você os abriu. Recarregue para ver a versão atual.'
export const MENSAGEM_EMPRESA_INATIVA = 'Cadastre ou ative a empresa para continuar.'
export const MENSAGEM_COM_HISTORICO =
  'Este cliente possui vendas ou ordens de serviço vinculadas e não pode ser excluído. Você pode inativá-lo.'
export const MENSAGEM_NAO_ENCONTRADO = 'Este cliente não existe mais.'
export const MENSAGEM_SEM_PERMISSAO = 'Seu perfil não tem permissão para alterar clientes.'
export const MENSAGEM_FALHA =
  'Não foi possível concluir agora. Verifique sua conexão e tente novamente.'

export const ERRO = Object.freeze({
  VALIDACAO: 'validacao',
  DOCUMENTO_DUPLICADO: 'documento-duplicado',
  CONFLITO: 'conflito',
  EMPRESA_INATIVA: 'empresa-inativa',
  COM_HISTORICO: 'com-historico',
  NAO_ENCONTRADO: 'nao-encontrado',
  SEM_PERMISSAO: 'sem-permissao',
  FALHA: 'falha',
})

/** Classifica o erro da API para a interface decidir o que mostrar. */
export function classificarErro(falha) {
  if (!(falha instanceof ApiError)) return ERRO.FALHA
  switch (falha.code) {
    case 'VALIDATION_ERROR':
      return ERRO.VALIDACAO
    case 'CUSTOMER_DOCUMENT_DUPLICATE':
      return ERRO.DOCUMENTO_DUPLICADO
    case 'VERSION_CONFLICT':
      return ERRO.CONFLITO
    case 'COMPANY_NOT_ACTIVE':
      return ERRO.EMPRESA_INATIVA
    case 'CUSTOMER_HAS_HISTORY':
      return ERRO.COM_HISTORICO
    case 'CUSTOMER_NOT_FOUND':
      return ERRO.NAO_ENCONTRADO
    default:
      return falha.status === 403 ? ERRO.SEM_PERMISSAO : ERRO.FALHA
  }
}

/** Mensagem curta para falhas de ações da lista (inativar, reativar). */
export function mensagemDaAcao(falha) {
  switch (classificarErro(falha)) {
    case ERRO.CONFLITO:
      return 'Este cliente foi alterado em outra sessão. A lista foi atualizada; tente de novo.'
    case ERRO.NAO_ENCONTRADO:
      return MENSAGEM_NAO_ENCONTRADO
    case ERRO.EMPRESA_INATIVA:
      return MENSAGEM_EMPRESA_INATIVA
    case ERRO.SEM_PERMISSAO:
      return MENSAGEM_SEM_PERMISSAO
    default:
      return MENSAGEM_FALHA
  }
}
