package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** PATCH /itens-catalogo/{id}/situacao: inativa ou reativa; idempotente (RN-10). */
@Service
public class AlterarSituacaoDoItem {

  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public AlterarSituacaoDoItem(CatalogoDaEmpresa catalogo, Clock clock) {
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDoItem executar(UUID id, boolean ativo, long versao) {
    ItemCatalogo item = catalogo.itemParaEscrita(id);
    if (!item.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (!item.definirAtivo(ativo, clock.instant())) {
      return FichaDoItem.de(item);
    }
    return FichaDoItem.de(catalogo.gravar(item));
  }
}
