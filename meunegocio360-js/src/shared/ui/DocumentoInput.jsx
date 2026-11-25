import Form from 'react-bootstrap/Form'
import { TIPO_PESSOA, formatarDocumento } from '../lib/documento'

const DICA = {
  [TIPO_PESSOA.FISICA]: '000.000.000-00',
  [TIPO_PESSOA.JURIDICA]: '00.000.000/0000-00',
}

/**
 * Campo de CPF ou CNPJ com a máscara do tipo de pessoa (RF-FE-003). Digitado ou colado, o valor
 * fica sempre mascarado na tela; o envio só com dígitos é feito pelo schema (RNF-FE-28).
 */
export function DocumentoInput({ tipo, value, onChange, ...props }) {
  return (
    <Form.Control
      type="text"
      inputMode="numeric"
      autoComplete="off"
      placeholder={DICA[tipo]}
      {...props}
      value={value ?? ''}
      onChange={(e) => onChange(formatarDocumento(tipo, e.target.value))}
    />
  )
}
