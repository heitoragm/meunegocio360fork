package br.com.meunegocio360.core.catalogo.infra;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/** Implementação Spring Data JPA da porta {@link CategoriaRepository}. */
interface JpaCategoriaRepository extends Repository<Categoria, UUID>, CategoriaRepository {

  @Override
  @Query(
      """
      select c from Categoria c
       where c.empresaId = :empresaId
         and (:ativo is null or c.ativo = :ativo)
         and (:padraoNome is null or c.nome like :padraoNome escape '!')
      """)
  Page<Categoria> buscar(
      @Param("empresaId") UUID empresaId,
      @Param("ativo") Boolean ativo,
      @Param("padraoNome") String padraoNome,
      Pageable pagina);
}
