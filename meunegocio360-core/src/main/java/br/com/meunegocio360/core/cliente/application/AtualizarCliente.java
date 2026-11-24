package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.cliente.domain.ClienteRepository;
import br.com.meunegocio360.core.cliente.domain.DadosDoCliente;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** PUT /clientes/{id}: edição com controle otimista; tipo e documento podem mudar (RN-09). */
@Service
public class AtualizarCliente {

  private final ClienteRepository clientes;
  private final ClientesDaEmpresa clientesDaEmpresa;
  private final Clock clock;

  public AtualizarCliente(
      ClienteRepository clientes, ClientesDaEmpresa clientesDaEmpresa, Clock clock) {
    this.clientes = clientes;
    this.clientesDaEmpresa = clientesDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoCliente executar(UUID id, DadosDoCliente dados, long versao) {
    Cliente cliente = clientesDaEmpresa.paraEscrita(id);
    if (!cliente.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (clientes.existsByEmpresaIdAndDocumentoAndIdNot(
        cliente.getEmpresaId(), dados.documento(), id)) {
      throw new DocumentoDuplicadoException();
    }
    cliente.atualizar(dados, clock.instant());
    return FichaDoCliente.de(clientesDaEmpresa.gravar(cliente));
  }
}
