package br.com.meunegocio360.core.cliente.domain;

import java.util.UUID;

/**
 * Ponto de extensão da exclusão de clientes (RN-05, RN-06, RNF-097). A exclusão consulta TODAS as
 * implementações registradas como beans do Spring; havendo vínculo em qualquer uma, o cliente só
 * pode ser inativado.
 *
 * <p>Nesta fase não há implementações. Cada requisito que criar um vínculo registra a sua, sem
 * alterar a feature {@code cliente}:
 *
 * <ul>
 *   <li>vendas (mesmo serviço): consulta a própria tabela de vendas, dentro da feature de vendas;
 *   <li>ordens de serviço (serviço Serviços): consulta a API daquele serviço, com timeout e
 *       tratamento de falha (RNF-090, RNF-092). Na dúvida (serviço indisponível), a implementação
 *       DEVE responder que há vínculo, para nunca excluir um cliente com histórico.
 * </ul>
 */
public interface VerificadorVinculoCliente {

  /** Indica se o cliente da empresa informada tem algum registro vinculado. */
  boolean possuiVinculo(UUID empresaId, UUID clienteId);
}
