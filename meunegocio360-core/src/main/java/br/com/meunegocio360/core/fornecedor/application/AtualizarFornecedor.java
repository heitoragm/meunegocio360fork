package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.fornecedor.domain.DadosDoFornecedor;
import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PUT /fornecedores/{id}: edição com controle otimista; tipo e documento podem mudar (RN-09 de
 * RF-BE-003).
 */
@Service
public class AtualizarFornecedor {

  private final FornecedorRepository fornecedores;
  private final FornecedoresDaEmpresa fornecedoresDaEmpresa;
  private final Clock clock;

  public AtualizarFornecedor(
      FornecedorRepository fornecedores, FornecedoresDaEmpresa fornecedoresDaEmpresa, Clock clock) {
    this.fornecedores = fornecedores;
    this.fornecedoresDaEmpresa = fornecedoresDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoFornecedor executar(UUID id, DadosDoFornecedor dados, long versao) {
    Fornecedor fornecedor = fornecedoresDaEmpresa.paraEscrita(id);
    if (!fornecedor.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (fornecedores.existsByEmpresaIdAndDocumentoAndIdNot(
        fornecedor.getEmpresaId(), dados.documento(), id)) {
      throw new DocumentoDuplicadoException();
    }
    fornecedor.atualizar(dados, clock.instant());
    return FichaDoFornecedor.de(fornecedoresDaEmpresa.gravar(fornecedor));
  }
}
