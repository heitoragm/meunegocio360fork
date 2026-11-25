import Alert from 'react-bootstrap/Alert'
import Button from 'react-bootstrap/Button'
import Container from 'react-bootstrap/Container'

/** Falha ao carregar algo essencial (sessão, empresa), com "Tentar novamente" (RNF-062). */
export function TelaDeErro({ titulo = 'Não foi possível conectar', mensagem, aoTentarNovamente }) {
  return (
    <div className="flex-grow-1 d-flex align-items-center py-5">
      <title>Erro de conexão · MeuNegócio360</title>
      <Container className="text-center mn-conteudo-estreito">
        <h1 className="h3">{titulo}</h1>
        <Alert variant="danger" className="my-4">
          {mensagem}
        </Alert>
        <Button variant="primary" onClick={aoTentarNovamente}>
          Tentar novamente
        </Button>
      </Container>
    </div>
  )
}
