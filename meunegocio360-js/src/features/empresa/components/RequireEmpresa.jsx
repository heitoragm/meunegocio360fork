import { Navigate, Outlet } from 'react-router'
import { PERFIS } from '../../../shared/lib/perfis'
import { Carregando } from '../../../shared/ui/Carregando'
import { TelaDeErro } from '../../../shared/ui/TelaDeErro'
import { useAuth } from '../../auth'
import { ESTADO_EMPRESA } from '../empresaContext'
import { useEmpresa } from '../hooks/useEmpresa'
import { AguardandoEmpresa } from './AguardandoEmpresa'

/**
 * Guarda das rotas de negócio: exige empresa ativa (RF-FE-002, item 2 das guardas).
 * Sem empresa, ADMIN vai ao primeiro cadastro e os demais perfis aguardam o administrador.
 */
export function RequireEmpresa() {
  const { usuario } = useAuth()
  const { estado, recarregar } = useEmpresa()

  switch (estado) {
    case ESTADO_EMPRESA.CARREGANDO:
      return <Carregando mensagem="Carregando os dados da empresa…" />
    case ESTADO_EMPRESA.ERRO:
      return (
        <TelaDeErro
          mensagem="Não foi possível carregar os dados da empresa. Verifique sua conexão e tente novamente."
          aoTentarNovamente={recarregar}
        />
      )
    case ESTADO_EMPRESA.AUSENTE:
      return usuario.perfil === PERFIS.ADMIN ? (
        <Navigate to="/empresa/cadastro" replace />
      ) : (
        <AguardandoEmpresa />
      )
    default:
      return <Outlet />
  }
}
