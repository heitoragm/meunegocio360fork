package br.com.meunegocio360.core.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Categoria do catálogo (RF-BE-005, RN-06): nome único na empresa, sem distinguir maiúsculas e
 * acentos. Inativar uma categoria não altera os itens que a usam.
 */
@Entity
@Table(name = "categoria")
public class Categoria {

  @Id private UUID id;

  @Column(name = "empresa_id", nullable = false, updatable = false)
  private UUID empresaId;

  @Column(nullable = false, length = 80)
  private String nome;

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

  protected Categoria() {}

  public static Categoria nova(UUID empresaId, String nome, Instant agora) {
    Categoria categoria = new Categoria();
    categoria.id = UUID.randomUUID();
    categoria.empresaId = Objects.requireNonNull(empresaId);
    categoria.nome = Objects.requireNonNull(nome);
    categoria.ativo = true;
    categoria.criadoEm = naPrecisaoDoBanco(agora);
    categoria.atualizadoEm = categoria.criadoEm;
    return categoria;
  }

  public void renomear(String novoNome, Instant agora) {
    nome = Objects.requireNonNull(novoNome);
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

  public String getNome() {
    return nome;
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
