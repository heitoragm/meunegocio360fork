import { useEffect, useRef } from 'react'
import Alert from 'react-bootstrap/Alert'
import Button from 'react-bootstrap/Button'

/**
 * Alerta de erro anunciado a leitores de tela (role="alert") que recebe o foco ao aparecer
 * (RNF-FE-22). A ação opcional cobre "Tentar novamente" e "Recarregar dados" (RNF-FE-29).
 */
export function ErrorAlert({
  children,
  acao,
  aoAcionar,
  acaoDesabilitada = false,
  className = 'mb-4',
}) {
  const ref = useRef(null)

  useEffect(() => {
    ref.current?.focus()
  }, [children])

  return (
    <div ref={ref} tabIndex={-1} className={className}>
      <Alert variant="danger" className="mb-0">
        <p className={acao ? 'mb-3' : 'mb-0'}>{children}</p>
        {acao && (
          <Button
            type="button"
            variant="outline-dark"
            onClick={aoAcionar}
            disabled={acaoDesabilitada}
          >
            {acao}
          </Button>
        )}
      </Alert>
    </div>
  )
}
