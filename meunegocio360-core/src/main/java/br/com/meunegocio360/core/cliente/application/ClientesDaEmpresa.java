package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.cliente.domain.ClienteRepository;
import br.com.meunegocio360.core.empresa.application.EmpresaAtiva;
import br.com.meunegocio360.core.shared.persistence.RestricaoDoBanco;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Acesso aos clientes sempre dentro da empresa ativa (RN-01, RNF-096). A empresa vem de {@link
 * EmpresaAtiva}, nunca da requisição; um cliente de outra empresa simplesmente não existe aqui.
 */
@Component
class ClientesDaEmpresa {

  private final ClienteRepository clientes;
  private final EmpresaAtiva empresaAtiva;

  ClientesDaEmpresa(ClienteRepository clientes, EmpresaAtiva empresaAtiva) {
    this.clientes = clientes;
    this.empresaAtiva = empresaAtiva;
  }

  /** Para escrita: sem empresa ativa, 422 COMPANY_NOT_ACTIVE (RN-04 do RF-BE-002). */
  UUID empresaParaEscrita() {
    return empresaAtiva.exigirId();
  }

  /** Para leitura: sem empresa ativa não há clientes visíveis, e o cliente "não existe". */
  Cliente paraLeitura(UUID clienteId) {
    return empresaAtiva
        .buscarId()
        .flatMap(empresaId -> clientes.findByIdAndEmpresaId(clienteId, empresaId))
        .orElseThrow(ClienteNaoEncontradoException::new);
  }

  Cliente paraEscrita(UUID clienteId) {
    return clientes
        .findByIdAndEmpresaId(clienteId, empresaParaEscrita())
        .orElseThrow(ClienteNaoEncontradoException::new);
  }

  /** Grava e traduz a violação de unicidade do banco no erro de negócio (RN-03, RNF-BE-15). */
  Cliente gravar(Cliente cliente) {
    try {
      return clientes.saveAndFlush(cliente);
    } catch (DataIntegrityViolationException erro) {
      if (RestricaoDoBanco.foiViolada(erro, "uk_cliente_empresa_documento")) {
        throw new DocumentoDuplicadoException();
      }
      throw erro;
    }
  }
}
