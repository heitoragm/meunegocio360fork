import { useEffect, useState } from 'react'

export const ATRASO_DA_BUSCA_MS = 400

/** Valor atrasado: só muda depois de `atrasoMs` sem alterações (busca com debounce, RNF-FE-27). */
export function useDebounce(valor, atrasoMs = ATRASO_DA_BUSCA_MS) {
  const [atrasado, setAtrasado] = useState(valor)

  useEffect(() => {
    const temporizador = setTimeout(() => setAtrasado(valor), atrasoMs)
    return () => clearTimeout(temporizador)
  }, [valor, atrasoMs])

  return atrasado
}
