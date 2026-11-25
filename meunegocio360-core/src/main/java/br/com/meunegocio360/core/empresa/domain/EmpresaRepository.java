package br.com.meunegocio360.core.empresa.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Porta de persistência da empresa; implementada na camada infra (RNF-010). Outras features NÃO
 * usam este repositório: elas obtêm a empresa ativa por {@code EmpresaAtiva} (RNF-BE-05).
 */
public interface EmpresaRepository {

  Optional<Empresa> findById(UUID id);

  Optional<Empresa> findByAtivaTrue();

  boolean existsByAtivaTrue();

  /** Empresas inativas, para reativação pelo ADMIN. */
  Page<Empresa> findByAtivaFalse(Pageable pagina);

  boolean existsByCnpj(String cnpj);

  boolean existsByCnpjAndIdNot(String cnpj, UUID id);

  /** Grava e sincroniza com o banco já, para que as restrições e a versão sejam aplicadas aqui. */
  Empresa saveAndFlush(Empresa empresa);
}
