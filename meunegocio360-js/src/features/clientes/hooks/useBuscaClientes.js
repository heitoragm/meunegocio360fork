import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { listarClientes } from '../api/clientesApi'

export const CHAVE_CLIENTES = ['clientes']

export const SITUACAO = Object.freeze({ ATIVO: 'ATIVO', INATIVO: 'INATIVO', TODOS: 'TODOS' })

/**
 * Busca de clientes por nome ou documento, paginada no servidor (RF-FE-003). É a MESMA busca da
 * lista e do ClienteSelect, para que se comporte igual em todas as telas. O termo já chega com
 * debounce; o `signal` cancela a requisição anterior, então um resultado atrasado nunca
 * sobrescreve o mais novo.
 */
export function useBuscaClientes(
  { q = '', situacao = SITUACAO.ATIVO, page = 0, size = 20 },
  { enabled = true } = {},
) {
  const termo = q.trim()
  return useQuery({
    queryKey: [...CHAVE_CLIENTES, 'lista', { q: termo, situacao, page, size }],
    queryFn: ({ signal }) => listarClientes({ q: termo, situacao, page, size, signal }),
    placeholderData: keepPreviousData,
    enabled,
    retry: false,
  })
}
