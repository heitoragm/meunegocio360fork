import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Col from 'react-bootstrap/Col'
import Form from 'react-bootstrap/Form'
import Row from 'react-bootstrap/Row'
import Spinner from 'react-bootstrap/Spinner'
import { Controller, useForm, useWatch } from 'react-hook-form'
import { Link } from 'react-router'
import { TIPO_PESSOA, formatarDocumento } from '../../../shared/lib/documento'
import { aplicarErrosDeCampo } from '../../../shared/lib/mensagens'
import { formatarTelefone } from '../../../shared/lib/telefone'
import { DocumentoInput } from '../../../shared/ui/DocumentoInput'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import {
  ERRO,
  MENSAGEM_CONFLITO_DE_VERSAO,
  MENSAGEM_DOCUMENTO_DUPLICADO,
  MENSAGEM_EMPRESA_INATIVA,
  MENSAGEM_FALHA,
  classificarErro,
} from '../mensagensDeErro'
import {
  CAMPOS_DO_CLIENTE,
  ROTULO_TIPO_PESSOA,
  VALORES_VAZIOS,
  clienteSchema,
  documentoIncompativel,
} from '../schemas'

const AVISO_DOCUMENTO_APAGADO =
  'O documento foi apagado porque não corresponde ao tipo de pessoa escolhido. Informe-o novamente.'

/**
 * Formulário do cliente, usado no cadastro e na edição (RF-FE-003). React Hook Form + Zod com a
 * marcação do Bootstrap (RNF-FE-20); documento e telefone com máscara na tela e só dígitos no
 * envio (RNF-FE-28). Os erros da API são tratados pelo `code` (RNF-FE-14, RNF-FE-29), sem perder
 * o que foi digitado.
 */
