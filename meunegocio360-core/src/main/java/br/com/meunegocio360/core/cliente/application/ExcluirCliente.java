package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.cliente.domain.ClienteRepository;
import br.com.meunegocio360.core.cliente.domain.VerificadorVinculoCliente;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DELETE /clientes/{id}: exclusão só sem vínculos (RN-05, RN-06, RNF-097). Os vínculos são
 * descobertos por todas as implementações de {@link VerificadorVinculoCliente}; nesta fase não há
 * nenhuma, e as próximas (vendas, OS) se registram sem alterar esta classe.
 */
@Service
public class ExcluirCliente {

  private static final Logger log = LoggerFactory.getLogger(ExcluirCliente.class);

  private final ClienteRepository clientes;
  private final ClientesDaEmpresa clientesDaEmpresa;
  private final ObjectProvider<VerificadorVinculoCliente> verificadores;

  public ExcluirCliente(
      ClienteRepository clientes,
      ClientesDaEmpresa clientesDaEmpresa,
      ObjectProvider<VerificadorVinculoCliente> verificadores) {
    this.clientes = clientes;
    this.clientesDaEmpresa = clientesDaEmpresa;
    this.verificadores = verificadores;
  }

  @Transactional
  public void executar(UUID id) {
    Cliente cliente = clientesDaEmpresa.paraEscrita(id);
    boolean possuiVinculo =
        verificadores
            .orderedStream()
            .anyMatch(v -> v.possuiVinculo(cliente.getEmpresaId(), cliente.getId()));
    if (possuiVinculo) {
      throw new ClienteComHistoricoException();
    }
    try {
      clientes.delete(cliente);
      clientes.flush();
    } catch (DataIntegrityViolationException erro) {
      // Um vínculo criado entre a verificação e a exclusão é barrado pela chave estrangeira.
      throw new ClienteComHistoricoException();
    }
    log.info("Cliente excluído clienteId={}", id);
  }
}
