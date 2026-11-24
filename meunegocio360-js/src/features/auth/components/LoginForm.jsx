import { zodResolver } from '@hookform/resolvers/zod'
import { useCallback, useEffect, useRef, useState } from 'react'
import Alert from 'react-bootstrap/Alert'
import Button from 'react-bootstrap/Button'
import Form from 'react-bootstrap/Form'
import Spinner from 'react-bootstrap/Spinner'
import { useForm } from 'react-hook-form'
import { ApiError } from '../../../shared/lib/http'
import { aplicarErrosDeCampo } from '../../../shared/lib/mensagens'
import { useLogin } from '../hooks/useLogin'
import { loginSchema } from '../schemas'
import { AvisoDeBloqueio } from './AvisoDeBloqueio'

const MENSAGEM_CREDENCIAIS = 'E-mail ou senha inválidos.'
const MENSAGEM_FALHA = 'Não foi possível entrar agora. Verifique sua conexão e tente novamente.'
const CAMPOS = ['email', 'senha']

/** Formulário de login: React Hook Form + Zod e marcação de validação do Bootstrap (RNF-FE-20). */
export function LoginForm() {
  const login = useLogin()
  const [erro, setErro] = useState(null)
  const alertaRef = useRef(null)
  const {
    register,
    handleSubmit,
    setError,
    setFocus,
    resetField,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: { email: '', senha: '' },
    mode: 'onSubmit',
    reValidateMode: 'onChange',
    shouldFocusError: true,
  })

  // Erros gerais (rede, 5xx) levam o foco à mensagem, para leitores de tela (RNF-FE-22).
  useEffect(() => {
    if (erro?.tipo === 'falha') {
      alertaRef.current?.focus()
    }
  }, [erro])

  const tratarErro = (falha) => {
    const codigo = falha instanceof ApiError ? falha.code : null

    if (codigo === 'INVALID_CREDENTIALS') {
      setErro({ tipo: 'credenciais' })
      resetField('senha')
      setFocus('senha')
      return
    }
    if (codigo === 'ACCOUNT_TEMPORARILY_LOCKED') {
      const segundos = falha.retryAfterSegundos ?? 15 * 60
      setErro((anterior) => ({ tipo: 'bloqueio', segundos, id: (anterior?.id ?? 0) + 1 }))
      return
    }
    if (codigo === 'VALIDATION_ERROR' && falha.errors.length > 0) {
      const marcados = aplicarErrosDeCampo(falha.errors, CAMPOS, setError)
      if (marcados.length > 0) {
        setErro(null)
        setFocus(marcados[0])
        return
      }
    }
    setErro({ tipo: 'falha' })
  }

  const enviar = (dados) => {
    setErro(null)
    login.mutate(dados, {
      onSuccess: () => reset(),
      onError: tratarErro,
    })
  }

  const encerrarBloqueio = useCallback(() => setErro(null), [])
  const bloqueado = erro?.tipo === 'bloqueio'
  const enviando = login.isPending

  return (
    <Form noValidate onSubmit={handleSubmit(enviar)}>
      {erro?.tipo === 'credenciais' && (
        <Alert variant="danger" className="mb-4">
          {MENSAGEM_CREDENCIAIS}
        </Alert>
      )}
      {bloqueado && (
        <AvisoDeBloqueio key={erro.id} segundos={erro.segundos} aoTerminar={encerrarBloqueio} />
      )}
      {erro?.tipo === 'falha' && (
        <div ref={alertaRef} tabIndex={-1}>
          <Alert variant="danger" className="mb-4">
            <p className="mb-3">{MENSAGEM_FALHA}</p>
            <Button type="submit" variant="outline-dark" disabled={enviando}>
              Tentar novamente
            </Button>
          </Alert>
        </div>
      )}

      <Form.Group className="mb-3" controlId="login-email">
        <Form.Label>E-mail</Form.Label>
        <Form.Control
          type="email"
          autoComplete="username"
          inputMode="email"
          isInvalid={Boolean(errors.email)}
          aria-invalid={Boolean(errors.email)}
          aria-describedby={errors.email ? 'login-email-erro' : undefined}
          {...register('email')}
        />
        <Form.Control.Feedback type="invalid" id="login-email-erro">
          {errors.email?.message}
        </Form.Control.Feedback>
      </Form.Group>

      <Form.Group className="mb-4" controlId="login-senha">
        <Form.Label>Senha</Form.Label>
        <Form.Control
          type="password"
          autoComplete="current-password"
          isInvalid={Boolean(errors.senha)}
          aria-invalid={Boolean(errors.senha)}
          aria-describedby={errors.senha ? 'login-senha-erro' : undefined}
          {...register('senha')}
        />
        <Form.Control.Feedback type="invalid" id="login-senha-erro">
          {errors.senha?.message}
        </Form.Control.Feedback>
      </Form.Group>

      <Button type="submit" variant="primary" className="w-100" disabled={enviando || bloqueado}>
        {enviando ? (
          <>
            <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
            Entrando…
          </>
        ) : (
          'Entrar'
        )}
      </Button>
    </Form>
  )
}
