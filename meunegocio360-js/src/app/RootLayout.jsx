import { Suspense } from 'react'
import { Outlet } from 'react-router'
import { Carregando } from '../shared/ui/Carregando'
import { TelaDeErro } from '../shared/ui/TelaDeErro'
import { STATUS, useAuth } from '../features/auth'

/**
 * Raiz das rotas: enquanto a sessão é restaurada, não renderiza login nem telas protegidas
 * (evita o "piscar" da tela de login). Falha de rede/5xx na restauração mostra erro com
 * "Tentar novamente", sem tratar como sessão inválida.
 */
export function RootLayout() {
  const { status, tentarNovamente } = useAuth()

  if (status === STATUS.CARREGANDO) {
    return <Carregando mensagem="Carregando sua sessão…" />
  }

  if (status === STATUS.ERRO) {
    return (
      <main className="flex-grow-1 d-flex">
        <TelaDeErro
          mensagem="Não conseguimos falar com o servidor. Verifique sua conexão e tente novamente."
          aoTentarNovamente={tentarNovamente}
        />
      </main>
    )
  }

  return (
    <Suspense fallback={<Carregando />}>
      <Outlet />
    </Suspense>
  )
}
