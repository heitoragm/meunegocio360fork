/**
 * Selo de situação de um registro (RF-FE-003). O texto diz a situação; a cor só reforça
 * (informação não só por cor, RNF-060). Cores do tema com contraste AA.
 */
export function StatusBadge({ ativo }) {
  return (
    <span className={`badge ${ativo ? 'text-bg-success' : 'text-bg-dark'}`}>
      {ativo ? 'Ativo' : 'Inativo'}
    </span>
  )
}
