import { useEffect, useId, useState } from 'react'
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import { useDebounce } from '../../../shared/hooks/useDebounce'
import { formatarDocumento } from '../../../shared/lib/documento'
import { SITUACAO, useBuscaClientes } from '../hooks/useBuscaClientes'

const MINIMO_DE_CARACTERES = 2
const TAMANHO_DA_SUGESTAO = 10

/**
 * Seletor de cliente para qualquer tela que o referencie (RF-FE-003). Segue o padrão ARIA de
 * combobox com lista (`role="combobox"` + `role="listbox"`), busca por nome ou documento com
 * debounce de 400 ms a partir de 2 caracteres e oferece somente clientes ATIVOS. A busca é a mesma
 * da lista de clientes (useBuscaClientes); telas futuras reutilizam este componente.
 *
 * Teclado: setas navegam, Enter seleciona, Esc fecha e Tab sai do campo.
 * `onChange` recebe `{ id, nome, documento }`, ou `null` quando o texto é alterado.
 */
export function ClienteSelect({
  value,
  onChange,
  label = 'Cliente',
  invalid = false,
  disabled = false,
  id: idExterno,
  'aria-describedby': descritoPor,
}) {
  const idGerado = useId()
  const id = idExterno ?? `cliente-select-${idGerado}`
  const idLista = `${id}-lista`
  const idStatus = `${id}-status`

  const [texto, setTexto] = useState(value?.nome ?? '')
  const [valorVisto, setValorVisto] = useState(value)
  const [aberto, setAberto] = useState(false)
  const [ativo, setAtivo] = useState(-1)
  const termo = useDebounce(texto)

  // O valor mudou por fora (ex.: o formulário foi limpo): o campo acompanha.
  if (value !== valorVisto) {
    setValorVisto(value)
    setTexto(value?.nome ?? '')
  }

  const termoValido = termo.trim().length >= MINIMO_DE_CARACTERES
  const busca = useBuscaClientes(
    { q: termo, situacao: SITUACAO.ATIVO, page: 0, size: TAMANHO_DA_SUGESTAO },
    { enabled: aberto && termoValido && !disabled },
  )
  const digitando = texto !== termo
  const textoValido = texto.trim().length >= MINIMO_DE_CARACTERES
  const sugestoes =
    aberto && textoValido && termoValido && !busca.isError ? (busca.data?.content ?? []) : []
  const listaVisivel = sugestoes.length > 0
  const status = mensagemDeStatus({ aberto, textoValido, digitando, busca, sugestoes })

  useEffect(() => {
    if (ativo >= 0) {
      document.getElementById(`${id}-opcao-${ativo}`)?.scrollIntoView({ block: 'nearest' })
    }
  }, [ativo, id])

  const selecionar = (cliente) => {
    const escolhido = { id: cliente.id, nome: cliente.nome, documento: cliente.documento }
    onChange(escolhido)
    setValorVisto(escolhido)
    setTexto(cliente.nome)
    setAberto(false)
    setAtivo(-1)
  }

  const aoDigitar = (e) => {
    setTexto(e.target.value)
    setAberto(true)
    setAtivo(-1)
    if (value) {
      // Texto alterado desfaz a seleção, sem apagar o que está sendo digitado.
      setValorVisto(null)
      onChange(null)
    }
  }

  const aoTeclar = (e) => {
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault()
        setAberto(true)
        if (listaVisivel) setAtivo((i) => (i + 1) % sugestoes.length)
        break
      case 'ArrowUp':
        e.preventDefault()
        setAberto(true)
        if (listaVisivel) setAtivo((i) => (i <= 0 ? sugestoes.length - 1 : i - 1))
        break
      case 'Enter':
        if (listaVisivel && ativo >= 0) {
          e.preventDefault()
          selecionar(sugestoes[ativo])
        }
        break
      case 'Escape':
        if (aberto) {
          e.preventDefault()
          setAberto(false)
          setAtivo(-1)
        }
        break
      case 'Tab':
        setAberto(false)
        setAtivo(-1)
        break
      default:
    }
  }

  return (
    <Form.Group className="mb-3 position-relative" controlId={id}>
      <Form.Label>{label}</Form.Label>
      <Form.Control
        type="text"
        role="combobox"
        autoComplete="off"
        placeholder="Digite o nome ou o documento"
        value={texto}
        onChange={aoDigitar}
        onKeyDown={aoTeclar}
        onFocus={() => setAberto(true)}
        onBlur={() => {
          setAberto(false)
          setAtivo(-1)
        }}
        disabled={disabled}
        isInvalid={invalid}
        aria-invalid={invalid}
        aria-autocomplete="list"
        aria-expanded={listaVisivel}
        aria-controls={idLista}
        aria-activedescendant={listaVisivel && ativo >= 0 ? `${id}-opcao-${ativo}` : undefined}
        aria-describedby={[idStatus, descritoPor].filter(Boolean).join(' ')}
      />
      <ul
        id={idLista}
        role="listbox"
        aria-label={`Sugestões de ${label.toLowerCase()}`}
        hidden={!listaVisivel}
        className="list-group position-absolute w-100 shadow mn-sugestoes"
      >
        {sugestoes.map((cliente, i) => (
          <li
            key={cliente.id}
            id={`${id}-opcao-${i}`}
            role="option"
            aria-selected={i === ativo}
            className={`list-group-item list-group-item-action mn-opcao-sugestao${i === ativo ? ' active' : ''}`}
            // mousedown, e não click: o campo não perde o foco antes da seleção.
            onMouseDown={(e) => {
              e.preventDefault()
              selecionar(cliente)
            }}
          >
            <span className="d-block fw-semibold">{cliente.nome}</span>
            <span className="d-block small">
              {formatarDocumento(cliente.tipoPessoa, cliente.documento)}
            </span>
          </li>
        ))}
      </ul>
      <div id={idStatus} role="status" aria-live="polite" className="small mt-1">
        {status}
      </div>
      {busca.isError && textoValido && (
        <Button
          variant="outline-dark"
          className="mt-2"
          onClick={() => {
            setAberto(true)
            busca.refetch()
          }}
        >
          Tentar novamente
        </Button>
      )}
    </Form.Group>
  )
}

function mensagemDeStatus({ aberto, textoValido, digitando, busca, sugestoes }) {
  if (textoValido && busca.isError && !busca.isFetching) return 'Erro ao buscar.'
  if (!aberto) return ''
  if (!textoValido) return `Digite ao menos ${MINIMO_DE_CARACTERES} caracteres para buscar.`
  if (digitando || busca.isFetching) return 'Buscando…'
  if (sugestoes.length === 0) return 'Nenhum cliente encontrado.'
  return sugestoes.length === 1
    ? '1 cliente encontrado. Use as setas para escolher.'
    : `${sugestoes.length} clientes encontrados. Use as setas para escolher.`
}
