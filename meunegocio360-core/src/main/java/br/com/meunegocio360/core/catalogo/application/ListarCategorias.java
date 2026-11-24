package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import br.com.meunegocio360.core.shared.persistence.TermoDeBusca;
import br.com.meunegocio360.core.shared.web.SituacaoDoFiltro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /categorias: busca paginada por nome na empresa ativa. */
@Service
public class ListarCategorias {

  private final CategoriaRepository categorias;
  private final CatalogoDaEmpresa catalogo;

  public ListarCategorias(CategoriaRepository categorias, CatalogoDaEmpresa catalogo) {
    this.categorias = categorias;
    this.catalogo = catalogo;
  }

  @Transactional(readOnly = true)
  public Page<FichaDaCategoria> executar(String q, SituacaoDoFiltro situacao, Pageable pagina) {
    String padrao = TermoDeBusca.padraoContem(q);
    return catalogo
        .empresaParaLeitura()
        .map(empresaId ->
            categorias
                .buscar(empresaId, situacao.ativo(), padrao, pagina)
                .map(FichaDaCategoria::de))
        .orElseGet(() -> Page.empty(pagina));
  }
}
