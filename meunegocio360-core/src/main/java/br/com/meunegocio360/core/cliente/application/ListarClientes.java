package br.com.meunegocio360.core.cliente.application;

import br.com.meunegocio360.core.cliente.domain.ClienteRepository;
import br.com.meunegocio360.core.empresa.application.EmpresaAtiva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /clientes: busca paginada por nome ou documento na empresa ativa (RN-07, RN-08). */
@Service
public class ListarClientes {

  private final ClienteRepository clientes;
  private final EmpresaAtiva empresaAtiva;

  public ListarClientes(ClienteRepository clientes, EmpresaAtiva empresaAtiva) {
    this.clientes = clientes;
    this.empresaAtiva = empresaAtiva;
  }

  /** Sem empresa ativa, a listagem é vazia: só os clientes da empresa ativa são visíveis. */
  @Transactional(readOnly = true)
  public Page<FichaDoCliente> executar(String q, SituacaoDoFiltro situacao, Pageable pagina) {
    TermoDeBusca termo = TermoDeBusca.de(q);
    return empresaAtiva
        .buscarId()
        .map(empresaId ->
            clientes
                .buscar(
                    empresaId,
                    situacao.ativo(),
                    termo.padraoNome(),
                    termo.padraoDocumento(),
                    pagina)
                .map(FichaDoCliente::de))
        .orElseGet(() -> Page.empty(pagina));
  }
}
