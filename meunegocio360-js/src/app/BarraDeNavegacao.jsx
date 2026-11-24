import { useState } from 'react'
import Button from 'react-bootstrap/Button'
import Container from 'react-bootstrap/Container'
import Nav from 'react-bootstrap/Nav'
import Navbar from 'react-bootstrap/Navbar'
import Spinner from 'react-bootstrap/Spinner'
import { Link, NavLink } from 'react-router'
import icone from '../shared/assets/icone-meunegocio360-branco.png'
import { podeAcessar } from '../shared/lib/perfis'
import { useAuth } from '../features/auth'
import { ESTADO_EMPRESA, useEmpresa } from '../features/empresa'
import { ITENS_DO_MENU } from './menu'

/**
 * Barra de navegação da aplicação (RF-FE-002), laranja com texto Preto (RNF-FE, seção 2.2).
 * - Telas grandes: marca à esquerda dos itens do menu.
 * - Celular: hambúrguer | marca ao centro | pesquisar (desabilitado até existir busca).
 * O menu mostra só o que o perfil pode usar (RNF-FE-16) e o nome da empresa, o usuário e Sair.
 */
export function BarraDeNavegacao() {
  const { usuario, sair } = useAuth()
  const { estado, nomeExibicao } = useEmpresa()
  const [saindo, setSaindo] = useState(false)
  const comEmpresa = estado === ESTADO_EMPRESA.ATIVA
  const itens = comEmpresa ? ITENS_DO_MENU.filter((i) => podeAcessar(usuario.perfil, i.perfis)) : []

  const aoSair = async () => {
    setSaindo(true)
    await sair()
  }

  return (
    <header>
      <Navbar
        bg="primary"
        data-bs-theme="light"
        expand="lg"
        collapseOnSelect
        aria-label="Menu principal"
      >
        <Container>
          <Navbar.Toggle
            aria-controls="menu-principal"
            aria-label="Abrir menu de navegação"
            className="order-0"
          />

          <Navbar.Brand as={Link} to="/" className="mn-marca-navbar order-1 mx-auto ms-lg-0 me-lg-4">
            <img src={icone} alt="MeuNegócio360 — página inicial" width={94} height={96} />
          </Navbar.Brand>

          <Button
            variant="link"
            className="mn-botao-icone order-2 d-lg-none"
            disabled
            aria-label="Pesquisar (em breve)"
            title="Pesquisar (em breve)"
          >
            <svg viewBox="0 0 16 16" width="22" height="22" fill="currentColor" aria-hidden="true">
              <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0" />
            </svg>
          </Button>

          <Navbar.Collapse id="menu-principal" className="order-3">
            <Nav className="me-auto">
              {itens.map((item) => (
                <Nav.Link
                  key={item.rota}
                  as={NavLink}
                  to={item.rota}
                  end={item.exato}
                  eventKey={item.rota}
                >
                  {item.rotulo}
                </Nav.Link>
              ))}
            </Nav>
            <div className="d-flex flex-column flex-lg-row align-items-lg-center gap-2 gap-lg-3 py-2 py-lg-0">
              <span className="navbar-text py-0">
                {nomeExibicao && (
                  <>
                    <span className="visually-hidden">Empresa: </span>
                    <span className="fw-semibold">{nomeExibicao}</span>
                    <span aria-hidden="true"> · </span>
                  </>
                )}
                <span className="visually-hidden">Usuário: </span>
                {usuario.nome}
              </span>
              <Button variant="outline-dark" onClick={aoSair} disabled={saindo}>
                {saindo && (
                  <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
                )}
                Sair
              </Button>
            </div>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </header>
  )
}
