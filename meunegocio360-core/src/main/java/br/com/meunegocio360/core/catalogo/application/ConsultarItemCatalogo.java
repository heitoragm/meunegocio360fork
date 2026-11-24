package br.com.meunegocio360.core.catalogo.application;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** GET /itens-catalogo/{id}: inclusive inativos, sempre na empresa ativa. */
@Service
public class ConsultarItemCatalogo {

  private final CatalogoDaEmpresa catalogo;

  public ConsultarItemCatalogo(CatalogoDaEmpresa catalogo) {
    this.catalogo = catalogo;
  }

  @Transactional(readOnly = true)
  public FichaDoItem executar(UUID id) {
    return FichaDoItem.de(catalogo.itemParaLeitura(id));
  }
}
