package br.com.meunegocio360.core.shared.web;

/** Filtro {@code situacao} das listagens de cadastros mestres: por padrão, só os ativos. */
public enum SituacaoDoFiltro {
  ATIVO(true),
  INATIVO(false),
  TODOS(null);

  private final Boolean ativo;

  SituacaoDoFiltro(Boolean ativo) {
    this.ativo = ativo;
  }

  /** {@code null} quando não filtra pela situação. */
  public Boolean ativo() {
    return ativo;
  }
}
