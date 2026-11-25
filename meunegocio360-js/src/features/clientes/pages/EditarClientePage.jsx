import { useState } from 'react'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import { Link, useNavigate, useParams } from 'react-router'
import { formatarDocumento } from '../../../shared/lib/documento'
import { formatarTelefone } from '../../../shared/lib/telefone'
import { AcessoNegado } from '../../../shared/ui/AcessoNegado'
import { Carregando } from '../../../shared/ui/Carregando'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { StatusBadge } from '../../../shared/ui/StatusBadge'
import { ClienteForm } from '../components/ClienteForm'
import { useAtualizarCliente, useCliente } from '../hooks/useMutacoesDoCliente'
import { ERRO, classificarErro } from '../mensagensDeErro'

/**
 * Edição de cliente (RF-FE-003, rota /clientes/:id). Carrega por GET /clientes/{id};
 * CUSTOMER_NOT_FOUND mostra "Cliente não encontrado". O PUT leva a `versao` lida (RN-09 do
 * RF-BE-003) e, em conflito, "Recarregar dados" busca a versão atual.
 */
export default function EditarClientePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const consulta = useCliente(id)
  const atualizar = useAtualizarCliente()
  const [acessoNegado, setAcessoNegado] = useState(false)
  const cliente = consulta.data

  if (acessoNegado) {
    return <AcessoNegado />
  }
  if (consulta.isPending) {
    return <Carregando mensagem="Carregando cliente…" />
  }
  if (consulta.isError) {
    const tipo = classificarErro(consulta.error)
    if (tipo === ERRO.SEM_PERMISSAO) {
      return <AcessoNegado />
    }
    // Um id malformado também não identifica nenhum cliente da empresa.
    if (tipo === ERRO.NAO_ENCONTRADO || consulta.error.status === 400) {
      return <ClienteNaoEncontrado />
    }
    return (
      <Container className="py-5 mn-conteudo-medio">
        <title>Editar cliente · MeuNegócio360</title>
        <h1>Editar cliente</h1>
        <ErrorAlert
          acao="Tentar novamente"
          aoAcionar={() => consulta.refetch()}
          acaoDesabilitada={consulta.isFetching}
        >
          Não foi possível carregar o cliente. Verifique sua conexão e tente novamente.
        </ErrorAlert>
      </Container>
    )
  }

  const salvar = async (dados) => {
    await atualizar.mutateAsync({ id: cliente.id, dados: { ...dados, versao: cliente.versao } })
    navigate('/clientes', { state: { aviso: 'Cliente atualizado com sucesso.' } })
  }

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>Editar cliente · MeuNegócio360</title>
      <h1>Editar cliente</h1>
      <p className="d-flex flex-wrap align-items-center gap-2">
        Situação: <StatusBadge ativo={cliente.ativo} />
      </p>
      <Card className="mt-3">
        <Card.Body className="p-4">
          <ClienteForm
            // Nova versão do servidor (após "Recarregar dados") recria o formulário com ela.
            key={cliente.versao}
            valoresIniciais={valoresDoFormulario(cliente)}
            textoEnviar="Salvar alterações"
            aoEnviar={salvar}
            aoCancelar={() => navigate('/clientes')}
            aoRecarregar={() => consulta.refetch()}
            aoNegarAcesso={() => setAcessoNegado(true)}
          />
        </Card.Body>
      </Card>
    </Container>
  )
}

function valoresDoFormulario(cliente) {
  return {
    tipoPessoa: cliente.tipoPessoa,
    nome: cliente.nome,
    documento: formatarDocumento(cliente.tipoPessoa, cliente.documento),
    email: cliente.email ?? '',
    telefone: formatarTelefone(cliente.telefone),
  }
}

function ClienteNaoEncontrado() {
  return (
    <Container className="py-5">
      <title>Cliente não encontrado · MeuNegócio360</title>
      <h1>Cliente não encontrado</h1>
      <p>Este cliente não existe ou foi excluído.</p>
      <Link to="/clientes">Voltar para a lista de clientes</Link>
    </Container>
  )
}
