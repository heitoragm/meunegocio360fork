import { createBrowserRouter } from 'react-router'
import { PERFIS } from '../shared/lib/perfis'
import { LoginPage, RequireAuth, RequireRole } from '../features/auth'
import {
  CadastroEmpresaPage,
  EmpresaPage,
  EmpresaProvider,
  RequireEmpresa,
} from '../features/empresa'
import {
  ClienteSelectDevPage,
  ClientesPage,
  EditarClientePage,
  NovoClientePage,
} from '../features/clientes'
import { InicioPage } from '../features/inicio'
import { AppLayout } from './AppLayout'
import { NaoEncontradaPage } from './pages'
import { RootLayout } from './RootLayout'

/**
 * Rotas de apoio que só existem em desenvolvimento (RF-FE-003): no build de produção a condição é
 * falsa e a página nem entra no pacote.
 */
const ROTAS_DE_DESENVOLVIMENTO = import.meta.env.DEV
  ? [{ path: '/dev/cliente-select', element: <ClienteSelectDevPage /> }]
  : []

/**
 * Sessão (RequireAuth) → empresa (EmpresaProvider) → layout (AppLayout) → guardas de perfil e de
 * empresa ativa (RF-FE-002). Cada RF seguinte acrescenta suas rotas sob RequireEmpresa, com
 * RequireRole conforme a tabela "Perfis e acesso".
 */
export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    children: [
      { path: '/login', element: <LoginPage /> },
      {
        element: <RequireAuth />,
        children: [
          {
            element: <EmpresaProvider />,
            children: [
              {
                element: <AppLayout />,
                children: [
                  {
                    path: '/empresa/cadastro',
                    element: (
                      <RequireRole perfis={[PERFIS.ADMIN]}>
                        <CadastroEmpresaPage />
                      </RequireRole>
                    ),
                  },
                  {
                    element: <RequireEmpresa />,
                    children: [
                      { path: '/', element: <InicioPage /> },
                      { path: '/empresa', element: <EmpresaPage /> },
                      {
                        element: <RequireRole perfis={[PERFIS.ADMIN, PERFIS.OPERADOR_COMERCIAL]} />,
                        children: [
                          { path: '/clientes', element: <ClientesPage /> },
                          { path: '/clientes/novo', element: <NovoClientePage /> },
                          { path: '/clientes/:id', element: <EditarClientePage /> },
                          ...ROTAS_DE_DESENVOLVIMENTO,
                        ],
                      },
                    ],
                  },
                ],
              },
            ],
          },
        ],
      },
      { path: '*', element: <NaoEncontradaPage /> },
    ],
  },
])
