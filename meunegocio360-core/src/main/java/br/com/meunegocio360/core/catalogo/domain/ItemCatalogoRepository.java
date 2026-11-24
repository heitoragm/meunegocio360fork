package br.com.meunegocio360.core.catalogo.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Porta de persistência dos itens do catálogo (RNF-010). Toda operação recebe a empresa. */
public interface ItemCatalogoRepository {

  Optional<ItemCatalogo> findByIdAndEmpresaId(UUID id, UUID empresaId);

  boolean existsByEmpresaIdAndCodigo(UUID empresaId, String codigo);

  boolean existsByEmpresaIdAndCodigoAndIdNot(UUID empresaId, String codigo, UUID id);

  /** Itens (de qualquer situação) que usam a categoria: impede a exclusão dela (RN-06). */
  long contarPorCategoria(UUID categoriaId);

  /**
   * Busca paginada com filtros combinados por E (RN-08, RN-10). {@code tipo} é comparado por
   * igualdade: {@code PRODUTO} nunca devolve serviços.
   *
   * @param ativo {@code null} para ativos e inativos
   * @param tipo {@code null} para todos os tipos
   * @param categoriaId {@code null} para todas as categorias
   * @param padrao padrão LIKE já escapado (com {@code !}) para nome ou código, ou {@code null}
   */
  Page<ItemCatalogo> buscar(
      UUID empresaId,
      Boolean ativo,
      TipoItem tipo,
      UUID categoriaId,
      String padrao,
      Pageable pagina);

  ItemCatalogo saveAndFlush(ItemCatalogo item);

  void delete(ItemCatalogo item);

  void flush();
}
