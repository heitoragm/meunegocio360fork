import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import Spinner from 'react-bootstrap/Spinner'
import { Navigate, useNavigate } from 'react-router'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { TelaDeErro } from '../../../shared/ui/TelaDeErro'
import { EmpresaForm } from '../components/EmpresaForm'
import { ESTADO_EMPRESA } from '../empresaContext'
import { useEmpresa } from '../hooks/useEmpresa'
import { useAlterarSituacaoDaEmpresa, useCadastrarEmpresa } from '../hooks/useMutacoesDaEmpresa'
import {
  MENSAGEM_EMPRESA_JA_ATIVA,
  MENSAGEM_FALHA,
  MENSAGEM_SEM_PERMISSAO,
  classificarErro,
} from '../mensagensDeErro'

/**
 * Primeiro cadastro da empresa (RF-FE-002, rota /empresa/cadastro, somente ADMIN pela RequireRole).
 * Com empresa ativa, redireciona para /empresa.
 */
export default function CadastroEmpresaPage() {
  const { estado, empresaInativada, recarregar } = useEmpresa()
  const navigate = useNavigate()
  const cadastrar = useCadastrarEmpresa()
  const reativar = useAlterarSituacaoDaEmpresa()
  const [erroReativacao, setErroReativacao] = useState(null)

  // Depois de um envio bem-sucedido, a navegação é feita pelo próprio envio (com a mensagem).
  if (estado === ESTADO_EMPRESA.ATIVA && !cadastrar.isSuccess && !reativar.isSuccess) {
    return <Navigate to="/empresa" replace />
  }
  if (estado === ESTADO_EMPRESA.ERRO) {
    return (
      <TelaDeErro
        mensagem="Não foi possível carregar os dados da empresa. Verifique sua conexão e tente novamente."
        aoTentarNovamente={recarregar}
      />
    )
  }

  const enviar = async (dados) => {
    await cadastrar.mutateAsync(dados)
    navigate('/', { replace: true, state: { aviso: 'Empresa cadastrada com sucesso.' } })
  }

  const reativarEmpresa = () => {
    setErroReativacao(null)
    reativar.mutate(
      { id: empresaInativada.id, ativa: true, versao: empresaInativada.versao },
      {
        onSuccess: () =>
          navigate('/empresa', { replace: true, state: { aviso: 'Empresa reativada.' } }),
        onError: (falha) => {
          const tipo = classificarErro(falha)
          setErroReativacao(
            tipo === 'ja-ativa'
              ? MENSAGEM_EMPRESA_JA_ATIVA
              : tipo === 'sem-permissao'
                ? MENSAGEM_SEM_PERMISSAO
                : MENSAGEM_FALHA,
          )
        },
      },
    )
  }

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>Cadastre sua empresa · MeuNegócio360</title>
      <h1>Cadastre sua empresa</h1>
      <p className="text-body-secondary mb-4">
        Os dados da empresa são usados em todos os módulos do sistema. Campos sem “(opcional)” são
        obrigatórios.
      </p>

      {empresaInativada && (
        <Card className="mb-4">
          <Card.Body>
            <h2 className="h5">Empresa inativada</h2>
            <p>
              A empresa <strong>{empresaInativada.nomeFantasia || empresaInativada.razaoSocial}</strong>{' '}
              foi inativada nesta sessão. Você pode reativá-la em vez de cadastrar outra.
            </p>
            {erroReativacao && <ErrorAlert>{erroReativacao}</ErrorAlert>}
            <Button variant="outline-dark" onClick={reativarEmpresa} disabled={reativar.isPending}>
              {reativar.isPending && (
                <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
              )}
              Reativar empresa
            </Button>
          </Card.Body>
        </Card>
      )}

      <Card>
        <Card.Body className="p-4">
          <EmpresaForm textoEnviar="Cadastrar empresa" aoEnviar={enviar} />
        </Card.Body>
      </Card>
    </Container>
  )
}
