import { z } from 'zod'
import { cnpjValido } from '../../shared/lib/cnpj'
import { somenteDigitos } from '../../shared/lib/digitos'
import { telefoneValido } from '../../shared/lib/telefone'

export const FUSO_PADRAO = 'America/Sao_Paulo'
export const MOEDA = 'BRL'

/** Fusos horários brasileiros (IANA) mais UTC (RF-FE-002, RN-05 do RF-BE-002). */
export const FUSOS_HORARIOS = Object.freeze([
  { id: 'America/Noronha', nome: 'Fernando de Noronha (UTC−02:00)' },
  { id: 'America/Sao_Paulo', nome: 'Brasília / São Paulo (UTC−03:00)' },
  { id: 'America/Bahia', nome: 'Bahia (UTC−03:00)' },
  { id: 'America/Fortaleza', nome: 'Fortaleza (UTC−03:00)' },
  { id: 'America/Recife', nome: 'Recife (UTC−03:00)' },
  { id: 'America/Maceio', nome: 'Maceió (UTC−03:00)' },
  { id: 'America/Belem', nome: 'Belém (UTC−03:00)' },
  { id: 'America/Araguaina', nome: 'Araguaína (UTC−03:00)' },
  { id: 'America/Santarem', nome: 'Santarém (UTC−03:00)' },
  { id: 'America/Campo_Grande', nome: 'Campo Grande (UTC−04:00)' },
  { id: 'America/Cuiaba', nome: 'Cuiabá (UTC−04:00)' },
  { id: 'America/Manaus', nome: 'Manaus (UTC−04:00)' },
  { id: 'America/Porto_Velho', nome: 'Porto Velho (UTC−04:00)' },
  { id: 'America/Boa_Vista', nome: 'Boa Vista (UTC−04:00)' },
  { id: 'America/Rio_Branco', nome: 'Rio Branco (UTC−05:00)' },
  { id: 'America/Eirunepe', nome: 'Eirunepé (UTC−05:00)' },
  { id: 'UTC', nome: 'UTC' },
])

const MENSAGEM_RAZAO_SOCIAL = 'A razão social deve ter entre 3 e 150 caracteres.'
const MENSAGEM_EMAIL = 'Informe um e-mail válido.'
const emailFormato = z.email()

/**
 * Formulário da empresa, espelhando o backend (RNF-FE-20). Na saída, CNPJ e telefone ficam só com
 * dígitos e os opcionais vazios viram null (RNF-FE-28).
 */
export const empresaSchema = z
  .object({
    razaoSocial: z
      .string()
      .trim()
      .min(1, 'Informe a razão social.')
      .min(3, MENSAGEM_RAZAO_SOCIAL)
      .max(150, MENSAGEM_RAZAO_SOCIAL),
    nomeFantasia: z.string().trim().max(150, 'O nome fantasia deve ter no máximo 150 caracteres.'),
    cnpj: z
      .string()
      .transform(somenteDigitos)
      .pipe(z.string().min(1, 'Informe o CNPJ.').refine(cnpjValido, 'CNPJ inválido.')),
    email: z
      .string()
      .trim()
      .max(160, MENSAGEM_EMAIL)
      .refine((v) => v === '' || emailFormato.safeParse(v).success, MENSAGEM_EMAIL),
    telefone: z
      .string()
      .transform(somenteDigitos)
      .refine((v) => v === '' || telefoneValido(v), 'Informe um telefone válido com DDD.'),
    fusoHorario: z.enum(
      FUSOS_HORARIOS.map((f) => f.id),
      'Selecione o fuso horário.',
    ),
  })
  .transform((dados) => ({
    ...dados,
    nomeFantasia: dados.nomeFantasia || null,
    email: dados.email || null,
    telefone: dados.telefone || null,
    moeda: MOEDA,
  }))

/** Campos do formulário, na ordem da tela (usada para ligar errors[] do servidor). */
export const CAMPOS_DA_EMPRESA = [
  'razaoSocial',
  'nomeFantasia',
  'cnpj',
  'email',
  'telefone',
  'fusoHorario',
]
