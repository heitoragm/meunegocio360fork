import { ApiError, request } from '../../../shared/lib/http'

/**
 * Empresa ativa (RF-BE-002). Sem empresa ativa a API responde 404 COMPANY_NOT_REGISTERED, que
 * aqui vira `null`: é um estado esperado ("ausente"), não uma falha.
 */
export async function obterEmpresaAtiva() {
  try {
    return await request('/empresa')
  } catch (erro) {
    if (erro instanceof ApiError && erro.code === 'COMPANY_NOT_REGISTERED') {
      return null
    }
    throw erro
  }
}

export function cadastrarEmpresa(dados) {
  return request('/empresa', { method: 'POST', body: dados })
}

export function atualizarEmpresa(id, dados) {
  return request(`/empresa/${encodeURIComponent(id)}`, { method: 'PUT', body: dados })
}

export function alterarSituacaoDaEmpresa(id, { ativa, versao }) {
  return request(`/empresa/${encodeURIComponent(id)}/situacao`, {
    method: 'PATCH',
    body: { ativa, versao },
  })
}
