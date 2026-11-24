package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.ItemCatalogoRepository;
import br.com.meunegocio360.core.catalogo.domain.TipoItem;
import br.com.meunegocio360.core.shared.persistence.TermoDeBusca;
import br.com.meunegocio360.core.shared.web.SituacaoDoFiltro;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GET /itens-catalogo: filtros por texto, tipo, categoria e situação combinados por E (RN-08,
 * RN-10). {@code tipo=PRODUTO} é igualdade: nunca devolve serviços.
 */
@Service
public class ListarItensCatalogo {

  private final ItemCatalogoRepository itens;
  private final CatalogoDaEmpresa catalogo;

  public ListarItensCatalogo(ItemCatalogoRepository itens, CatalogoDaEmpresa catalogo) {
    this.itens = itens;
    this.catalogo = catalogo;
  }

  /** Sem empresa ativa, a listagem é vazia: só os itens da empresa ativa são visíveis. */
  @Transactional(readOnly = true)
  public Page<FichaDoItem> executar(
      String q, TipoItem tipo, UUID categoriaId, SituacaoDoFiltro situacao, Pageable pagina) {
    String padrao = TermoDeBusca.padraoContem(q);
    return catalogo
        .empresaParaLeitura()
        .map(empresaId ->
            itens
                .buscar(empresaId, situacao.ativo(), tipo, categoriaId, padrao, pagina)
                .map(FichaDoItem::de))
        .orElseGet(() -> Page.empty(pagina));
  }
}
