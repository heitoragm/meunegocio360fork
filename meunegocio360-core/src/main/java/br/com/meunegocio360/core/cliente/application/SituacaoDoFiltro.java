package br.com.meunegocio360.core.cliente.application;

/** Filtro {@code situacao} da listagem (RN-07): por padrão, só os ativos. */
public enum SituacaoDoFiltro {
  ATIVO(true),
  INATIVO(false),
  TODOS(null);

  private final Boolean ativo;

  SituacaoDoFiltro(Boolean ativo) {
    this.ativo = ativo;
  }

  /** {@code null} quando não filtra pela situação. */
  Boolean ativo() {
    return ativo;
  }
}
