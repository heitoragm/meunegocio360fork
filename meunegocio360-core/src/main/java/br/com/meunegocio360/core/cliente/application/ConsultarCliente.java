package br.com.meunegocio360.core.cliente.application;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /clientes/{id}: inclusive inativos (RN-07), sempre na empresa ativa. */
@Service
public class ConsultarCliente {

  private final ClientesDaEmpresa clientesDaEmpresa;

  public ConsultarCliente(ClientesDaEmpresa clientesDaEmpresa) {
    this.clientesDaEmpresa = clientesDaEmpresa;
  }

  @Transactional(readOnly = true)
  public FichaDoCliente executar(UUID id) {
    return FichaDoCliente.de(clientesDaEmpresa.paraLeitura(id));
  }
}
