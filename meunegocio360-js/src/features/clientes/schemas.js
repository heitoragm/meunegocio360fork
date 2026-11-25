import { z } from 'zod'
import { somenteDigitos } from '../../shared/lib/digitos'
import { TAMANHO_DO_DOCUMENTO, TIPO_PESSOA, documentoValido } from '../../shared/lib/documento'
import { telefoneValido } from '../../shared/lib/telefone'

const MENSAGEM_NOME = 'Informe o nome.'
const MENSAGEM_EMAIL = 'Informe um e-mail válido.'
const emailFormato = z.email()

export const ROTULO_TIPO_PESSOA = Object.freeze({
  [TIPO_PESSOA.FISICA]: 'Pessoa física',
  [TIPO_PESSOA.JURIDICA]: 'Pessoa jurídica',
})

/** Documento conforme o tipo (RN-02 do RF-BE-003): "CPF inválido." ou "CNPJ inválido.". */
export function mensagemDoDocumento(tipo, documento) {
  const digitos = somenteDigitos(documento)
  if (digitos === '') return 'Informe o documento.'
  if (documentoValido(tipo, digitos)) return null
  return tipo === TIPO_PESSOA.JURIDICA ? 'CNPJ inválido.' : 'CPF inválido.'
}

/** O documento digitado não cabe no tipo escolhido (ex.: 14 dígitos com "Pessoa física"). */
export function documentoIncompativel(tipo, documento) {
  return somenteDigitos(documento).length > (TAMANHO_DO_DOCUMENTO[tipo] ?? 14)
}

/**
 * Formulário do cliente, espelhando o backend (RF-FE-003, RNF-FE-20). Na saída, documento e
 * telefone vão só com dígitos e os opcionais vazios como null (RNF-FE-28).
 */
export const clienteSchema = z
  .object({
    tipoPessoa: z.enum(Object.values(TIPO_PESSOA), 'Selecione o tipo de pessoa.'),
    nome: z.string().trim().min(1, MENSAGEM_NOME).min(2, MENSAGEM_NOME).max(150, MENSAGEM_NOME),
    documento: z.string().transform(somenteDigitos),
    email: z
      .string()
      .trim()
      .max(160, MENSAGEM_EMAIL)
      .refine((v) => v === '' || emailFormato.safeParse(v).success, MENSAGEM_EMAIL),
    telefone: z
      .string()
      .transform(somenteDigitos)
      .refine((v) => v === '' || telefoneValido(v), 'Informe um telefone válido com DDD.'),
  })
  .superRefine(
    (dados, ctx) => {
      // Sem tipo escolhido, só a obrigatoriedade: o tamanho esperado depende do tipo.
      const tipoValido = Object.values(TIPO_PESSOA).includes(dados.tipoPessoa)
      const mensagem = tipoValido
        ? mensagemDoDocumento(dados.tipoPessoa, dados.documento)
        : somenteDigitos(dados.documento) === '' && 'Informe o documento.'
      if (mensagem) {
        ctx.addIssue({ code: 'custom', path: ['documento'], message: mensagem })
      }
    },
    // Valida o documento mesmo quando outro campo já falhou: todos os erros aparecem de uma vez.
    { when: ({ value }) => value !== null && typeof value === 'object' },
  )
  .transform((dados) => ({
    ...dados,
    email: dados.email || null,
    telefone: dados.telefone || null,
  }))

/** Campos do formulário, na ordem da tela (usada para ligar errors[] do servidor). */
export const CAMPOS_DO_CLIENTE = ['tipoPessoa', 'nome', 'documento', 'email', 'telefone']

export const VALORES_VAZIOS = Object.freeze({
  tipoPessoa: '',
  nome: '',
  documento: '',
  email: '',
  telefone: '',
})
