import { useState } from 'react'
import { ConfirmModal } from '../../../shared/ui/ConfirmModal'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import { useAlterarSituacaoDoCliente, useExcluirCliente } from '../hooks/useMutacoesDoCliente'
import {
  ERRO,
  MENSAGEM_COM_HISTORICO,
  MENSAGEM_NAO_ENCONTRADO,
  classificarErro,
  mensagemDaAcao,
} from '../mensagensDeErro'

/**
 * Exclusão com confirmação (RF-FE-003, RNF-097). Se o cliente tem histórico (422
 * CUSTOMER_HAS_HISTORY), o modal continua aberto com a mensagem de negócio e passa a oferecer
 * "Inativar cliente". O resultado vai para a página por `aoConcluir({ mensagem, sucesso })`.
 */
export function ExcluirClienteModal({ cliente, aberto, aoFechar, aoConcluir, aoFechado }) {
  const excluir = useExcluirCliente()
  const inativar = useAlterarSituacaoDoCliente()
  const [comHistorico, setComHistorico] = useState(false)
  const [erro, setErro] = useState(null)
  const ocupado = excluir.isPending || inativar.isPending

  const confirmarExclusao = () => {
    setErro(null)
    excluir.mutate(
      { id: cliente.id },
      {
        onSuccess: () => aoConcluir({ mensagem: 'Cliente excluído.', sucesso: true }),
        onError: (falha) => {
          const tipo = classificarErro(falha)
          if (tipo === ERRO.COM_HISTORICO) {
            setComHistorico(true)
          } else if (tipo === ERRO.NAO_ENCONTRADO) {
            aoConcluir({ mensagem: MENSAGEM_NAO_ENCONTRADO, sucesso: false, atualizar: true })
          } else {
            setErro(mensagemDaAcao(falha))
          }
        },
      },
    )
  }

  const confirmarInativacao = () => {
    setErro(null)
    inativar.mutate(
      { id: cliente.id, ativo: false, versao: cliente.versao },
      {
        onSuccess: () => aoConcluir({ mensagem: 'Cliente inativado.', sucesso: true }),
        onError: (falha) => {
          if (classificarErro(falha) === ERRO.NAO_ENCONTRADO) {
            aoConcluir({ mensagem: MENSAGEM_NAO_ENCONTRADO, sucesso: false, atualizar: true })
          } else {
            setErro(mensagemDaAcao(falha))
          }
        },
      },
    )
  }

  if (!cliente) {
    return null
  }

  return (
    <ConfirmModal
      aberto={aberto}
      titulo={comHistorico ? 'Não é possível excluir' : 'Excluir cliente?'}
      textoConfirmar={comHistorico ? 'Inativar cliente' : 'Excluir cliente'}
      varianteConfirmar={comHistorico ? 'primary' : 'danger'}
      confirmando={ocupado}
      aoConfirmar={comHistorico ? confirmarInativacao : confirmarExclusao}
      aoCancelar={aoFechar}
      aoFechado={aoFechado}
    >
      {comHistorico ? (
        <ErrorAlert className="mb-0">{MENSAGEM_COM_HISTORICO}</ErrorAlert>
      ) : (
        <p className="mb-0">
          Excluir o cliente <strong>{cliente.nome}</strong>? Esta ação não pode ser desfeita.
        </p>
      )}
      {erro && <ErrorAlert className="mt-3 mb-0">{erro}</ErrorAlert>}
    </ConfirmModal>
  )
}
