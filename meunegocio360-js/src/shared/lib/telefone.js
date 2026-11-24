import { somenteDigitos } from './digitos'

/** Máscara (00) 0000-0000 ou (00) 00000-0000; descarta tudo que não for dígito (RNF-FE-28). */
export function formatarTelefone(valor) {
  const d = somenteDigitos(valor).slice(0, 11)
  if (d.length === 0) return ''
  if (d.length <= 2) return `(${d}`
  const ddd = `(${d.slice(0, 2)}) `
  const numero = d.slice(2)
  if (numero.length <= 4) return `${ddd}${numero}`
  const corte = d.length === 11 ? 5 : 4
  return `${ddd}${numero.slice(0, corte)}-${numero.slice(corte)}`
}

/** Telefone com DDD: 10 ou 11 dígitos. */
export function telefoneValido(valor) {
  const tamanho = somenteDigitos(valor).length
  return tamanho === 10 || tamanho === 11
}
