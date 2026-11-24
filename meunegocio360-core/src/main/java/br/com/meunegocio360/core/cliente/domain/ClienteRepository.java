package br.com.meunegocio360.core.cliente.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Porta de persistência dos clientes; implementada na camada infra (RNF-010). Toda operação recebe
 * a empresa: não existe consulta de cliente sem filtro por {@code empresa_id} (RNF-096).
 */
public interface ClienteRepository {

  Optional<Cliente> findByIdAndEmpresaId(UUID id, UUID empresaId);

  boolean existsByEmpresaIdAndDocumento(UUID empresaId, String documento);

  boolean existsByEmpresaIdAndDocumentoAndIdNot(UUID empresaId, String documento, UUID id);

  /**
   * Busca paginada da empresa (RN-07, RN-08).
   *
   * @param ativo {@code null} para ativos e inativos
   * @param padraoNome padrão LIKE já escapado (com {@code !}) para o nome, ou {@code null} sem
   *     busca
   * @param padraoDocumento padrão LIKE de prefixo dos dígitos do documento, ou {@code null}
   */
  Page<Cliente> buscar(
      UUID empresaId, Boolean ativo, String padraoNome, String padraoDocumento, Pageable pagina);

  /** Grava e sincroniza já, para que as restrições e a versão sejam aplicadas aqui. */
  Cliente saveAndFlush(Cliente cliente);

  void delete(Cliente cliente);

  void flush();
}
