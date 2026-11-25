import { request } from '../../../shared/lib/http'

// Clientes da empresa ativa (contrato do RF-BE-003). Componentes nunca chamam fetch: usam estas
// funções por meio dos hooks do TanStack Query (RNF-FE-03, RNF-FE-04).

const caminho = (id) => `/clientes/${encodeURIComponent(id)}`

/**
 * Página de clientes. `q` busca por nome ou documento (com ou sem máscara); `signal` permite ao
 * TanStack Query cancelar a busca anterior quando o termo muda.
 */
export function listarClientes({ q = '', situacao = 'ATIVO', page = 0, size = 20, signal } = {}) {
  const parametros = new URLSearchParams({ situacao, page: String(page), size: String(size) })
  if (q) {
    parametros.set('q', q)
  }
  return request(`/clientes?${parametros}`, { signal })
}

export function obterCliente(id, { signal } = {}) {
  return request(caminho(id), { signal })
}

export function cadastrarCliente(dados) {
  return request('/clientes', { method: 'POST', body: dados })
}

export function atualizarCliente(id, dados) {
  return request(caminho(id), { method: 'PUT', body: dados })
}

export function alterarSituacaoDoCliente(id, { ativo, versao }) {
  return request(`${caminho(id)}/situacao`, { method: 'PATCH', body: { ativo, versao } })
}

export function excluirCliente(id) {
  return request(caminho(id), { method: 'DELETE' })
}
