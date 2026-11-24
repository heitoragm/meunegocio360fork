import Container from 'react-bootstrap/Container'
import { Link } from 'react-router'

/** Perfil sem permissão para a rota (RF-FE-002): não revela dados, só orienta. */
export function AcessoNegado() {
  return (
    <Container className="py-5">
      <title>Acesso negado · MeuNegócio360</title>
      <h1>Acesso negado</h1>
      <p>Seu perfil não tem permissão para acessar esta página.</p>
      <Link to="/">Ir para a página inicial</Link>
    </Container>
  )
}
