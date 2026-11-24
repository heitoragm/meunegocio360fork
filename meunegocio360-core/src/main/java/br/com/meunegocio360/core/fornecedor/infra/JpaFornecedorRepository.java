package br.com.meunegocio360.core.fornecedor.infra;

import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Implementação Spring Data JPA da porta {@link FornecedorRepository}. Os termos de busca entram só
 * como parâmetros (nunca concatenados na consulta). A comparação do nome não distingue maiúsculas
 * nem acentos pela collation da coluna (RNF-BE-26).
 */
interface JpaFornecedorRepository extends Repository<Fornecedor, UUID>, FornecedorRepository {

  @Override
  @Query(
      """
      select c from Fornecedor c
       where c.empresaId = :empresaId
         and (:ativo is null or c.ativo = :ativo)
         and (:padraoNome is null
              or c.nome like :padraoNome escape '!'
              or (:padraoDocumento is not null and c.documento like :padraoDocumento))
      """)
  Page<Fornecedor> buscar(
      @Param("empresaId") UUID empresaId,
      @Param("ativo") Boolean ativo,
      @Param("padraoNome") String padraoNome,
      @Param("padraoDocumento") String padraoDocumento,
      Pageable pagina);
}
