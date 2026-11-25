import Button from 'react-bootstrap/Button'
import Modal from 'react-bootstrap/Modal'
import Spinner from 'react-bootstrap/Spinner'

/**
 * Confirmação de ação com consequência (RF-FE-002). O Modal do react-bootstrap prende o foco
 * enquanto aberto e o devolve ao botão de origem ao fechar (RNF-FE-09, RNF-FE-23).
 */
export function ConfirmModal({
  aberto,
  titulo,
  children,
  textoConfirmar,
  varianteConfirmar = 'danger',
  confirmando = false,
  aoConfirmar,
  aoCancelar,
}) {
  return (
    <Modal
      show={aberto}
      onHide={confirmando ? undefined : aoCancelar}
      backdrop={confirmando ? 'static' : true}
      centered
      aria-labelledby="confirm-modal-titulo"
    >
      <Modal.Header closeButton={!confirmando} closeLabel="Fechar">
        <Modal.Title id="confirm-modal-titulo" as="h2" className="h4">
          {titulo}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>{children}</Modal.Body>
      <Modal.Footer>
        <Button variant="outline-dark" onClick={aoCancelar} disabled={confirmando}>
          Cancelar
        </Button>
        <Button variant={varianteConfirmar} onClick={aoConfirmar} disabled={confirmando}>
          {confirmando && (
            <Spinner as="span" animation="border" size="sm" aria-hidden="true" className="me-2" />
          )}
          {textoConfirmar}
        </Button>
      </Modal.Footer>
    </Modal>
  )
}
