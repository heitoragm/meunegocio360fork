package br.com.meunegocio360.core.catalogo.application;

import br.com.meunegocio360.core.catalogo.domain.Categoria;
import br.com.meunegocio360.core.catalogo.domain.CategoriaRepository;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** POST /categorias: nome único na empresa, sem distinguir maiúsculas e acentos (RN-06). */
@Service
public class CadastrarCategoria {

  private final CategoriaRepository categorias;
  private final CatalogoDaEmpresa catalogo;
  private final Clock clock;

  public CadastrarCategoria(
      CategoriaRepository categorias, CatalogoDaEmpresa catalogo, Clock clock) {
    this.categorias = categorias;
    this.catalogo = catalogo;
    this.clock = clock;
  }

  @Transactional
  public FichaDaCategoria executar(String nome) {
    UUID empresaId = catalogo.empresaParaEscrita();
    if (categorias.existsByEmpresaIdAndNome(empresaId, nome)) {
      throw new NomeDeCategoriaDuplicadoException();
    }
    return FichaDaCategoria.de(catalogo.gravar(Categoria.nova(empresaId, nome, clock.instant())));
  }
}
