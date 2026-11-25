import { ApiError } from '../../shared/lib/http'

// Mensagens por `code` (RNF-FE-14, RNF-FE-29), conforme a tabela de comportamentos do RF-FE-002.

export const MENSAGEM_CNPJ_DUPLICADO = 'Já existe uma empresa cadastrada com este CNPJ.'
export const MENSAGEM_EMPRESA_JA_ATIVA =
  'Já existe uma empresa ativa. Inative-a antes de cadastrar ou reativar outra.'
export const MENSAGEM_CONFLITO_DE_VERSAO =
  'Os dados da empresa foram alterados em outra sessão depois que você os abriu. Recarregue para ver a versão atual.'
export const MENSAGEM_SEM_PERMISSAO = 'Você não tem permissão para alterar os dados da empresa.'
export const MENSAGEM_FALHA =
  'Não foi possível salvar agora. Verifique sua conexão e tente novamente.'

/** Classifica o erro para a interface decidir o que mostrar. */
export function classificarErro(falha) {
  if (!(falha instanceof ApiError)) {
    return 'falha'
  }
  if (falha.code === 'VALIDATION_ERROR') return 'validacao'
  if (falha.code === 'COMPANY_CNPJ_DUPLICATE') return 'cnpj-duplicado'
  if (falha.code === 'COMPANY_ALREADY_ACTIVE') return 'ja-ativa'
  if (falha.code === 'VERSION_CONFLICT') return 'conflito'
  if (falha.status === 403) return 'sem-permissao'
  return 'falha'
}
