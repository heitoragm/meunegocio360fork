import { useQuery, useQueryClient } from '@tanstack/react-query'
import { useCallback, useMemo, useState } from 'react'
import { Outlet } from 'react-router'
import { obterEmpresaAtiva } from '../api/empresaApi'
import { CHAVE_EMPRESA, ESTADO_EMPRESA, EmpresaContext } from '../empresaContext'

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
  // A API só devolve a empresa ATIVA; a inativada agora há pouco fica em memória para permitir
  // reativá-la nesta sessão (não há endpoint que liste empresas inativas).
  const [empresaInativada, setEmpresaInativada] = useState(null)

  const estado = consulta.isPending
    ? ESTADO_EMPRESA.CARREGANDO
    : consulta.isError
      ? ESTADO_EMPRESA.ERRO
      : consulta.data
        ? ESTADO_EMPRESA.ATIVA
        : ESTADO_EMPRESA.AUSENTE

  const definirEmpresaAtiva = useCallback(
    (empresa) => {
      queryClient.setQueryData(CHAVE_EMPRESA, empresa)
      setEmpresaInativada(null)
    },
    [queryClient],
  )

  const registrarInativacao = useCallback(
    (empresa) => {
      setEmpresaInativada(empresa)
      queryClient.setQueryData(CHAVE_EMPRESA, null)
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
      empresaInativada,
      recarregar,
      definirEmpresaAtiva,
      registrarInativacao,
    }),
    [estado, empresa, empresaInativada, recarregar, definirEmpresaAtiva, registrarInativacao],
  )

  return <EmpresaContext.Provider value={valor}>{children ?? <Outlet />}</EmpresaContext.Provider>
}
