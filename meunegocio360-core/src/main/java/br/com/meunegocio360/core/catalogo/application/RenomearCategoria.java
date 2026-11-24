package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import br.com.meunegocio360.core.shared.web.VersaoDesatualizadaException;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** PUT /categorias/{id}: renomeia com controle otimista, mantendo o nome único (RN-06). */
@Service
public class RenomearCategoria {

  private final CategoriaRepository categorias;
  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public RenomearCategoria(
      CategoriaRepository categorias, CatalogoDaEmpresa catalogo, Clock clock) {
    this.categorias = categorias;
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDaCategoria executar(UUID id, String nome, long versao) {
    Categoria categoria = catalogo.categoriaParaEscrita(id);
    if (!categoria.estaNaVersao(versao)) {
      throw new VersaoDesatualizadaException();
    }
    if (categorias.existsByEmpresaIdAndNomeAndIdNot(categoria.getEmpresaId(), nome, id)) {
      throw new NomeDeCategoriaDuplicadoException();
    }
    categoria.renomear(nome, clock.instant());
    return FichaDaCategoria.de(catalogo.gravar(categoria));
  }
}
