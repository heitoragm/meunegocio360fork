package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DELETE /categorias/{id}: só sem itens vinculados, de qualquer situação (RN-06). Nunca apaga itens
 * em cascata.
 */
@Service
public class ExcluirCategoria {

  private final CategoriaRepository categorias;
  private final ItemCatalogoRepository itens;
  private final CatalogoDaEmpresa catalogo;

  public ExcluirCategoria(
      CategoriaRepository categorias, ItemCatalogoRepository itens, CatalogoDaEmpresa catalogo) {
    this.categorias = categorias;
    this.itens = itens;
    this.catalogo = catalogo;
  }

  @Transactional
  public void executar(UUID id) {
    Categoria categoria = catalogo.categoriaParaEscrita(id);
    long itensVinculados = itens.contarPorCategoria(id);
    if (itensVinculados > 0) {
      throw new CategoriaEmUsoException(itensVinculados);
    }
    try {
      categorias.delete(categoria);
      categorias.flush();
    } catch (DataIntegrityViolationException erro) {
      // Um item associado entre a contagem e a exclusão é barrado pela chave estrangeira.
      throw new CategoriaEmUsoException(1);
    }
  }
}
