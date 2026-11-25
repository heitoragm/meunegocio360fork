import { createContext } from 'react'

export const EmpresaContext = createContext(null)

export const CHAVE_EMPRESA = ['empresa']
export const CHAVE_EMPRESAS_INATIVAS = ['empresa', 'inativas']

/** Estados expostos às guardas (RF-FE-002, "Comportamento das guardas"). */
export const ESTADO_EMPRESA = Object.freeze({
  CARREGANDO: 'carregando',
  ATIVA: 'ativa',
  AUSENTE: 'ausente',
  ERRO: 'erro',
})
