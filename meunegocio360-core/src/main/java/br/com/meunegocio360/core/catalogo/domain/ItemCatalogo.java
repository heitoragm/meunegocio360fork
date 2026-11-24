package br.com.meunegocio360.core.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Item do catálogo, produto ou serviço (RF-BE-005). O tipo é fixado na criação (RN-02).
 *
 * <p>A coluna {@code estoque_minimo} existe na tabela, mas NÃO é mapeada aqui (RN-07): o banco
 * aplica o padrão 0 e, sem o campo na entidade, nenhum DTO consegue lê-la ou gravá-la.
 */
@Entity
@Table(name = "item_catalogo")
public class ItemCatalogo {

  @Id private UUID id;

  @Column(name = "empresa_id", nullable = false, updatable = false)
  private UUID empresaId;

  /** Relação dentro da própria feature; a categoria pertence à mesma empresa (RN-05). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(nullable = false, length = 10, updatable = false)
  private TipoItem tipo;

  @Column(length = 40)
  private String codigo;

  @Column(nullable = false, length = 150)
  private String nome;

  @Column(length = 500)
  private String descricao;

  @Column(nullable = false, length = 6)
  private String unidade;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal preco;

  @Column(nullable = false)
  private boolean ativo;

  @Column(name = "criado_em", nullable = false, updatable = false)
  private Instant criadoEm;

  @Column(name = "atualizado_em", nullable = false)
  private Instant atualizadoEm;

  /** Wrapper (e não primitivo) para que o Spring Data reconheça a entidade nova pelo valor nulo. */
  @Version
  @Column(nullable = false)
  private Long versao;

  protected ItemCatalogo() {}

  /** Novo item, já ativo, na empresa informada pelo servidor (RN-01). */
  public static ItemCatalogo novo(
      UUID empresaId, TipoItem tipo, DadosDoItem dados, Categoria categoria, Instant agora) {
    ItemCatalogo item = new ItemCatalogo();
    item.id = UUID.randomUUID();
    item.empresaId = Objects.requireNonNull(empresaId);
    item.tipo = Objects.requireNonNull(tipo);
    item.aplicar(dados, categoria);
    item.ativo = true;
    item.criadoEm = naPrecisaoDoBanco(agora);
    item.atualizadoEm = item.criadoEm;
    return item;
  }

  /** O tipo não entra aqui: ele é imutável (RN-02). */
  public void atualizar(DadosDoItem dados, Categoria novaCategoria, Instant agora) {
    aplicar(dados, novaCategoria);
    atualizadoEm = naPrecisaoDoBanco(agora);
  }

  /** Inativar e reativar são idempotentes. Retorna se houve mudança. */
  public boolean definirAtivo(boolean novoValor, Instant agora) {
    if (ativo == novoValor) {
      return false;
    }
    ativo = novoValor;
    atualizadoEm = naPrecisaoDoBanco(agora);
    return true;
  }

  /** RNF-BE-28: a versão enviada pelo cliente precisa ser a versão atual. */
  public boolean estaNaVersao(long versaoInformada) {
    return versao != null && versao == versaoInformada;
  }

  /** Indica se o item já está associado à categoria informada (ou a nenhuma, se nulo). */
  public boolean estaNaCategoria(UUID categoriaId) {
    return Objects.equals(categoria == null ? null : categoria.getId(), categoriaId);
  }

  private void aplicar(DadosDoItem dados, Categoria novaCategoria) {
    codigo = dados.codigo();
    nome = Objects.requireNonNull(dados.nome());
    descricao = dados.descricao();
    unidade = dados.unidade() == null ? tipo.unidadePadrao() : dados.unidade();
    preco = Objects.requireNonNull(dados.preco());
    categoria = novaCategoria;
  }

  /** DATETIME(6) guarda microssegundos: a resposta mostra o mesmo instante que as consultas. */
  private static Instant naPrecisaoDoBanco(Instant instante) {
    return instante.truncatedTo(ChronoUnit.MICROS);
  }

  public UUID getId() {
    return id;
  }

  public UUID getEmpresaId() {
    return empresaId;
  }

  public Categoria getCategoria() {
    return categoria;
  }

  public TipoItem getTipo() {
    return tipo;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNome() {
    return nome;
  }

  public String getDescricao() {
    return descricao;
  }

  public String getUnidade() {
    return unidade;
  }

  public BigDecimal getPreco() {
    return preco;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public Instant getCriadoEm() {
    return criadoEm;
  }

  public Instant getAtualizadoEm() {
    return atualizadoEm;
  }

  public Long getVersao() {
    return versao;
  }
}