export function ClienteForm({
  valoresIniciais,
  textoEnviar,
  aoEnviar,
  aoCancelar,
  aoRecarregar,
  aoNegarAcesso,
}) {
  const [erro, setErro] = useState(null)
  const [avisoDocumento, setAvisoDocumento] = useState('')
  const {
    register,
    control,
    handleSubmit,
    setError,
    setFocus,
    setValue,
    getValues,
    trigger,
    formState: { errors, isSubmitting, isSubmitted },
  } = useForm({
    resolver: zodResolver(clienteSchema),
    defaultValues: valoresIniciais ?? VALORES_VAZIOS,
    mode: 'onSubmit',
    reValidateMode: 'onChange',
    shouldFocusError: true,
  })
  const tipoPessoa = useWatch({ control, name: 'tipoPessoa' })

  // Ao trocar o tipo, o documento é remascarado e revalidado; se o tamanho não couber, é apagado
  // com aviso anunciado (RF-FE-003, "Formulário").
  const aoTrocarTipo = (e) => {
    const novoTipo = e.target.value
    const documento = getValues('documento')
    if (documentoIncompativel(novoTipo, documento)) {
      setValue('documento', '')
      setAvisoDocumento(AVISO_DOCUMENTO_APAGADO)
    } else {
      setValue('documento', formatarDocumento(novoTipo, documento))
      setAvisoDocumento('')
    }
    if (isSubmitted || getValues('documento') !== '') {
      trigger('documento')
    }
  }

  const tratarErro = (falha) => {
    switch (classificarErro(falha)) {
      case ERRO.VALIDACAO: {
        const marcados = aplicarErrosDeCampo(falha.errors, CAMPOS_DO_CLIENTE, setError)
        if (marcados.length > 0) {
          setFocus(marcados[0])
          return
        }
        setErro({ mensagem: MENSAGEM_FALHA, tentarNovamente: true })
        return
      }
      case ERRO.DOCUMENTO_DUPLICADO:
        setError('documento', { type: 'server', message: MENSAGEM_DOCUMENTO_DUPLICADO })
        setFocus('documento')
        return
      case ERRO.CONFLITO:
        setErro({ mensagem: MENSAGEM_CONFLITO_DE_VERSAO, recarregar: true })
        return
      case ERRO.EMPRESA_INATIVA:
        setErro({ mensagem: MENSAGEM_EMPRESA_INATIVA, linkEmpresa: true })
        return
      case ERRO.SEM_PERMISSAO:
        aoNegarAcesso()
        return
      default:
        setErro({ mensagem: MENSAGEM_FALHA, tentarNovamente: true })
    }
  }

  const enviar = async (dados) => {
    setErro(null)
    try {
      await aoEnviar(dados)
    } catch (falha) {
      tratarErro(falha)
    }
  }

  const reenviar = handleSubmit(enviar)
  const acaoDoErro = erro?.recarregar
    ? { acao: 'Recarregar dados', aoAcionar: aoRecarregar }
    : erro?.tentarNovamente
      ? { acao: 'Tentar novamente', aoAcionar: reenviar }
      : {}
  const erroTipo = errors.tipoPessoa

  return (
    <Form noValidate onSubmit={reenviar}>
      {erro && (
        <ErrorAlert {...acaoDoErro} acaoDesabilitada={isSubmitting}>
          {erro.mensagem}
          {erro.linkEmpresa && (
            <>
              {' '}
              <Link to="/empresa">Ir para a empresa</Link>
            </>
          )}
        </ErrorAlert>
      )}

      <fieldset className="mb-3">
        <legend className="form-label fs-6 mb-2">Tipo de pessoa</legend>
        <div className="d-flex flex-wrap column-gap-4">
          {Object.values(TIPO_PESSOA).map((tipo) => (
            <Form.Check
              key={tipo}
              type="radio"
              id={`cliente-tipoPessoa-${tipo}`}
              label={ROTULO_TIPO_PESSOA[tipo]}
              value={tipo}
              isInvalid={Boolean(erroTipo)}
              aria-describedby={erroTipo ? 'cliente-tipoPessoa-erro' : undefined}
              className="mn-opcao"
              {...register('tipoPessoa', { onChange: aoTrocarTipo })}
            />
          ))}
        </div>
        {erroTipo && (
          <div className="invalid-feedback d-block" id="cliente-tipoPessoa-erro">
            {erroTipo.message}
          </div>
        )}
      </fieldset>

      <Campo id="nome" rotulo="Nome ou razão social" erro={errors.nome}>
        {(props) => (
          <Form.Control type="text" autoComplete="name" {...props} {...register('nome')} />
        )}
      </Campo>

      <Row>
        <Col md={6}>
          <Campo
            id="documento"
            rotulo="CPF ou CNPJ"
            erro={errors.documento}
            ajuda={
              tipoPessoa === TIPO_PESSOA.JURIDICA
                ? 'CNPJ no formato 00.000.000/0000-00.'
                : tipoPessoa === TIPO_PESSOA.FISICA
                  ? 'CPF no formato 000.000.000-00.'
                  : 'Escolha o tipo de pessoa para a máscara de CPF ou CNPJ.'
            }
          >
            {(props) => (
              <Controller
                name="documento"
                control={control}
                render={({ field }) => <DocumentoInput tipo={tipoPessoa} {...props} {...field} />}
              />
            )}
          </Campo>
          <div aria-live="polite" className="small">
            {avisoDocumento}
          </div>
        </Col>
        <Col md={6}>
          <Campo id="telefone" rotulo="Telefone (opcional)" erro={errors.telefone}>
            {(props) => (
              <Controller
                name="telefone"
                control={control}
                render={({ field }) => (
                  <Form.Control
                    type="tel"
                    autoComplete="tel"
                    inputMode="tel"
                    {...props}
                    {...field}
                    onChange={(e) => field.onChange(formatarTelefone(e.target.value))}
                  />
                )}
              />
            )}
          </Campo>
        </Col>
      </Row>

      <Campo id="email" rotulo="E-mail (opcional)" erro={errors.email}>
        {(props) => (
          <Form.Control type="email" autoComplete="email" {...props} {...register('email')} />
        )}
      </Campo>

      <div className="d-flex flex-wrap gap-2 mt-2">
        <Button type="submit" variant="primary" disabled={isSubmitting}>
          {isSubmitting && (
            <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
          )}
          {isSubmitting ? 'Salvando…' : textoEnviar}
        </Button>
        <Button type="button" variant="outline-dark" onClick={aoCancelar} disabled={isSubmitting}>
          Cancelar
        </Button>
      </div>
    </Form>
  )
}

/** Rótulo visível, mensagem de erro ligada por aria-describedby e aria-invalid (RNF-FE-21). */
function Campo({ id, rotulo, erro, ajuda, children }) {
  const idControle = `cliente-${id}`
  const idErro = `${idControle}-erro`
  const idAjuda = `${idControle}-ajuda`
  const descricao = [ajuda && idAjuda, erro && idErro].filter(Boolean).join(' ') || undefined
  return (
    <Form.Group className="mb-3" controlId={idControle}>
      <Form.Label>{rotulo}</Form.Label>
      {children({
        isInvalid: Boolean(erro),
        'aria-invalid': Boolean(erro),
        'aria-describedby': descricao,
      })}
      {ajuda && <Form.Text id={idAjuda}>{ajuda}</Form.Text>}
      <Form.Control.Feedback type="invalid" id={idErro}>
        {erro?.message}
      </Form.Control.Feedback>
    </Form.Group>
  )
}
