// API pública da feature de clientes (RNF-FE-01). Telas futuras (vendas, equipamentos, OS) usam o
// ClienteSelect daqui, nunca uma busca própria.
import { lazy } from 'react'

export { ClienteSelect } from './components/ClienteSelect'
export { SITUACAO, useBuscaClientes } from './hooks/useBuscaClientes'

export const ClientesPage = lazy(() => import('./pages/ClientesPage'))
export const NovoClientePage = lazy(() => import('./pages/NovoClientePage'))
export const EditarClientePage = lazy(() => import('./pages/EditarClientePage'))

/** Só em desenvolvimento: no build de produção a página nem entra no pacote. */
export const ClienteSelectDevPage = import.meta.env.DEV
  ? lazy(() => import('./pages/ClienteSelectDevPage'))
  : null
