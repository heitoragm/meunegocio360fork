import { cnpjValido, formatarCnpj } from './cnpj'
import { cpfValido, formatarCpf } from './cpf'
import { somenteDigitos } from './digitos'

/** Tipo de pessoa (RN-02 do RF-BE-003): física usa CPF; jurídica, CNPJ. */
export const TIPO_PESSOA = Object.freeze({ FISICA: 'FISICA', JURIDICA: 'JURIDICA' })

export const TAMANHO_DO_DOCUMENTO = Object.freeze({
  [TIPO_PESSOA.FISICA]: 11,
  [TIPO_PESSOA.JURIDICA]: 14,
})

/**
 * Máscara de CPF ou CNPJ conforme o tipo. Sem tipo (ex.: exibição em tabela), decide pelo
 * tamanho: até 11 dígitos é CPF.
 */
export function formatarDocumento(tipo, valor) {
  if (tipo === TIPO_PESSOA.JURIDICA) return formatarCnpj(valor)
  if (tipo === TIPO_PESSOA.FISICA) return formatarCpf(valor)
  return somenteDigitos(valor).length > 11 ? formatarCnpj(valor) : formatarCpf(valor)
}

export function documentoValido(tipo, valor) {
  return tipo === TIPO_PESSOA.JURIDICA ? cnpjValido(valor) : cpfValido(valor)
}
