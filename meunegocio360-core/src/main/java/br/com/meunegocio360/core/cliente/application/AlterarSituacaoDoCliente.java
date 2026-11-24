package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.Cliente;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PATCH /clientes/{id}/situacao: inativa ou reativa (RN-07). Pedir a situação atual não altera nada
 * (idempotente), nem a versão.
 */
@Service
public class AlterarSituacaoDoCliente {

  private final ClientesDaEmpresa clientesDaEmpresa;
  private final Clock clock;

  public AlterarSituacaoDoCliente(ClientesDaEmpresa clientesDaEmpresa, Clock clock) {
    this.clientesDaEmpresa = clientesDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoCliente executar(UUID id, boolean ativo, long versao) {
    Cliente cliente = clientesDaEmpresa.paraEscrita(id);
    if (!cliente.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (!cliente.definirAtivo(ativo, clock.instant())) {
      return FichaDoCliente.de(cliente);
    }
    return FichaDoCliente.de(clientesDaEmpresa.gravar(cliente));
  }
}
