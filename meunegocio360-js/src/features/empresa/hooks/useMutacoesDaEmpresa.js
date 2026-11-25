import { keepPreviousData, useMutation, useQuery } from '@tanstack/react-query'
import {
  alterarSituacaoDaEmpresa,
  atualizarEmpresa,
  cadastrarEmpresa,
  listarEmpresasInativas,
} from '../api/empresaApi'
import { CHAVE_EMPRESAS_INATIVAS } from '../empresaContext'
import { useEmpresa } from './useEmpresa'

// Consultas e mutações da empresa pelo TanStack Query (RNF-FE-04). No sucesso de uma mutação, o
// EmpresaProvider é atualizado com a resposta da API, sem nova consulta da empresa ativa.

const TAMANHO_DA_PAGINA_DE_INATIVAS = 5

/** Empresas inativas, paginadas (somente ADMIN; usada em /empresa/cadastro). */
export function useEmpresasInativas(pagina) {
  return useQuery({
    queryKey: [...CHAVE_EMPRESAS_INATIVAS, pagina],
    queryFn: () => listarEmpresasInativas({ page: pagina, size: TAMANHO_DA_PAGINA_DE_INATIVAS }),
    placeholderData: keepPreviousData,
    retry: false,
  })
}

export function useCadastrarEmpresa() {
  const { definirEmpresaAtiva } = useEmpresa()
  return useMutation({ mutationFn: cadastrarEmpresa, onSuccess: definirEmpresaAtiva })
}

export function useAtualizarEmpresa() {
  const { definirEmpresaAtiva } = useEmpresa()
  return useMutation({
    mutationFn: ({ id, dados }) => atualizarEmpresa(id, dados),
    onSuccess: definirEmpresaAtiva,
  })
}

/** Inativa ou reativa; a resposta já diz se a empresa ficou ativa. */
export function useAlterarSituacaoDaEmpresa() {
  const { definirEmpresaAtiva } = useEmpresa()
  return useMutation({
    mutationFn: ({ id, ativa, versao }) => alterarSituacaoDaEmpresa(id, { ativa, versao }),
    onSuccess: definirEmpresaAtiva,
  })
}
