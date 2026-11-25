import Alert from 'react-bootstrap/Alert'

/** Confirmação de ação bem-sucedida, anunciada sem interromper o leitor de tela (role="status"). */
export function AvisoDeSucesso({ children, aoFechar }) {
  return (
    <Alert
      variant="success"
      role="status"
      dismissible={Boolean(aoFechar)}
      onClose={aoFechar}
      closeLabel="Fechar aviso"
      className="mb-4"
    >
      {children}
    </Alert>
  )
}
