import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Card from 'react-bootstrap/Card'
import ListGroup from 'react-bootstrap/ListGroup'
import Spinner from 'react-bootstrap/Spinner'
import { formatarCnpj } from '../../../shared/lib/cnpj'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { useAlterarSituacaoDaEmpresa, useEmpresasInativas } from '../hooks/useMutacoesDaEmpresa'
import {
  MENSAGEM_CONFLITO_DE_VERSAO,
  MENSAGEM_EMPRESA_JA_ATIVA,
  MENSAGEM_FALHA,
  MENSAGEM_SEM_PERMISSAO,
  classificarErro,
} from '../mensagensDeErro'

const MENSAGEM_FALHA_AO_LISTAR =
  'Não foi possível carregar as empresas inativas. Verifique sua conexão e tente novamente.'

/**
 * Empresas inativas, consultadas em GET /empresa/inativas (ADMIN), com a ação Reativar
 * (RF-FE-002). Só aparece quando há alguma; trata carregamento e erro (RNF-062).
 */
export function EmpresasInativas({ aoReativar }) {
  const [pagina, setPagina] = useState(0)
  const consulta = useEmpresasInativas(pagina)
  const reativar = useAlterarSituacaoDaEmpresa()
  const [erro, setErro] = useState(null)

  if (consulta.isPending) {
    return (
      <p role="status" className="d-flex align-items-center gap-2 mb-4">
        <Spinner animation="border" size="sm" aria-hidden="true" />
        Verificando empresas inativas…
      </p>
    )
  }
  if (consulta.isError) {
    return (
      <ErrorAlert acao="Tentar novamente" aoAcionar={() => consulta.refetch()}>
        {MENSAGEM_FALHA_AO_LISTAR}
      </ErrorAlert>
    )
  }

  const { content: empresas, totalPages, totalElements } = consulta.data
  if (totalElements === 0) {
    return null
  }

  const reativarEmpresa = (empresa) => {
    setErro(null)
    reativar.mutate(
      { id: empresa.id, ativa: true, versao: empresa.versao },
      {
        onSuccess: aoReativar,
        onError: (falha) => {
          const tipo = classificarErro(falha)
          setErro(
            {
              'ja-ativa': MENSAGEM_EMPRESA_JA_ATIVA,
              conflito: MENSAGEM_CONFLITO_DE_VERSAO,
              'sem-permissao': MENSAGEM_SEM_PERMISSAO,
            }[tipo] ?? MENSAGEM_FALHA,
          )
          if (tipo === 'conflito') {
            consulta.refetch()
          }
        },
      },
    )
  }

  const emReativacao = reativar.isPending ? reativar.variables?.id : null

  return (
    <Card className="mb-4">
      <Card.Body>
        <h2 className="h5">Reativar uma empresa inativa</h2>
        <p className="text-body-secondary">
          Em vez de cadastrar outra, você pode reativar uma empresa já cadastrada.
        </p>
        {erro && <ErrorAlert>{erro}</ErrorAlert>}
        <ListGroup className="mb-3">
          {empresas.map((empresa) => (
            <ListGroup.Item
              key={empresa.id}
              className="d-flex flex-column flex-sm-row align-items-sm-center gap-2"
            >
              <div className="flex-grow-1 text-break">
                <span className="fw-semibold d-block">
                  {empresa.nomeFantasia || empresa.razaoSocial}
                </span>
                <span className="small text-body-secondary">CNPJ {formatarCnpj(empresa.cnpj)}</span>
              </div>
              <Button
                variant="outline-dark"
                onClick={() => reativarEmpresa(empresa)}
                disabled={reativar.isPending}
                aria-label={`Reativar ${empresa.nomeFantasia || empresa.razaoSocial}`}
              >
                {emReativacao === empresa.id && (
                  <Spinner
                    as="span"
                    animation="border"
                    size="sm"
                    aria-hidden="true"
                    className="me-2"
                  />
                )}
                Reativar
              </Button>
            </ListGroup.Item>
          ))}
        </ListGroup>
        {totalPages > 1 && (
          <nav
            aria-label="Páginas de empresas inativas"
            className="d-flex align-items-center gap-2"
          >
            <Button
              variant="outline-dark"
              size="sm"
              onClick={() => setPagina((p) => p - 1)}
              disabled={pagina === 0 || consulta.isFetching}
            >
              Anterior
            </Button>
            <span className="small" aria-live="polite">
              Página {pagina + 1} de {totalPages}
            </span>
            <Button
              variant="outline-dark"
              size="sm"
              onClick={() => setPagina((p) => p + 1)}
              disabled={pagina + 1 >= totalPages || consulta.isFetching}
            >
              Próxima
            </Button>
          </nav>
        )}
      </Card.Body>
    </Card>
  )
}
