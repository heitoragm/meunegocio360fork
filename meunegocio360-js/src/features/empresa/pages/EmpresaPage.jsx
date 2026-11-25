import { useEffect, useRef, useState } from 'react'
import Button from 'react-bootstrap/Button'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import { useLocation } from 'react-router'
import { PERFIS } from '../../../shared/lib/perfis'
import { AvisoDeSucesso } from '../../../shared/ui/AvisoDeSucesso'
import { ConfirmModal } from '../../../shared/ui/ConfirmModal'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { useAuth } from '../../auth'
import { DadosDaEmpresa } from '../components/DadosDaEmpresa'
import { EmpresaForm } from '../components/EmpresaForm'
import { useEmpresa } from '../hooks/useEmpresa'
import { useAlterarSituacaoDaEmpresa, useAtualizarEmpresa } from '../hooks/useMutacoesDaEmpresa'
import {
  MENSAGEM_CONFLITO_DE_VERSAO,
  MENSAGEM_FALHA,
  MENSAGEM_SEM_PERMISSAO,
  classificarErro,
} from '../mensagensDeErro'
import { valoresDoFormulario } from '../valoresDoFormulario'

const TEXTO_INATIVACAO =
  'Ao inativar a empresa, não será possível cadastrar clientes, fornecedores, produtos nem vendas até que uma empresa seja ativada.'

/**
 * Dados da empresa ativa (RF-FE-002, rota /empresa). ADMIN edita e inativa; os demais perfis só
 * consultam (RNF-FE-16). Fica sob RequireEmpresa, então sempre há empresa ativa aqui.
 */
export default function EmpresaPage() {
  const { usuario } = useAuth()
  const { empresa, recarregar } = useEmpresa()
  const location = useLocation()
  const atualizar = useAtualizarEmpresa()
  const situacao = useAlterarSituacaoDaEmpresa()
  const [editando, setEditando] = useState(false)
  const [aviso, setAviso] = useState(location.state?.aviso ?? null)
  const [modalAberto, setModalAberto] = useState(false)
  const [erroSituacao, setErroSituacao] = useState(null)
  const editarRef = useRef(null)
  const saiuDaEdicao = useRef(false)
  const ehAdmin = usuario.perfil === PERFIS.ADMIN

  // Ao sair do modo de edição, o foco volta ao botão Editar (RNF-FE-23).
  useEffect(() => {
    if (!editando && saiuDaEdicao.current) {
      editarRef.current?.focus()
      saiuDaEdicao.current = false
    }
  }, [editando])

  const iniciarEdicao = () => {
    setAviso(null)
    setErroSituacao(null)
    setEditando(true)
  }

  const encerrarEdicao = () => {
    saiuDaEdicao.current = true
    setEditando(false)
  }

  const salvar = async (dados) => {
    await atualizar.mutateAsync({ id: empresa.id, dados: { ...dados, versao: empresa.versao } })
    setAviso('Dados da empresa atualizados.')
    encerrarEdicao()
  }

  const inativar = () => {
    situacao.mutate(
      { id: empresa.id, ativa: false, versao: empresa.versao },
      {
        // Sem empresa ativa, as guardas passam ao estado "ausente" e tiram o usuário daqui.
        onSuccess: () => setModalAberto(false),
        onError: (falha) => {
          setModalAberto(false)
          const tipo = classificarErro(falha)
          setErroSituacao(
            tipo === 'conflito'
              ? { mensagem: MENSAGEM_CONFLITO_DE_VERSAO, recarregar: true }
              : { mensagem: tipo === 'sem-permissao' ? MENSAGEM_SEM_PERMISSAO : MENSAGEM_FALHA },
          )
        },
      },
    )
  }

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>Empresa · MeuNegócio360</title>
      <h1>Empresa</h1>
      {!ehAdmin && (
        <p className="text-body-secondary">
          Somente o administrador pode alterar os dados da empresa.
        </p>
      )}

      {aviso && <AvisoDeSucesso aoFechar={() => setAviso(null)}>{aviso}</AvisoDeSucesso>}
      {erroSituacao && (
        <ErrorAlert
          {...(erroSituacao.recarregar
            ? {
                acao: 'Recarregar dados',
                aoAcionar: () => {
                  setErroSituacao(null)
                  recarregar()
                },
              }
            : {})}
        >
          {erroSituacao.mensagem}
        </ErrorAlert>
      )}

      <Card className="mt-3">
        <Card.Body className="p-4">
          {editando ? (
            <EmpresaForm
              key={empresa.versao}
              valoresIniciais={valoresDoFormulario(empresa)}
              textoEnviar="Salvar alterações"
              aoEnviar={salvar}
              aoCancelar={encerrarEdicao}
              aoRecarregar={recarregar}
            />
          ) : (
            <>
              <DadosDaEmpresa empresa={empresa} />
              {ehAdmin && (
                <div className="d-flex flex-wrap gap-2 mt-4">
                  <Button ref={editarRef} variant="primary" onClick={iniciarEdicao}>
                    Editar
                  </Button>
                  <Button variant="outline-danger" onClick={() => setModalAberto(true)}>
                    Inativar
                  </Button>
                </div>
              )}
            </>
          )}
        </Card.Body>
      </Card>

      {ehAdmin && (
        <ConfirmModal
          aberto={modalAberto}
          titulo="Inativar a empresa?"
          textoConfirmar="Inativar empresa"
          confirmando={situacao.isPending}
          aoConfirmar={inativar}
          aoCancelar={() => setModalAberto(false)}
        >
          {TEXTO_INATIVACAO}
        </ConfirmModal>
      )}
    </Container>
  )
}
