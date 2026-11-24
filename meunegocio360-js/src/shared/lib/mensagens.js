/** As mensagens de campo do servidor vêm em minúsculas e sem ponto ("deve ser um e-mail válido"). */
export function capitalizar(texto) {
  if (!texto) {
    return texto
  }
  const frase = `${texto.charAt(0).toUpperCase()}${texto.slice(1)}`
  return frase.endsWith('.') ? frase : `${frase}.`
}

/** Liga os erros de campo do servidor (errors[]) ao formulário; retorna os campos marcados. */
export function aplicarErrosDeCampo(erros, camposDoFormulario, setError) {
  const marcados = erros.filter((e) => camposDoFormulario.includes(e.field))
  marcados.forEach((e) => setError(e.field, { type: 'server', message: capitalizar(e.message) }))
  return marcados.map((e) => e.field)
}
