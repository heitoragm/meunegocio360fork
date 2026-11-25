import Button from 'react-bootstrap/Button'

/**
 * Navegação entre páginas de uma listagem paginada no servidor (RNF-FE-27, RNF-054). `pagina` começa
 * em 0, como na API; a tela mostra a partir de 1. Some quando há uma página só.
 */
export function Paginacao({ pagina, totalPaginas, aoMudar, desabilitada = false }) {
  if (totalPaginas <= 1) {
    return null
  }
  const primeira = pagina <= 0
  const ultima = pagina >= totalPaginas - 1

  return (
    <nav aria-label="Paginação" className="d-flex flex-wrap align-items-center gap-2 mt-3">
      <Button
        variant="outline-dark"
        onClick={() => aoMudar(pagina - 1)}
        disabled={primeira || desabilitada}
      >
        Anterior
      </Button>
      <span className="px-2">
        Página {Math.min(pagina + 1, totalPaginas)} de {totalPaginas}
      </span>
      <Button
        variant="outline-dark"
        onClick={() => aoMudar(pagina + 1)}
        disabled={ultima || desabilitada}
      >
        Próxima
      </Button>
    </nav>
  )
}
