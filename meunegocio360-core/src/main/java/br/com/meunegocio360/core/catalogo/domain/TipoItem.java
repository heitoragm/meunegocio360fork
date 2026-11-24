package br.com.meunegocio360.core.catalogo.domain;

/**
 * RN-02: produtos e serviços coexistem no mesmo catálogo, distinguidos pelo tipo, que não muda
 * depois da criação. Consultas de estoque usam {@code PRODUTO} por igualdade (RN-08).
 */
public enum TipoItem {
  PRODUTO("UN"),
  SERVICO("H");

  private final String unidadePadrao;

  TipoItem(String unidadePadrao) {
    this.unidadePadrao = unidadePadrao;
  }

  /** RN-04: {@code UN} para produto e {@code H} para serviço. */
  public String unidadePadrao() {
    return unidadePadrao;
  }
}
