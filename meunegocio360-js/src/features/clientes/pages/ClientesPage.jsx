import { useCallback, useRef, useState } from 'react'
import Alert from 'react-bootstrap/Alert'
import Button from 'react-bootstrap/Button'
import Container from 'react-bootstrap/Container'
import Spinner from 'react-bootstrap/Spinner'
import { Link, useLocation } from 'react-router'
import { useUrlFilters } from '../../../shared/hooks/useUrlFilters'
import { AvisoDeSucesso } from '../../../shared/ui/AvisoDeSucesso'
import { ConfirmModal } from '../../../shared/ui/ConfirmModal'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { ListToolbar } from '../../../shared/ui/ListToolbar'
import { Paginacao } from '../../../shared/ui/Paginacao'
import { ExcluirClienteModal } from '../components/ExcluirClienteModal'
import { TabelaDeClientes } from '../components/TabelaDeClientes'
import { SITUACAO, useBuscaClientes } from '../hooks/useBuscaClientes'
import {
  useAlterarSituacaoDoCliente,
  useAtualizarListaDeClientes,
} from '../hooks/useMutacoesDoCliente'
import { mensagemDaAcao } from '../mensagensDeErro'

const TAMANHO_DA_PAGINA = 20
const FILTROS_PADRAO = Object.freeze({ q: '', situacao: SITUACAO.ATIVO })
const OPCOES_DE_SITUACAO = [
  { valor: SITUACAO.ATIVO, rotulo: 'Ativos' },
  { valor: SITUACAO.INATIVO, rotulo: 'Inativos' },
  { valor: SITUACAO.TODOS, rotulo: 'Todos' },
]

/**
 * Lista de clientes (RF-FE-003, rota /clientes): busca por nome ou documento, filtro de situação e
 * paginação no servidor, tudo na URL (RNF-FE-27), com os estados de carregamento, vazio, erro e
 * sucesso (RNF-062).
 */
