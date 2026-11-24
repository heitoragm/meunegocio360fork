package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.catalogo.domain.VerificadorVinculoItem;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DELETE /itens-catalogo/{id}: exclusão só sem vínculos (RN-09, RNF-097). Os vínculos são
 * descobertos por todas as implementações de {@link VerificadorVinculoItem}; nesta fase não há
 * nenhuma, e as próximas (vendas, compras) se registram sem alterar esta classe.
 */
@Service
public class ExcluirItemCatalogo {

  private static final Logger log = LoggerFactory.getLogger(ExcluirItemCatalogo.class);

  private final ItemCatalogoRepository itens;
  private final CatalogoDaEmpresa catalogo;
  private final ObjectProvider<VerificadorVinculoItem> verificadores;

  public ExcluirItemCatalogo(
      ItemCatalogoRepository itens,
      CatalogoDaEmpresa catalogo,
      ObjectProvider<VerificadorVinculoItem> verificadores) {
    this.itens = itens;
    this.catalogo = catalogo;
    this.verificadores = verificadores;
  }

  @Transactional
  public void executar(UUID id) {
    ItemCatalogo item = catalogo.itemParaEscrita(id);
    boolean possuiVinculo =
        verificadores
            .orderedStream()
            .anyMatch(v -> v.possuiVinculo(item.getEmpresaId(), item.getId()));
    if (possuiVinculo) {
      throw new ItemComHistoricoException();
    }
    try {
      itens.delete(item);
      itens.flush();
    } catch (DataIntegrityViolationException erro) {
      // Um vínculo criado entre a verificação e a exclusão é barrado pela chave estrangeira.
      throw new ItemComHistoricoException();
    }
    log.info("Item do catálogo excluído itemId={}", id);
  }
}
