import { Suspense } from 'react'
import { Outlet } from 'react-router'
import { Carregando } from '../shared/ui/Carregando'
import { Rodape } from '../shared/ui/Rodape'
import { ESTADO_EMPRESA, useEmpresa } from '../features/empresa'
import { BarraDeNavegacao } from './BarraDeNavegacao'

/**
 * Layout das rotas protegidas (RF-FE-002): barra de navegação, conteúdo e rodapé. Nada é
 * renderizado antes de a empresa ser resolvida, para não haver "flash" de conteúdo.
 */
export function AppLayout() {
  const { estado } = useEmpresa()

  if (estado === ESTADO_EMPRESA.CARREGANDO) {
    return <Carregando mensagem="Carregando os dados da empresa…" />
  }

  return (
    <>
      <BarraDeNavegacao />
      <main className="flex-grow-1 d-flex flex-column">
        <Suspense fallback={<Carregando />}>
          <Outlet />
        </Suspense>
      </main>
      <Rodape />
    </>
  )
}
