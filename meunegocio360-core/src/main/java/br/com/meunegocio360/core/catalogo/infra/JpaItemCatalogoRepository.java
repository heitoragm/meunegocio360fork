package br.com.meunegocio360.core.catalogo.infra;

import br.com.meunegocio360.core.catalogo.domain.ItemCatalogo;
import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Implementação Spring Data JPA da porta {@link ItemCatalogoRepository}. Os filtros entram só como
 * parâmetros; nome e código usam a collation sem distinção de maiúsculas e acentos (RNF-BE-26).
 */
interface JpaItemCatalogoRepository extends Repository<ItemCatalogo, UUID>, ItemCatalogoRepository {

  @Override
  @Query("select count(i) from ItemCatalogo i where i.categoria.id = :categoriaId")
  long contarPorCategoria(@Param("categoriaId") UUID categoriaId);

  @Override
  @Query(
      value =
          """
          select i from ItemCatalogo i left join fetch i.categoria
           where i.empresaId = :empresaId
             and (:ativo is null or i.ativo = :ativo)
             and (:tipo is null or i.tipo = :tipo)
             and (:categoriaId is null or i.categoria.id = :categoriaId)
             and (:padrao is null
                  or i.nome like :padrao escape '!'
                  or i.codigo like :padrao escape '!')
          """,
      countQuery =
          """
          select count(i) from ItemCatalogo i
           where i.empresaId = :empresaId
             and (:ativo is null or i.ativo = :ativo)
             and (:tipo is null or i.tipo = :tipo)
             and (:categoriaId is null or i.categoria.id = :categoriaId)
             and (:padrao is null
                  or i.nome like :padrao escape '!'
                  or i.codigo like :padrao escape '!')
          """)
  Page<ItemCatalogo> buscar(
      @Param("empresaId") UUID empresaId,
      @Param("ativo") Boolean ativo,
      @Param("tipo") TipoItem tipo,
      @Param("categoriaId") UUID categoriaId,
      @Param("padrao") String padrao,
      Pageable pagina);
}
