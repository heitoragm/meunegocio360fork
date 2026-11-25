import { useState } from 'react'
import Container from 'react-bootstrap/Container'
import { useLocation } from 'react-router'
import { NOME_DO_PERFIL } from '../../../shared/lib/perfis'
import { AvisoDeSucesso } from '../../../shared/ui/AvisoDeSucesso'
import { useAuth } from '../../auth'

/**
 * Página inicial provisória (RF-FE-001), agora dentro do AppLayout (RF-FE-002), que cuida da
 * barra de navegação e do botão Sair. Será substituída pelo dashboard em requisito futuro.
 */
export default function InicioPage() {
  const { usuario } = useAuth()
  const location = useLocation()
  const [aviso, setAviso] = useState(location.state?.aviso ?? null)

  return (
    <Container className="py-5">
      <title>Início · MeuNegócio360</title>
      {aviso && <AvisoDeSucesso aoFechar={() => setAviso(null)}>{aviso}</AvisoDeSucesso>}
      <h1>Olá, {usuario.nome}!</h1>
      <p className="lead text-body-secondary">
        Perfil: {NOME_DO_PERFIL[usuario.perfil] ?? usuario.perfil}
      </p>
    </Container>
  )
}
