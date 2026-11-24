import { formatarCnpj } from '../../../shared/lib/cnpj'
import { formatarTelefone } from '../../../shared/lib/telefone'
import { FUSOS_HORARIOS } from '../schemas'

const NAO_INFORMADO = 'Não informado'

/** Dados da empresa em modo leitura, como lista de descrição semântica. */
export function DadosDaEmpresa({ empresa }) {
  const fuso = FUSOS_HORARIOS.find((f) => f.id === empresa.fusoHorario)?.nome ?? empresa.fusoHorario
  const itens = [
    ['Razão social', empresa.razaoSocial],
    ['Nome fantasia', empresa.nomeFantasia || NAO_INFORMADO],
    ['CNPJ', formatarCnpj(empresa.cnpj)],
    ['E-mail', empresa.email || NAO_INFORMADO],
    ['Telefone', empresa.telefone ? formatarTelefone(empresa.telefone) : NAO_INFORMADO],
    ['Fuso horário', fuso],
    ['Moeda', empresa.moeda],
    ['Situação', empresa.ativa ? 'Ativa' : 'Inativa'],
  ]
  return (
    <dl className="row mb-0">
      {itens.map(([rotulo, valor]) => (
        <div key={rotulo} className="col-12 col-md-6 mb-3">
          <dt className="text-body-secondary fw-normal">{rotulo}</dt>
          <dd className="mb-0 fw-semibold text-break">{valor}</dd>
        </div>
      ))}
    </dl>
  )
}
