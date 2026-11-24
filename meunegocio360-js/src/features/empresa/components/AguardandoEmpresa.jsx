import Container from 'react-bootstrap/Container'

/** Operador sem empresa ativa: sem acesso às telas de negócio (RF-FE-002). */
export function AguardandoEmpresa() {
  return (
    <Container className="py-5 mn-conteudo-estreito">
      <title>Aguardando o cadastro da empresa · MeuNegócio360</title>
      <h1>Aguardando o cadastro da empresa</h1>
      <p>
        O sistema ainda não tem uma empresa ativa. Fale com o administrador para que ele cadastre ou
        reative a empresa; depois disso, entre novamente.
      </p>
    </Container>
  )
}
