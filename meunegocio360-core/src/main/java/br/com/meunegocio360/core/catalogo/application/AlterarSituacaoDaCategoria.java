package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PATCH /categorias/{id}/situacao: inativa ou reativa; idempotente. Inativar NÃO altera os itens
 * que usam a categoria (RN-05).
 */
@Service
public class AlterarSituacaoDaCategoria {

  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public AlterarSituacaoDaCategoria(CatalogoDaEmpresa catalogo, Clock clock) {
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDaCategoria executar(UUID id, boolean ativo, long versao) {
    Categoria categoria = catalogo.categoriaParaEscrita(id);
    if (!categoria.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (!categoria.definirAtivo(ativo, clock.instant())) {
      return FichaDaCategoria.de(categoria);
    }
    return FichaDaCategoria.de(catalogo.gravar(categoria));
  }
}
