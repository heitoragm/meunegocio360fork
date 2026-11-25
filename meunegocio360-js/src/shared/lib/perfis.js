// Perfis de acesso da sessão (RN-07 do RF-BE-002), centralizados em um único lugar (RF-FE-002).
// O frontend usa os perfis só para a experiência de uso; quem autoriza é o backend (RNF-FE-16).

export const PERFIS = Object.freeze({
  ADMIN: 'ADMIN',
  OPERADOR_COMERCIAL: 'OPERADOR_COMERCIAL',
  OPERADOR_FINANCEIRO: 'OPERADOR_FINANCEIRO',
})

export const TODOS_OS_PERFIS = Object.freeze(Object.values(PERFIS))

export const NOME_DO_PERFIL = Object.freeze({
  [PERFIS.ADMIN]: 'Administrador',
  [PERFIS.OPERADOR_COMERCIAL]: 'Operador comercial',
  [PERFIS.OPERADOR_FINANCEIRO]: 'Operador financeiro',
})

export function podeAcessar(perfil, permitidos) {
  return Boolean(perfil) && permitidos.includes(perfil)
}
