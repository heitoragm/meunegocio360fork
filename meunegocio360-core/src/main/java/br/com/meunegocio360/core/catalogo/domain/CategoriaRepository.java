package br.com.meunegocio360.core.catalogo.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Porta de persistência das categorias (RNF-010). Toda operação recebe a empresa (RNF-096). A
 * comparação de nome usa a collation da coluna: não distingue maiúsculas nem acentos (RN-06).
 */
public interface CategoriaRepository {

  Optional<Categoria> findByIdAndEmpresaId(UUID id, UUID empresaId);

  boolean existsByEmpresaIdAndNome(UUID empresaId, String nome);

  boolean existsByEmpresaIdAndNomeAndIdNot(UUID empresaId, String nome, UUID id);

  /**
   * @param ativo {@code null} para ativas e inativas
   * @param padraoNome padrão LIKE já escapado (com {@code !}), ou {@code null} sem busca
   */
  Page<Categoria> buscar(UUID empresaId, Boolean ativo, String padraoNome, Pageable pagina);

  Categoria saveAndFlush(Categoria categoria);

  void delete(Categoria categoria);

  void flush();
}
