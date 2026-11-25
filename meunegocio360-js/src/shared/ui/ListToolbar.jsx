import { useEffect, useId, useState } from 'react'
import Col from 'react-bootstrap/Col'
import Form from 'react-bootstrap/Form'
import Row from 'react-bootstrap/Row'
import { useDebounce } from '../hooks/useDebounce'

/**
 * Barra de uma listagem (RF-FE-003, RNF-FE-27): campo de busca com debounce de 400 ms, um filtro de
 * seleção e o espaço da ação principal (`children`). A URL é a fonte da verdade: o texto digitado
 * só vai para ela quando a digitação para, e volta a ela quando a URL muda por fora (ex.: "Limpar
 * filtros" ou voltar no navegador).
 */
export function ListToolbar({ rotuloBusca, busca, aoBuscar, filtro, children }) {
  const id = useId()
  const [texto, setTexto] = useState(busca)
  const [buscaVista, setBuscaVista] = useState(busca)
  const textoAtrasado = useDebounce(texto)

  // A URL mudou por fora: o campo acompanha (ajuste de estado durante a renderização).
  if (busca !== buscaVista) {
    setBuscaVista(busca)
    if (texto.trim() !== busca) {
      setTexto(busca)
    }
  }

  useEffect(() => {
    // Só envia quando a digitação assentou, para não reenviar um texto antigo após "Limpar".
    const termo = textoAtrasado.trim()
    if (textoAtrasado === texto && termo !== busca) {
      aoBuscar(termo)
    }
  }, [textoAtrasado, texto, busca, aoBuscar])

  return (
    <Form role="search" onSubmit={(e) => e.preventDefault()} className="mb-3">
      <Row className="g-3 align-items-end">
        <Col xs={12} md>
          <Form.Group controlId={`${id}-busca`}>
            <Form.Label>{rotuloBusca}</Form.Label>
            <Form.Control
              type="search"
              autoComplete="off"
              maxLength={150}
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
            />
          </Form.Group>
        </Col>
        {filtro && (
          <Col xs={12} sm={6} md={3}>
            <Form.Group controlId={`${id}-filtro`}>
              <Form.Label>{filtro.rotulo}</Form.Label>
              <Form.Select value={filtro.valor} onChange={(e) => filtro.aoAlterar(e.target.value)}>
                {filtro.opcoes.map((opcao) => (
                  <option key={opcao.valor} value={opcao.valor}>
                    {opcao.rotulo}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Col>
        )}
        {children && (
          <Col xs={12} sm={6} md="auto" className="d-grid">
            {children}
          </Col>
        )}
      </Row>
    </Form>
  )
}
