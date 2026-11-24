package br.com.meunegocio360.core.catalogo.application;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /categorias/{id}: alvo do Location do cadastro (RNF-BE-12). */
@Service
public class ConsultarCategoria {

  private final CatalogoDaEmpresa catalogo;

  public ConsultarCategoria(CatalogoDaEmpresa catalogo) {
    this.catalogo = catalogo;
  }

  @Transactional(readOnly = true)
  public FichaDaCategoria executar(UUID id) {
    return FichaDaCategoria.de(catalogo.categoriaParaLeitura(id));
  }
}
