import { somenteDigitos } from './digitos'

const TAMANHO = 11

/** Máscara progressiva 000.000.000-00; descarta tudo que não for dígito (RNF-FE-28). */
export function formatarCpf(valor) {
  const d = somenteDigitos(valor).slice(0, TAMANHO)
  if (d.length <= 3) return d
  if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`
  if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`
  return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`
}

/** CPF com 11 dígitos e dígitos verificadores corretos; rejeita sequências repetidas. */
export function cpfValido(valor) {
  const d = somenteDigitos(valor)
  if (d.length !== TAMANHO || /^(\d)\1+$/.test(d)) {
    return false
  }
  return Number(d[9]) === digitoVerificador(d, 9) && Number(d[10]) === digitoVerificador(d, 10)
}

/** Pesos decrescentes a partir de quantidade + 1 sobre os primeiros `quantidade` dígitos. */
function digitoVerificador(digitos, quantidade) {
  let soma = 0
  for (let i = 0; i < quantidade; i++) {
    soma += Number(digitos[i]) * (quantidade + 1 - i)
  }
  const resto = (soma * 10) % 11
  return resto === 10 ? 0 : resto
}
