// API pública da feature de empresa (RNF-FE-01).
import { lazy } from 'react'

export { EmpresaProvider } from './components/EmpresaProvider'
export { RequireEmpresa } from './components/RequireEmpresa'
export { useEmpresa } from './hooks/useEmpresa'
export { ESTADO_EMPRESA } from './empresaContext'

export const CadastroEmpresaPage = lazy(() => import('./pages/CadastroEmpresaPage'))
export const EmpresaPage = lazy(() => import('./pages/EmpresaPage'))
