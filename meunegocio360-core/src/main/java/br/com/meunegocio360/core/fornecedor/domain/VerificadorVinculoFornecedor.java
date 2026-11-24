package br.com.meunegocio360.core.fornecedor.domain;

import java.util.UUID;

/**
 * Ponto de extensão da exclusão de fornecedores (RF-BE-004, RN-03 e RN-04; RNF-097). A exclusão
 * consulta TODAS as implementações registradas como beans do Spring; havendo vínculo em qualquer
 * uma, o fornecedor só pode ser inativado.
 *
 * <p>Nesta fase não há implementações. Cada requisito que criar um vínculo registra a sua, sem
 * alterar a feature {@code fornecedor}:
 *
 * <ul>
 *   <li>contas a pagar (mesmo serviço): consulta a própria tabela, dentro da feature de contas;
 *   <li>pedidos de compra (serviço Suprimentos): consulta a API daquele serviço, com timeout e
 *       tratamento de falha (RNF-090, RNF-092). Na dúvida (serviço indisponível), a implementação
 *       DEVE responder que há vínculo, para nunca excluir um fornecedor com histórico.
 * </ul>
 */
public interface VerificadorVinculoFornecedor {

  /** Indica se o fornecedor da empresa informada tem algum registro vinculado. */
  boolean possuiVinculo(UUID empresaId, UUID fornecedorId);
}