export default function ClientesPage() {
  const location = useLocation()
  const { filtros, pagina, filtrando, definirFiltros, definirPagina, limpar } =
    useUrlFilters(FILTROS_PADRAO)
  const situacaoValida = OPCOES_DE_SITUACAO.some((o) => o.valor === filtros.situacao)
  const situacao = situacaoValida ? filtros.situacao : SITUACAO.ATIVO
  const busca = useBuscaClientes({ q: filtros.q, situacao, page: pagina, size: TAMANHO_DA_PAGINA })
  const alterarSituacao = useAlterarSituacaoDoCliente()
  const atualizarLista = useAtualizarListaDeClientes()

  const [aviso, setAviso] = useState(location.state?.aviso ?? null)
  const [erroDaAcao, setErroDaAcao] = useState(null)
  const [inativando, setInativando] = useState(null)
  const [exclusao, setExclusao] = useState({ cliente: null, aberto: false, vez: 0 })
  const [focarAoFechar, setFocarAoFechar] = useState(false)
  const mensagensRef = useRef(null)

  // Depois de inativar, reativar ou excluir, a linha (e o botão que tinha o foco) pode sumir da
  // lista: o foco vai para a mensagem do resultado, sem cair no início da página (RNF-FE-23).
  const focarMensagens = () => mensagensRef.current?.focus()
  const aoFecharModal = () => {
    if (focarAoFechar) {
      setFocarAoFechar(false)
      focarMensagens()
    }
  }

  const aoBuscar = useCallback((q) => definirFiltros({ q }, { substituir: true }), [definirFiltros])

  const anunciar = ({ mensagem, sucesso }) => {
    setErroDaAcao(sucesso ? null : mensagem)
    setAviso(sucesso ? mensagem : null)
  }

  /** Inativar vem do modal (o foco é tratado quando ele fecha); reativar é direto. */
  const mudarSituacao = (cliente, ativo, peloModal = false) => {
    setAviso(null)
    setErroDaAcao(null)
    alterarSituacao.mutate(
      { id: cliente.id, ativo, versao: cliente.versao },
      {
        onSuccess: () =>
          anunciar({
            mensagem: ativo ? 'Cliente reativado.' : 'Cliente inativado.',
            sucesso: true,
          }),
        onError: (falha) => {
          anunciar({ mensagem: mensagemDaAcao(falha), sucesso: false })
          atualizarLista()
        },
        onSettled: () => {
          if (peloModal) {
            setFocarAoFechar(true)
            setInativando(null)
          } else {
            focarMensagens()
          }
        },
      },
    )
  }

  const abrirExclusao = (cliente) => {
    setAviso(null)
    setErroDaAcao(null)
    setExclusao((atual) => ({ cliente, aberto: true, vez: atual.vez + 1 }))
  }

  const concluirExclusao = (resultado) => {
    setExclusao((atual) => ({ ...atual, aberto: false }))
    setFocarAoFechar(true)
    anunciar(resultado)
    if (resultado.atualizar) {
      atualizarLista()
    }
  }

  const resultado = busca.data
  const clientes = resultado?.content ?? []
  // A página pedida pode não existir mais (ex.: após excluir o último cliente dela).
  const paginaInexistente =
    resultado && pagina > 0 && clientes.length === 0 && resultado.totalPages > 0

  return (
    <Container className="py-5">
      <title>Clientes · MeuNegócio360</title>
      <h1>Clientes</h1>

      <div ref={mensagensRef} tabIndex={-1}>
        {aviso && <AvisoDeSucesso aoFechar={() => setAviso(null)}>{aviso}</AvisoDeSucesso>}
        {erroDaAcao && <ErrorAlert>{erroDaAcao}</ErrorAlert>}
      </div>

      <ListToolbar
        rotuloBusca="Buscar por nome ou documento"
        busca={filtros.q}
        aoBuscar={aoBuscar}
        filtro={{
          rotulo: 'Situação',
          valor: situacao,
          opcoes: OPCOES_DE_SITUACAO,
          aoAlterar: (valor) => definirFiltros({ situacao: valor }),
        }}
      >
        <Link to="/clientes/novo" className="btn btn-primary">
          Novo cliente
        </Link>
      </ListToolbar>

      <div aria-busy={busca.isFetching}>
        {busca.isPending ? (
          <div role="status" className="d-flex align-items-center gap-2 py-4">
            <Spinner animation="border" size="sm" aria-hidden="true" />
            Carregando clientes…
          </div>
        ) : busca.isError ? (
          <ErrorAlert
            acao="Tentar novamente"
            aoAcionar={() => busca.refetch()}
            acaoDesabilitada={busca.isFetching}
          >
            Não foi possível carregar os clientes. Verifique sua conexão e tente novamente.
          </ErrorAlert>
        ) : paginaInexistente ? (
          <Alert variant="light" className="border">
            <p>Esta página não tem clientes.</p>
            <Button variant="outline-dark" onClick={() => definirPagina(0)}>
              Ir para a primeira página
            </Button>
          </Alert>
        ) : clientes.length === 0 ? (
          <ListaVazia filtrando={filtrando} aoLimpar={limpar} />
        ) : (
          <>
            <p className="visually-hidden" role="status">
              {resultado.totalElements === 1
                ? '1 cliente encontrado.'
                : `${resultado.totalElements} clientes encontrados.`}
            </p>
            <TabelaDeClientes
              clientes={clientes}
              ocupado={alterarSituacao.isPending}
              aoInativar={(cliente) => {
                setAviso(null)
                setErroDaAcao(null)
                setInativando(cliente)
              }}
              aoReativar={(cliente) => mudarSituacao(cliente, true)}
              aoExcluir={abrirExclusao}
            />
            <Paginacao
              pagina={pagina}
              totalPaginas={resultado.totalPages}
              aoMudar={definirPagina}
              desabilitada={busca.isFetching}
            />
          </>
        )}
      </div>

      <ConfirmModal
        aberto={Boolean(inativando)}
        titulo="Inativar cliente?"
        textoConfirmar="Inativar cliente"
        varianteConfirmar="primary"
        confirmando={alterarSituacao.isPending}
        aoConfirmar={() => mudarSituacao(inativando, false, true)}
        aoCancelar={() => setInativando(null)}
        aoFechado={aoFecharModal}
      >
        {inativando && (
          <p className="mb-0">
            Inativar o cliente <strong>{inativando.nome}</strong>? Ele deixa de aparecer nas
            seleções de outras telas, mas continua consultável e pode ser reativado.
          </p>
        )}
      </ConfirmModal>

      <ExcluirClienteModal
        key={exclusao.vez}
        cliente={exclusao.cliente}
        aberto={exclusao.aberto}
        aoFechar={() => setExclusao((atual) => ({ ...atual, aberto: false }))}
        aoFechado={aoFecharModal}
        aoConcluir={concluirExclusao}
      />
    </Container>
  )
}

/** Vazio sem filtro convida a cadastrar; vazio com filtro oferece limpar (RF-FE-003, RNF-062). */
function ListaVazia({ filtrando, aoLimpar }) {
  if (filtrando) {
    return (
      <div role="status" className="py-4">
        <p>Nenhum cliente encontrado para a busca.</p>
        <Button variant="outline-dark" onClick={aoLimpar}>
          Limpar filtros
        </Button>
      </div>
    )
  }
  return (
    <div role="status" className="py-4">
      <p>Nenhum cliente cadastrado. Cadastre o primeiro cliente.</p>
      <Link to="/clientes/novo" className="btn btn-primary">
        Novo cliente
      </Link>
    </div>
  )
}
