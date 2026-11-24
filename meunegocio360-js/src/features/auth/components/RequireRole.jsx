import { Outlet } from 'react-router'
import { podeAcessar } from '../../../shared/lib/perfis'
import { AcessoNegado } from '../../../shared/ui/AcessoNegado'
import { useAuth } from '../hooks/useAuth'

/**
 * Guarda por perfil (RF-FE-002, RNF-FE-16). Sem permissão, mostra "Acesso negado" sem renderizar
 * a página, logo nenhuma requisição de dados é feita. É só UX: quem autoriza é o backend.
 */
export function RequireRole({ perfis, children }) {
  const { usuario } = useAuth()

  if (!podeAcessar(usuario?.perfil, perfis)) {
    return <AcessoNegado />
  }
  return children ?? <Outlet />
}
