package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import br.com.meunegocio360.core.fornecedor.domain.VerificadorVinculoFornecedor;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DELETE /fornecedores/{id}: exclusão só sem vínculos (RN-03, RN-04, RNF-097). Os vínculos são
 * descobertos por todas as implementações de {@link VerificadorVinculoFornecedor}; nesta fase não
 * há nenhuma, e as próximas (contas a pagar, pedidos de compra) se registram sem alterar esta
 * classe.
 */
@Service
public class ExcluirFornecedor {

  private static final Logger log = LoggerFactory.getLogger(ExcluirFornecedor.class);

  private final FornecedorRepository fornecedores;
  private final FornecedoresDaEmpresa fornecedoresDaEmpresa;
  private final ObjectProvider<VerificadorVinculoFornecedor> verificadores;

  public ExcluirFornecedor(
      FornecedorRepository fornecedores,
      FornecedoresDaEmpresa fornecedoresDaEmpresa,
      ObjectProvider<VerificadorVinculoFornecedor> verificadores) {
    this.fornecedores = fornecedores;
    this.fornecedoresDaEmpresa = fornecedoresDaEmpresa;
    this.verificadores = verificadores;
  }

  @Transactional
  public void executar(UUID id) {
    Fornecedor fornecedor = fornecedoresDaEmpresa.paraEscrita(id);
    boolean possuiVinculo =
        verificadores
            .orderedStream()
            .anyMatch(v -> v.possuiVinculo(fornecedor.getEmpresaId(), fornecedor.getId()));
    if (possuiVinculo) {
      throw new FornecedorComHistoricoException();
    }
    try {
      fornecedores.delete(fornecedor);
      fornecedores.flush();
    } catch (DataIntegrityViolationException erro) {
      // Um vínculo criado entre a verificação e a exclusão é barrado pela chave estrangeira.
      throw new FornecedorComHistoricoException();
    }
    log.info("Fornecedor excluído fornecedorId={}", id);
  }
}
