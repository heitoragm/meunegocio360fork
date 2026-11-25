import { PERFIS, TODOS_OS_PERFIS } from '../shared/lib/perfis'

/**
 * Itens do menu principal e perfis que os veem (RF-FE-002, "Perfis e acesso"; RNF-FE-16).
 * Cada RF seguinte acrescenta a sua entrada aqui, e a guarda RequireRole na rota correspondente.
 */
export const ITENS_DO_MENU = Object.freeze([
  { rota: '/', rotulo: 'Início', perfis: TODOS_OS_PERFIS, exato: true },
  { rota: '/empresa', rotulo: 'Empresa', perfis: TODOS_OS_PERFIS },
  { rota: '/clientes', rotulo: 'Clientes', perfis: [PERFIS.ADMIN, PERFIS.OPERADOR_COMERCIAL] },
])
