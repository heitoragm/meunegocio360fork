import { zodResolver } from '@hookform/resolvers/zod'
import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Col from 'react-bootstrap/Col'
import Form from 'react-bootstrap/Form'
import Row from 'react-bootstrap/Row'
import Spinner from 'react-bootstrap/Spinner'
import { Controller, useForm } from 'react-hook-form'
import { formatarCnpj } from '../../../shared/lib/cnpj'
import { aplicarErrosDeCampo } from '../../../shared/lib/mensagens'
import { formatarTelefone } from '../../../shared/lib/telefone'
import { ErrorAlert } from '../../../shared/ui/ErrorAlert'
import {
  MENSAGEM_CNPJ_DUPLICADO,
  MENSAGEM_CONFLITO_DE_VERSAO,
  MENSAGEM_EMPRESA_JA_ATIVA,
  MENSAGEM_FALHA,
  MENSAGEM_SEM_PERMISSAO,
  classificarErro,
} from '../mensagensDeErro'
import { CAMPOS_DA_EMPRESA, FUSOS_HORARIOS, MOEDA, empresaSchema } from '../schemas'
import { VALORES_VAZIOS } from '../valoresDoFormulario'

/**
 * Formulário da empresa, usado no primeiro cadastro e na edição (RF-FE-002). React Hook Form +
 * Zod com a marcação do Bootstrap (RNF-FE-20); CNPJ e telefone com máscara na tela e só dígitos
 * no envio (RNF-FE-28). Erros da API são tratados pelo `code` (RNF-FE-14, RNF-FE-29).
 */
export function EmpresaForm({ valoresIniciais, textoEnviar, aoEnviar, aoCancelar, aoRecarregar }) {
  const [erro, setErro] = useState(null)
  const {
    register,
    control,
    handleSubmit,
    setError,
    setFocus,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(empresaSchema),
    defaultValues: valoresIniciais ?? VALORES_VAZIOS,
    mode: 'onSubmit',
    reValidateMode: 'onChange',
    shouldFocusError: true,
  })

  const tratarErro = (falha) => {
    switch (classificarErro(falha)) {
      case 'validacao': {
        const marcados = aplicarErrosDeCampo(falha.errors, CAMPOS_DA_EMPRESA, setError)
        if (marcados.length > 0) {
          setFocus(marcados[0])
          return
        }
        setErro({ mensagem: MENSAGEM_FALHA, tentarNovamente: true })
        return
      }
      case 'cnpj-duplicado':
        setError('cnpj', { type: 'server', message: MENSAGEM_CNPJ_DUPLICADO })
        setFocus('cnpj')
        return
      case 'ja-ativa':
        setErro({ mensagem: MENSAGEM_EMPRESA_JA_ATIVA })
        return
      case 'conflito':
        setErro({ mensagem: MENSAGEM_CONFLITO_DE_VERSAO, recarregar: true })
        return
      case 'sem-permissao':
        setErro({ mensagem: MENSAGEM_SEM_PERMISSAO })
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

  return (
    <Form noValidate onSubmit={reenviar}>
      {erro && (
        <ErrorAlert {...acaoDoErro} acaoDesabilitada={isSubmitting}>
          {erro.mensagem}
        </ErrorAlert>
      )}

      <Campo id="razaoSocial" rotulo="Razão social" erro={errors.razaoSocial}>
        {(props) => (
          <Form.Control type="text" autoComplete="organization" {...props} {...register('razaoSocial')} />
        )}
      </Campo>

      <Campo id="nomeFantasia" rotulo="Nome fantasia (opcional)" erro={errors.nomeFantasia}>
        {(props) => <Form.Control type="text" {...props} {...register('nomeFantasia')} />}
      </Campo>

      <Row>
        <Col md={6}>
          <Campo id="cnpj" rotulo="CNPJ" erro={errors.cnpj} ajuda="Formato 00.000.000/0000-00.">
            {(props) => (
              <Controller
                name="cnpj"
                control={control}
                render={({ field }) => (
                  <Form.Control
                    type="text"
                    inputMode="numeric"
                    autoComplete="off"
                    {...props}
                    {...field}
                    onChange={(e) => field.onChange(formatarCnpj(e.target.value))}
                  />
                )}
              />
            )}
          </Campo>
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

      <Row>
        <Col md={8}>
          <Campo id="fusoHorario" rotulo="Fuso horário" erro={errors.fusoHorario}>
            {(props) => (
              <Form.Select {...props} {...register('fusoHorario')}>
                {FUSOS_HORARIOS.map((fuso) => (
                  <option key={fuso.id} value={fuso.id}>
                    {fuso.nome}
                  </option>
                ))}
              </Form.Select>
            )}
          </Campo>
        </Col>
        <Col md={4}>
          <Form.Group className="mb-3" controlId="empresa-moeda">
            <Form.Label>Moeda</Form.Label>
            <Form.Select value={MOEDA} disabled aria-describedby="empresa-moeda-ajuda">
              <option value={MOEDA}>Real (BRL)</option>
            </Form.Select>
            <Form.Text id="empresa-moeda-ajuda">Nesta versão, somente BRL.</Form.Text>
          </Form.Group>
        </Col>
      </Row>

      <div className="d-flex flex-wrap gap-2 mt-2">
        <Button type="submit" variant="primary" disabled={isSubmitting}>
          {isSubmitting && (
            <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
          )}
          {isSubmitting ? 'Salvando…' : textoEnviar}
        </Button>
        {aoCancelar && (
          <Button type="button" variant="outline-dark" onClick={aoCancelar} disabled={isSubmitting}>
            Cancelar
          </Button>
        )}
      </div>
    </Form>
  )
}

/** Rótulo visível, mensagem de erro ligada por aria-describedby e aria-invalid (RNF-FE-21). */
function Campo({ id, rotulo, erro, ajuda, children }) {
  const idControle = `empresa-${id}`
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
