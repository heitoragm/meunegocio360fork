package br.com.meunegocio360.core.catalogo.domain;

import java.util.UUID;

/**
 * Ponto de extensão da exclusão de itens do catálogo (RF-BE-005, RN-09; RNF-097). A exclusão
 * consulta TODAS as implementações registradas como beans do Spring; havendo vínculo em qualquer
 * uma, o item só pode ser inativado.
 *
 * <p>Nesta fase não há implementações. Cada requisito que criar um vínculo registra a sua, sem
 * alterar a feature {@code catalogo}:
 *
 * <ul>
 *   <li>vendas (mesmo serviço): consulta a própria tabela, dentro da feature de vendas;
 *   <li>compras e estoque (serviço Suprimentos): consulta a API daquele serviço, com timeout e
 *       tratamento de falha (RNF-090, RNF-092). Na dúvida (serviço indisponível), a implementação
 *       DEVE responder que há vínculo, para nunca excluir um item com histórico.
 * </ul>
 */
public interface VerificadorVinculoItem {

  /** Indica se o item da empresa informada tem algum registro vinculado. */
  boolean possuiVinculo(UUID empresaId, UUID itemId);
}
