package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.cliente.domain.ClienteRepository;
import br.com.meunegocio360.core.cliente.domain.DadosDoCliente;
import java.time.Clock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** POST /clientes: cadastra na empresa ativa, já ativo (RN-01 a RN-04). */
@Service
public class CadastrarCliente {

  private static final Logger log = LoggerFactory.getLogger(CadastrarCliente.class);

  private final ClienteRepository clientes;
  private final ClientesDaEmpresa clientesDaEmpresa;
  private final Clock clock;

  public CadastrarCliente(
      ClienteRepository clientes, ClientesDaEmpresa clientesDaEmpresa, Clock clock) {
    this.clientes = clientes;
    this.clientesDaEmpresa = clientesDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoCliente executar(DadosDoCliente dados) {
    UUID empresaId = clientesDaEmpresa.empresaParaEscrita();
    if (clientes.existsByEmpresaIdAndDocumento(empresaId, dados.documento())) {
      throw new DocumentoDuplicadoException();
    }
    Cliente cliente = clientesDaEmpresa.gravar(Cliente.novo(empresaId, dados, clock.instant()));
    // Só o id: documento, nome, e-mail e telefone são dados pessoais (RNF-025).
    log.info("Cliente cadastrado clienteId={}", cliente.getId());
    return FichaDoCliente.de(cliente);
  }
}
