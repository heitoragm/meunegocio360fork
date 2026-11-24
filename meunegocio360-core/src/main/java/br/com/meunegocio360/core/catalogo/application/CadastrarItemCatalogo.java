package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.DadosDoItem;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import java.time.Clock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** POST /itens-catalogo: cadastra na empresa ativa, já ativo (RN-01 a RN-05). */
@Service
public class CadastrarItemCatalogo {

  private static final Logger log = LoggerFactory.getLogger(CadastrarItemCatalogo.class);

  private final ItemCatalogoRepository itens;
  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public CadastrarItemCatalogo(
      ItemCatalogoRepository itens, CatalogoDaEmpresa catalogo, Clock clock) {
    this.itens = itens;
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDoItem executar(TipoItem tipo, DadosDoItem dados, UUID categoriaId) {
    UUID empresaId = catalogo.empresaParaEscrita();
    if (dados.codigo() != null && itens.existsByEmpresaIdAndCodigo(empresaId, dados.codigo())) {
      throw new CodigoDeItemDuplicadoException();
    }
    Categoria categoria = catalogo.categoriaParaAssociar(categoriaId, empresaId);
    ItemCatalogo item =
        catalogo.gravar(ItemCatalogo.novo(empresaId, tipo, dados, categoria, clock.instant()));
    log.info("Item do catálogo cadastrado itemId={} tipo={}", item.getId(), tipo);
    return FichaDoItem.de(item);
  }
}
