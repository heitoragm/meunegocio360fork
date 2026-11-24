package br.com.meunegocio360.core.fornecedor.application;

import br.com.meunegocio360.core.empresa.application.EmpresaAtiva;
import br.com.meunegocio360.core.fornecedor.domain.FornecedorRepository;
import br.com.meunegocio360.core.shared.persistence.TermoDeBusca;
import br.com.meunegocio360.core.shared.web.SituacaoDoFiltro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GET /fornecedores: busca paginada por nome ou documento na empresa ativa (RN-07 e RN-08 de
 * RF-BE-003).
 */
@Service
public class ListarFornecedores {

  private final FornecedorRepository fornecedores;
  private final EmpresaAtiva empresaAtiva;

  public ListarFornecedores(FornecedorRepository fornecedores, EmpresaAtiva empresaAtiva) {
    this.fornecedores = fornecedores;
    this.empresaAtiva = empresaAtiva;
  }

  /** Sem empresa ativa, a listagem é vazia: só os fornecedores da empresa ativa são visíveis. */
  @Transactional(readOnly = true)
  public Page<FichaDoFornecedor> executar(String q, SituacaoDoFiltro situacao, Pageable pagina) {
    TermoDeBusca termo = TermoDeBusca.de(q);
    return empresaAtiva
        .buscarId()
        .map(empresaId ->
            fornecedores
                .buscar(
                    empresaId,
                    situacao.ativo(),
                    termo.padraoNome(),
                    termo.padraoDocumento(),
                    pagina)
                .map(FichaDoFornecedor::de))
        .orElseGet(() -> Page.empty(pagina));
  }
}
