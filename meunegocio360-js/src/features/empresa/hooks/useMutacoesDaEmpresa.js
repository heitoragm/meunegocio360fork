import { useMutation } from '@tanstack/react-query'
import { alterarSituacaoDaEmpresa, atualizarEmpresa, cadastrarEmpresa } from '../api/empresaApi'
import { useEmpresa } from './useEmpresa'

// Mutações da empresa pelo TanStack Query (RNF-FE-04). No sucesso, o EmpresaProvider é atualizado
// com a resposta da API, sem nova consulta.

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

export function useAlterarSituacaoDaEmpresa() {
  const { definirEmpresaAtiva, registrarInativacao } = useEmpresa()
  return useMutation({
    mutationFn: ({ id, ativa, versao }) => alterarSituacaoDaEmpresa(id, { ativa, versao }),
    onSuccess: (empresa) =>
      empresa.ativa ? definirEmpresaAtiva(empresa) : registrarInativacao(empresa),
  })
}
