package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PATCH /fornecedores/{id}/situacao: inativa ou reativa (RN-07 de RF-BE-003). Pedir a situação
 * atual não altera nada (idempotente), nem a versão.
 */
@Service
public class AlterarSituacaoDoFornecedor {

  private final FornecedoresDaEmpresa fornecedoresDaEmpresa;
  private final Clock clock;

  public AlterarSituacaoDoFornecedor(FornecedoresDaEmpresa fornecedoresDaEmpresa, Clock clock) {
    this.fornecedoresDaEmpresa = fornecedoresDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoFornecedor executar(UUID id, boolean ativo, long versao) {
    Fornecedor fornecedor = fornecedoresDaEmpresa.paraEscrita(id);
    if (!fornecedor.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (!fornecedor.definirAtivo(ativo, clock.instant())) {
      return FichaDoFornecedor.de(fornecedor);
    }
    return FichaDoFornecedor.de(fornecedoresDaEmpresa.gravar(fornecedor));
  }
}
