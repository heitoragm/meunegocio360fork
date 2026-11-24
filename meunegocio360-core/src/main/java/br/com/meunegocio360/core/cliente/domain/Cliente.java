package br.com.meunegocio360.core.cliente.domain;

import br.com.meunegocio360.core.shared.domain.TipoPessoa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Cliente da empresa ativa (RF-BE-003). Guarda apenas o id da empresa, sem relação JPA com a
 * feature {@code empresa} (RNF-BE-05). Não sobrescreve {@code toString()}, para que documento,
 * e-mail e telefone nunca acabem em logs (RNF-025).
 */
@Entity
@Table(name = "cliente")
public class Cliente {

  @Id private UUID id;

  @Column(name = "empresa_id", nullable = false, updatable = false)
  private UUID empresaId;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "tipo_pessoa", nullable = false, length = 10)
  private TipoPessoa tipoPessoa;

  @Column(nullable = false, length = 150)
  private String nome;

  @Column(nullable = false, length = 14)
  private String documento;

  @Column(length = 160)
  private String email;

  @Column(length = 11)
  private String telefone;

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

  protected Cliente() {}

  /** Novo cliente, já ativo, na empresa informada pelo servidor (RN-01). */
  public static Cliente novo(UUID empresaId, DadosDoCliente dados, Instant agora) {
    Cliente cliente = new Cliente();
    cliente.id = UUID.randomUUID();
    cliente.empresaId = Objects.requireNonNull(empresaId);
    cliente.aplicar(dados);
    cliente.ativo = true;
    cliente.criadoEm = naPrecisaoDoBanco(agora);
    cliente.atualizadoEm = cliente.criadoEm;
    return cliente;
  }

  /** RN-09: tipo de pessoa e documento também podem ser corrigidos. */
  public void atualizar(DadosDoCliente dados, Instant agora) {
    aplicar(dados);
    atualizadoEm = naPrecisaoDoBanco(agora);
  }

  /** RN-07: inativar e reativar são idempotentes. Retorna se houve mudança. */
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

  private void aplicar(DadosDoCliente dados) {
    tipoPessoa = Objects.requireNonNull(dados.tipoPessoa());
    nome = Objects.requireNonNull(dados.nome());
    documento = Objects.requireNonNull(dados.documento());
    email = dados.email();
    telefone = dados.telefone();
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

  public TipoPessoa getTipoPessoa() {
    return tipoPessoa;
  }

  public String getNome() {
    return nome;
  }

  public String getDocumento() {
    return documento;
  }

  public String getEmail() {
    return email;
  }

  public String getTelefone() {
    return telefone;
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
