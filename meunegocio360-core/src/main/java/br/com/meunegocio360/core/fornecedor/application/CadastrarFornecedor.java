package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.fornecedor.domain.DadosDoFornecedor;
import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import java.time.Clock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** POST /fornecedores: cadastra na empresa ativa, já ativo (RN-01 e RN-02). */
@Service
public class CadastrarFornecedor {

  private static final Logger log = LoggerFactory.getLogger(CadastrarFornecedor.class);

  private final FornecedorRepository fornecedores;
  private final FornecedoresDaEmpresa fornecedoresDaEmpresa;
  private final Clock clock;

  public CadastrarFornecedor(
      FornecedorRepository fornecedores, FornecedoresDaEmpresa fornecedoresDaEmpresa, Clock clock) {
    this.fornecedores = fornecedores;
    this.fornecedoresDaEmpresa = fornecedoresDaEmpresa;
    this.clock = clock;
  }

  @Transactional
  public FichaDoFornecedor executar(DadosDoFornecedor dados) {
    UUID empresaId = fornecedoresDaEmpresa.empresaParaEscrita();
    if (fornecedores.existsByEmpresaIdAndDocumento(empresaId, dados.documento())) {
      throw new DocumentoDuplicadoException();
    }
    Fornecedor fornecedor =
        fornecedoresDaEmpresa.gravar(Fornecedor.novo(empresaId, dados, clock.instant()));
    // Só o id: documento, nome, e-mail e telefone são dados pessoais (RNF-025).
    log.info("Fornecedor cadastrado fornecedorId={}", fornecedor.getId());
    return FichaDoFornecedor.de(fornecedor);
  }
}
