import Button from 'react-bootstrap/Button'
import Card from 'react-bootstrap/Card'
import Table from 'react-bootstrap/Table'
import { Link } from 'react-router'
import { formatarDocumento } from '../../../shared/lib/documento'
import { formatarTelefone } from '../../../shared/lib/telefone'
import { StatusBadge } from '../../../shared/ui/StatusBadge'

/**
 * Clientes da página atual (RF-FE-003). A partir de `md`, tabela com legenda e cabeçalhos
 * associados; no celular, lista de cartões, sem rolagem horizontal da página (RNF-FE-26).
 */
export function TabelaDeClientes({ clientes, ocupado, aoInativar, aoReativar, aoExcluir }) {
  const acoes = { ocupado, aoInativar, aoReativar, aoExcluir }
  return (
    <>
      <Table responsive hover className="d-none d-md-table align-middle mb-0">
        <caption className="visually-hidden">Clientes</caption>
        <thead>
          <tr>
            <th scope="col">Nome</th>
            <th scope="col">Documento</th>
            <th scope="col">Telefone</th>
            <th scope="col">E-mail</th>
            <th scope="col">Situação</th>
            <th scope="col">Ações</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id}>
              <th scope="row" className="fw-semibold">
                {cliente.nome}
              </th>
              <td className="text-nowrap">
                {formatarDocumento(cliente.tipoPessoa, cliente.documento)}
              </td>
              <td className="text-nowrap">{formatarTelefone(cliente.telefone) || '—'}</td>
              <td className="text-break">{cliente.email || '—'}</td>
              <td>
                <StatusBadge ativo={cliente.ativo} />
              </td>
              <td>
                <AcoesDoCliente cliente={cliente} {...acoes} />
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <ul className="list-unstyled d-md-none mb-0" aria-label="Clientes">
        {clientes.map((cliente) => (
          <li key={cliente.id} className="mb-3">
            <Card>
              <Card.Body>
                <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                  <h2 className="h5 mb-0 text-break">{cliente.nome}</h2>
                  <StatusBadge ativo={cliente.ativo} />
                </div>
                <dl className="mb-3">
                  <dt className="small">Documento</dt>
                  <dd>{formatarDocumento(cliente.tipoPessoa, cliente.documento)}</dd>
                  <dt className="small">Telefone</dt>
                  <dd>{formatarTelefone(cliente.telefone) || '—'}</dd>
                  <dt className="small">E-mail</dt>
                  <dd className="text-break mb-0">{cliente.email || '—'}</dd>
                </dl>
                <AcoesDoCliente cliente={cliente} {...acoes} />
              </Card.Body>
            </Card>
          </li>
        ))}
      </ul>
    </>
  )
}

/** Editar, Inativar (ou Reativar) e Excluir, com o nome no rótulo acessível de cada ação. */
function AcoesDoCliente({ cliente, ocupado, aoInativar, aoReativar, aoExcluir }) {
  return (
    <div className="d-flex flex-wrap gap-2">
      <Link
        to={`/clientes/${cliente.id}`}
        className="btn btn-outline-dark"
        aria-label={`Editar ${cliente.nome}`}
      >
        Editar
      </Link>
      {cliente.ativo ? (
        <Button
          variant="outline-dark"
          onClick={() => aoInativar(cliente)}
          disabled={ocupado}
          aria-label={`Inativar ${cliente.nome}`}
        >
          Inativar
        </Button>
      ) : (
        <Button
          variant="outline-dark"
          onClick={() => aoReativar(cliente)}
          disabled={ocupado}
          aria-label={`Reativar ${cliente.nome}`}
        >
          Reativar
        </Button>
      )}
      <Button
        variant="outline-danger"
        onClick={() => aoExcluir(cliente)}
        disabled={ocupado}
        aria-label={`Excluir ${cliente.nome}`}
      >
        Excluir
      </Button>
    </div>
  )
}
