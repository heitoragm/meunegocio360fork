import { useQuery, useQueryClient } from '@tanstack/react-query'
import { useCallback, useMemo } from 'react'
import { Outlet } from 'react-router'
import { obterEmpresaAtiva } from '../api/empresaApi'
import {
  CHAVE_EMPRESA,
  CHAVE_EMPRESAS_INATIVAS,
  ESTADO_EMPRESA,
  EmpresaContext,
} from '../empresaContext'

/**
 * Empresa ativa e parâmetros de exibição (RF-FE-002). Consulta GET /empresa UMA vez por sessão,
 * pelo TanStack Query (RNF-FE-04); o logout limpa o cache e, com ele, a empresa.
 * Usado como rota de layout, abaixo de RequireAuth.
 */
export function EmpresaProvider({ children }) {
  const queryClient = useQueryClient()
  const consulta = useQuery({
    queryKey: CHAVE_EMPRESA,
    queryFn: obterEmpresaAtiva,
    staleTime: Infinity,
    retry: false,
  })

  const estado = consulta.isPending
    ? ESTADO_EMPRESA.CARREGANDO
    : consulta.isError
      ? ESTADO_EMPRESA.ERRO
      : consulta.data
        ? ESTADO_EMPRESA.ATIVA
        : ESTADO_EMPRESA.AUSENTE

  /**
   * Atualiza a empresa ativa com a resposta da API (cadastro, edição, inativação, reativação),
   * sem nova consulta. A lista de inativas muda junto e é recarregada quando for exibida.
   */
  const definirEmpresaAtiva = useCallback(
    (empresa) => {
      queryClient.setQueryData(CHAVE_EMPRESA, empresa?.ativa ? empresa : null)
      queryClient.invalidateQueries({ queryKey: CHAVE_EMPRESAS_INATIVAS })
    },
    [queryClient],
  )

  const { refetch } = consulta
  const recarregar = useCallback(() => refetch(), [refetch])

  const empresa = consulta.data ?? null
  const valor = useMemo(
    () => ({
      estado,
      empresa,
      nomeExibicao: empresa ? empresa.nomeFantasia || empresa.razaoSocial : null,
      recarregar,
      definirEmpresaAtiva,
    }),
    [estado, empresa, recarregar, definirEmpresaAtiva],
  )

  return <EmpresaContext.Provider value={valor}>{children ?? <Outlet />}</EmpresaContext.Provider>
}
