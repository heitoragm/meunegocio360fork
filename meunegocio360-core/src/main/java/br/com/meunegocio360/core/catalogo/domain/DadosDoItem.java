package br.com.meunegocio360.core.catalogo.domain;

import java.math.BigDecimal;

/**
 * Campos editáveis do item, já validados e normalizados (RN-03, RN-04). {@code unidade} nula assume
 * o padrão do tipo; {@code preco} tem escala 2 e não é negativo.
 */
public record DadosDoItem(
    String codigo, String nome, String descricao, String unidade, BigDecimal preco) {}
