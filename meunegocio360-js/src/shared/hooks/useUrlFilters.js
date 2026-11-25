import { useCallback, useMemo } from 'react'
import { useSearchParams } from 'react-router'

/**
 * Filtros e página de uma listagem guardados na query string (RNF-FE-27): a tela sobrevive ao
 * recarregar e o link pode ser compartilhado. Valores iguais ao padrão não aparecem na URL.
 *
 * - `padroes`: constante do módulo com o valor de cada filtro ausente da URL
 *   (ex.: `{ q: '', situacao: 'ATIVO' }`).
 * - Mudar um filtro volta à primeira página; mudar só a página preserva os filtros.
 */
export function useUrlFilters(padroes) {
  const [parametros, setParametros] = useSearchParams()

  const filtros = useMemo(
    () =>
      Object.fromEntries(
        Object.entries(padroes).map(([nome, padrao]) => [nome, parametros.get(nome) ?? padrao]),
      ),
    [parametros, padroes],
  )

  const pagina = lerPagina(parametros.get('page'))

  const definirFiltros = useCallback(
    (novos, { substituir = false } = {}) =>
      setParametros(
        (atuais) => {
          const proximos = new URLSearchParams(atuais)
          for (const [nome, valor] of Object.entries(novos)) {
            if (valor == null || valor === '' || valor === padroes[nome]) {
              proximos.delete(nome)
            } else {
              proximos.set(nome, valor)
            }
          }
          proximos.delete('page')
          return proximos
        },
        { replace: substituir },
      ),
    [setParametros, padroes],
  )

  const definirPagina = useCallback(
    (novaPagina) =>
      setParametros((atuais) => {
        const proximos = new URLSearchParams(atuais)
        if (novaPagina > 0) {
          proximos.set('page', String(novaPagina))
        } else {
          proximos.delete('page')
        }
        return proximos
      }),
    [setParametros],
  )

  const limpar = useCallback(() => setParametros(new URLSearchParams()), [setParametros])

  const filtrando = Object.entries(filtros).some(([nome, valor]) => valor !== padroes[nome])

  return { filtros, pagina, filtrando, definirFiltros, definirPagina, limpar }
}

function lerPagina(valor) {
  const numero = Number.parseInt(valor ?? '', 10)
  return Number.isFinite(numero) && numero > 0 ? numero : 0
}
