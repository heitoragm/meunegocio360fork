import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import { formatarDocumento } from '../../../shared/lib/documento'
import { ClienteSelect } from '../components/ClienteSelect'

/**
 * Rota de apoio /dev/cliente-select (RF-FE-003): verifica o ClienteSelect enquanto nenhuma tela o
 * consome. Registrada somente em desenvolvimento (import.meta.env.DEV).
 */
export default function ClienteSelectDevPage() {
  const [cliente, setCliente] = useState(null)

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>ClienteSelect (desenvolvimento) · MeuNegócio360</title>
      <h1>ClienteSelect</h1>
      <p className="text-body-secondary">
        Página de apoio, disponível só em desenvolvimento. Busca somente clientes ativos.
      </p>
      <Card className="mt-3">
        <Card.Body className="p-4">
          <ClienteSelect value={cliente} onChange={setCliente} label="Cliente" />
          <h2 className="h5 mt-4">Selecionado</h2>
          <p aria-live="polite">
            {cliente
              ? `${cliente.nome} — ${formatarDocumento(null, cliente.documento)} (id ${cliente.id})`
              : 'Nenhum cliente selecionado.'}
          </p>
          <Button variant="outline-dark" onClick={() => setCliente(null)} disabled={!cliente}>
            Limpar seleção
          </Button>
        </Card.Body>
      </Card>
    </Container>
  )
}
