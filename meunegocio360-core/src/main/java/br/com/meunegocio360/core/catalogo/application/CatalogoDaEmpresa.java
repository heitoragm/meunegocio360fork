package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.empresa.application.EmpresaAtiva;
import br.com.meunegocio360.core.shared.persistence.RestricaoDoBanco;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Acesso a itens e categorias sempre dentro da empresa ativa (RN-01, RNF-096). A empresa vem de
 * {@link EmpresaAtiva}, nunca da requisição; registros de outra empresa simplesmente não existem
 * aqui (inclusive uma {@code categoriaId} de outra empresa).
 */
@Component
class CatalogoDaEmpresa {

  private final ItemCatalogoRepository itens;
  private final CategoriaRepository categorias;
  private final EmpresaAtiva empresaAtiva;

  CatalogoDaEmpresa(
      ItemCatalogoRepository itens, CategoriaRepository categorias, EmpresaAtiva empresaAtiva) {
    this.itens = itens;
    this.categorias = categorias;
    this.empresaAtiva = empresaAtiva;
  }

  /** Para escrita: sem empresa ativa, 422 COMPANY_NOT_ACTIVE. */
  UUID empresaParaEscrita() {
    return empresaAtiva.exigirId();
  }

  /** Para leitura: sem empresa ativa não há registros visíveis. */
  Optional<UUID> empresaParaLeitura() {
    return empresaAtiva.buscarId();
  }

  ItemCatalogo itemParaLeitura(UUID itemId) {
    return empresaParaLeitura()
        .flatMap(empresaId -> itens.findByIdAndEmpresaId(itemId, empresaId))
        .orElseThrow(ItemNaoEncontradoException::new);
  }

  ItemCatalogo itemParaEscrita(UUID itemId) {
    return itens
        .findByIdAndEmpresaId(itemId, empresaParaEscrita())
        .orElseThrow(ItemNaoEncontradoException::new);
  }

  Categoria categoriaParaLeitura(UUID categoriaId) {
    return empresaParaLeitura()
        .flatMap(empresaId -> categorias.findByIdAndEmpresaId(categoriaId, empresaId))
        .orElseThrow(CategoriaNaoEncontradaException::new);
  }

  Categoria categoriaParaEscrita(UUID categoriaId) {
    return categorias
        .findByIdAndEmpresaId(categoriaId, empresaParaEscrita())
        .orElseThrow(CategoriaNaoEncontradaException::new);
  }

  /**
   * RN-05: a categoria associada a um item deve existir na empresa e estar ativa. {@code null}
   * significa "sem categoria", o que é permitido.
   */
  Categoria categoriaParaAssociar(UUID categoriaId, UUID empresaId) {
    if (categoriaId == null) {
      return null;
    }
    return categorias
        .findByIdAndEmpresaId(categoriaId, empresaId)
        .filter(Categoria::isAtivo)
        .orElseThrow(CategoriaInvalidaException::new);
  }

  /** Grava e traduz a unicidade do código no erro de negócio (RN-04, RNF-BE-15). */
  ItemCatalogo gravar(ItemCatalogo item) {
    try {
      return itens.saveAndFlush(item);
    } catch (DataIntegrityViolationException erro) {
      if (RestricaoDoBanco.foiViolada(erro, "uk_item_catalogo_empresa_codigo")) {
        throw new CodigoDeItemDuplicadoException();
      }
      throw erro;
    }
  }

  /** Grava e traduz a unicidade do nome no erro de negócio (RN-06, RNF-BE-15). */
  Categoria gravar(Categoria categoria) {
    try {
      return categorias.saveAndFlush(categoria);
    } catch (DataIntegrityViolationException erro) {
      if (RestricaoDoBanco.foiViolada(erro, "uk_categoria_empresa_nome")) {
        throw new NomeDeCategoriaDuplicadoException();
      }
      throw erro;
    }
  }
}
