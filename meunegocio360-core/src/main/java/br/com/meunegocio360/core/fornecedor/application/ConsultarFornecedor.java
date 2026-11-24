package br.com.meunegocio360.core.fornecedor.application;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /fornecedores/{id}: inclusive inativos (RN-07 de RF-BE-003), sempre na empresa ativa. */
@Service
public class ConsultarFornecedor {

  private final FornecedoresDaEmpresa fornecedoresDaEmpresa;

  public ConsultarFornecedor(FornecedoresDaEmpresa fornecedoresDaEmpresa) {
    this.fornecedoresDaEmpresa = fornecedoresDaEmpresa;
  }

  @Transactional(readOnly = true)
  public FichaDoFornecedor executar(UUID id) {
    return FichaDoFornecedor.de(fornecedoresDaEmpresa.paraLeitura(id));
  }
}
