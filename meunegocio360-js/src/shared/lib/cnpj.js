import { somenteDigitos } from './digitos'

const TAMANHO = 14
const PESOS_PRIMEIRO = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
const PESOS_SEGUNDO = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]

/** Máscara progressiva 00.000.000/0000-00; descarta tudo que não for dígito (RNF-FE-28). */
export function formatarCnpj(valor) {
  const d = somenteDigitos(valor).slice(0, TAMANHO)
  if (d.length <= 2) return d
  if (d.length <= 5) return `${d.slice(0, 2)}.${d.slice(2)}`
  if (d.length <= 8) return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5)}`
  if (d.length <= 12) return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8)}`
  return `${d.slice(0, 2)}.${d.slice(2, 5)}.${d.slice(5, 8)}/${d.slice(8, 12)}-${d.slice(12)}`
}

/** CNPJ com 14 dígitos e dígitos verificadores corretos; rejeita sequências repetidas. */
export function cnpjValido(valor) {
  const d = somenteDigitos(valor)
  if (d.length !== TAMANHO || /^(\d)\1+$/.test(d)) {
    return false
  }
  const primeiro = digitoVerificador(d, PESOS_PRIMEIRO)
  const segundo = digitoVerificador(d, PESOS_SEGUNDO)
  return Number(d[12]) === primeiro && Number(d[13]) === segundo
}

function digitoVerificador(digitos, pesos) {
  const soma = pesos.reduce((acc, peso, i) => acc + Number(digitos[i]) * peso, 0)
  const resto = soma % 11
  return resto < 2 ? 0 : 11 - resto
}
