package br.com.meunegocio360.core.empresa.application;

import br.com.meunegocio360.core.empresa.domain.EmpresaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GET /empresa/inativas: empresas inativas, para que o ADMIN possa reativar uma delas quando não há
 * empresa ativa (RN-02, RN-04). A restrição ao ADMIN fica no controller (RNF-BE-47).
 */
@Service
public class ListarEmpresasInativas {

  private final EmpresaRepository empresas;

  public ListarEmpresasInativas(EmpresaRepository empresas) {
    this.empresas = empresas;
  }

  @Transactional(readOnly = true)
  public Page<DadosDaEmpresa> executar(Pageable pagina) {
    return empresas.findByAtivaFalse(pagina).map(DadosDaEmpresa::de);
  }
}
