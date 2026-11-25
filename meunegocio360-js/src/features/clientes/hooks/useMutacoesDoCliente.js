import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  alterarSituacaoDoCliente,
  atualizarCliente,
  cadastrarCliente,
  excluirCliente,
  obterCliente,
} from '../api/clientesApi'
import { CHAVE_CLIENTES } from './useBuscaClientes'

// Consulta e mutações de um cliente (RNF-FE-04). Toda mutação bem-sucedida invalida as consultas
// de clientes, e a lista se atualiza sem recarregar a página.

export function useCliente(id) {
  return useQuery({
    queryKey: [...CHAVE_CLIENTES, 'detalhe', id],
    queryFn: ({ signal }) => obterCliente(id, { signal }),
    retry: false,
  })
}

function useMutacaoDeCliente(mutationFn) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: CHAVE_CLIENTES }),
  })
}

export function useCadastrarCliente() {
  return useMutacaoDeCliente(cadastrarCliente)
}

export function useAtualizarCliente() {
  return useMutacaoDeCliente(({ id, dados }) => atualizarCliente(id, dados))
}

export function useAlterarSituacaoDoCliente() {
  return useMutacaoDeCliente(({ id, ativo, versao }) =>
    alterarSituacaoDoCliente(id, { ativo, versao }),
  )
}

export function useExcluirCliente() {
  return useMutacaoDeCliente(({ id }) => excluirCliente(id))
}

/** Depois de um 404 CUSTOMER_NOT_FOUND, a lista precisa ser atualizada mesmo sem sucesso. */
export function useAtualizarListaDeClientes() {
  const queryClient = useQueryClient()
  return () => queryClient.invalidateQueries({ queryKey: CHAVE_CLIENTES })
}
