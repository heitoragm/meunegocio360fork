import { formatarCnpj } from '../../shared/lib/cnpj'
import { formatarTelefone } from '../../shared/lib/telefone'
import { FUSO_PADRAO } from './schemas'

export const VALORES_VAZIOS = Object.freeze({
  razaoSocial: '',
  nomeFantasia: '',
  cnpj: '',
  email: '',
  telefone: '',
  fusoHorario: FUSO_PADRAO,
})

/** Converte a empresa da API para os valores exibidos no formulário (com máscara). */
export function valoresDoFormulario(empresa) {
  if (!empresa) {
    return VALORES_VAZIOS
  }
  return {
    razaoSocial: empresa.razaoSocial ?? '',
    nomeFantasia: empresa.nomeFantasia ?? '',
    cnpj: formatarCnpj(empresa.cnpj),
    email: empresa.email ?? '',
    telefone: formatarTelefone(empresa.telefone),
    fusoHorario: empresa.fusoHorario ?? FUSO_PADRAO,
  }
}
