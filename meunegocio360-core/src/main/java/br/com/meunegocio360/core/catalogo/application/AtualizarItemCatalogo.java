package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.DadosDoItem;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PUT /itens-catalogo/{id}: edição completa com controle otimista (RN-10). O tipo enviado deve ser
 * o atual (RN-02). Manter a categoria atual é permitido mesmo que ela tenha sido inativada depois;
 * trocar de categoria exige uma categoria ativa (RN-05).
 */
@Service
public class AtualizarItemCatalogo {

  private final ItemCatalogoRepository itens;
  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public AtualizarItemCatalogo(
      ItemCatalogoRepository itens, CatalogoDaEmpresa catalogo, Clock clock) {
    this.itens = itens;
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDoItem executar(
      UUID id, TipoItem tipo, DadosDoItem dados, UUID categoriaId, long versao) {
    ItemCatalogo item = catalogo.itemParaEscrita(id);
    if (!item.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (item.getTipo() != tipo) {
      throw new TipoDeItemImutavelException();
    }
    if (dados.codigo() != null
        && itens.existsByEmpresaIdAndCodigoAndIdNot(item.getEmpresaId(), dados.codigo(), id)) {
      throw new CodigoDeItemDuplicadoException();
    }
    Categoria categoria =
        item.estaNaCategoria(categoriaId)
            ? item.getCategoria()
            : catalogo.categoriaParaAssociar(categoriaId, item.getEmpresaId());
    item.atualizar(dados, categoria, clock.instant());
    return FichaDoItem.de(catalogo.gravar(item));
  }
}
