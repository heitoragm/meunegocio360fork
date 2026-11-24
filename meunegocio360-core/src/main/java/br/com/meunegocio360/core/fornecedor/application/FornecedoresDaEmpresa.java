package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.empresa.application.EmpresaAtiva;
import br.com.meunegocio360.core.fornecedor.domain.Fornecedor;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import br.com.meunegocio360.core.shared.persistence.RestricaoDoBanco;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

/**
 * Acesso aos fornecedores sempre dentro da empresa ativa (RN-01, RNF-096). A empresa vem de {@link
 * EmpresaAtiva}, nunca da requisição; um fornecedor de outra empresa simplesmente não existe aqui.
 */
@Component
class FornecedoresDaEmpresa {

  private final FornecedorRepository fornecedores;
  private final EmpresaAtiva empresaAtiva;

  FornecedoresDaEmpresa(FornecedorRepository fornecedores, EmpresaAtiva empresaAtiva) {
    this.fornecedores = fornecedores;
    this.empresaAtiva = empresaAtiva;
  }

  /** Para escrita: sem empresa ativa, 422 COMPANY_NOT_ACTIVE (RN-04 do RF-BE-002). */
  UUID empresaParaEscrita() {
    return empresaAtiva.exigirId();
  }

  /** Para leitura: sem empresa ativa não há fornecedores visíveis, e o fornecedor "não existe". */
  Fornecedor paraLeitura(UUID fornecedorId) {
    return empresaAtiva
        .buscarId()
        .flatMap(empresaId -> fornecedores.findByIdAndEmpresaId(fornecedorId, empresaId))
        .orElseThrow(FornecedorNaoEncontradoException::new);
  }

  Fornecedor paraEscrita(UUID fornecedorId) {
    return fornecedores
        .findByIdAndEmpresaId(fornecedorId, empresaParaEscrita())
        .orElseThrow(FornecedorNaoEncontradoException::new);
  }

  /** Grava e traduz a violação de unicidade do banco no erro de negócio (RN-02, RNF-BE-15). */
  Fornecedor gravar(Fornecedor fornecedor) {
    try {
      return fornecedores.saveAndFlush(fornecedor);
    } catch (DataIntegrityViolationException erro) {
      if (RestricaoDoBanco.foiViolada(erro, "uk_fornecedor_empresa_documento")) {
        throw new DocumentoDuplicadoException();
      }
      throw erro;
    }
  }
}
