import { useState } from 'react'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import { useNavigate } from 'react-router'
import { AcessoNegado } from '../../../shared/ui/AcessoNegado'
import { ClienteForm } from '../components/ClienteForm'
import { useCadastrarCliente } from '../hooks/useMutacoesDoCliente'

/** Cadastro de cliente (RF-FE-003, rota /clientes/novo). */
export default function NovoClientePage() {
  const navigate = useNavigate()
  const cadastrar = useCadastrarCliente()
  const [acessoNegado, setAcessoNegado] = useState(false)

  if (acessoNegado) {
    return <AcessoNegado />
  }

  const salvar = async (dados) => {
    await cadastrar.mutateAsync(dados)
    navigate('/clientes', { state: { aviso: 'Cliente cadastrado com sucesso.' } })
  }

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>Novo cliente · MeuNegócio360</title>
      <h1>Novo cliente</h1>
      <Card className="mt-3">
        <Card.Body className="p-4">
          <ClienteForm
            textoEnviar="Cadastrar cliente"
            aoEnviar={salvar}
            aoCancelar={() => navigate('/clientes')}
            aoNegarAcesso={() => setAcessoNegado(true)}
          />
        </Card.Body>
      </Card>
    </Container>
  )
}
