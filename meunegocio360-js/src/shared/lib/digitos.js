/** Mantém apenas os dígitos de um texto (máscaras e envio à API, RNF-FE-28). */
export function somenteDigitos(valor) {
  return String(valor ?? '').replace(/\D/g, '')
}
