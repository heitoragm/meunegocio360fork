import { useState } from 'react'
import Card from 'react-bootstrap/Card'
import Container from 'react-bootstrap/Container'
import { Navigate, useNavigate } from 'react-router'
import { TelaDeErro } from '../../../shared/ui/TelaDeErro'
import { EmpresaForm } from '../components/EmpresaForm'
import { EmpresasInativas } from '../components/EmpresasInativas'
import { ESTADO_EMPRESA } from '../empresaContext'
import { useEmpresa } from '../hooks/useEmpresa'
import { useCadastrarEmpresa } from '../hooks/useMutacoesDaEmpresa'

/**
 * Primeiro cadastro da empresa (RF-FE-002, rota /empresa/cadastro, somente ADMIN pela RequireRole).
 * Também oferece reativar uma empresa inativa (GET /empresa/inativas), mesmo após recarregar a
 * página. Com empresa ativa, redireciona para /empresa.
 */
export default function CadastroEmpresaPage() {
  const { estado, recarregar } = useEmpresa()
  const navigate = useNavigate()
  const cadastrar = useCadastrarEmpresa()
  // Depois de cadastrar ou reativar, a navegação é feita pela própria ação (com a mensagem).
  const [concluido, setConcluido] = useState(false)

  if (estado === ESTADO_EMPRESA.ATIVA && !cadastrar.isSuccess && !concluido) {
    return <Navigate to="/empresa" replace />
  }
  if (estado === ESTADO_EMPRESA.ERRO) {
    return (
      <TelaDeErro
        mensagem="Não foi possível carregar os dados da empresa. Verifique sua conexão e tente novamente."
        aoTentarNovamente={recarregar}
      />
    )
  }

  const enviar = async (dados) => {
    await cadastrar.mutateAsync(dados)
    navigate('/', { replace: true, state: { aviso: 'Empresa cadastrada com sucesso.' } })
  }

  const aoReativar = () => {
    setConcluido(true)
    navigate('/empresa', { replace: true, state: { aviso: 'Empresa reativada.' } })
  }

  return (
    <Container className="py-5 mn-conteudo-medio">
      <title>Cadastre sua empresa · MeuNegócio360</title>
      <h1>Cadastre sua empresa</h1>
      <p className="text-body-secondary mb-4">
        Os dados da empresa são usados em todos os módulos do sistema. Campos sem “(opcional)” são
        obrigatórios.
      </p>

      <EmpresasInativas aoReativar={aoReativar} />

      <Card>
        <Card.Body className="p-4">
          <EmpresaForm textoEnviar="Cadastrar empresa" aoEnviar={enviar} />
        </Card.Body>
      </Card>
    </Container>
  )
}
