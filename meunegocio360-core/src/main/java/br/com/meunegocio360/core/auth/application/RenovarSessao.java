package br.com.meunegocio360.core.auth.application;

import br.com.meunegocio360.core.auth.domain.GeradorDeRefreshToken;
import br.com.meunegocio360.core.auth.domain.RefreshToken;
import br.com.meunegocio360.core.auth.domain.RefreshTokenRepository;
import br.com.meunegocio360.core.auth.domain.Usuario;
import br.com.meunegocio360.core.auth.domain.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de renovação de sessão com rotação e detecção de reuso (RN-02, RN-07, RN-08, RN-13).
 */
@Service
public class RenovarSessao {

  private static final Logger log = LoggerFactory.getLogger(RenovarSessao.class);
  private static final int TAMANHO_MAXIMO_TOKEN = 128;

  private final RefreshTokenRepository refreshTokens;
  private final UsuarioRepository usuarios;
  private final GeradorDeRefreshToken geradorDeRefreshToken;
  private final EmissorDeSessao emissorDeSessao;
  private final Clock clock;

  public RenovarSessao(
      RefreshTokenRepository refreshTokens,
      UsuarioRepository usuarios,
      GeradorDeRefreshToken geradorDeRefreshToken,
      EmissorDeSessao emissorDeSessao,
      Clock clock) {
    this.refreshTokens = refreshTokens;
    this.usuarios = usuarios;
    this.geradorDeRefreshToken = geradorDeRefreshToken;
    this.emissorDeSessao = emissorDeSessao;
    this.clock = clock;
  }

  /** A revogação da família precisa ser gravada mesmo com a exceção (sem rollback). */
  @Transactional(noRollbackFor = RefreshTokenInvalidoException.class)
  public SessaoAutenticada executar(String refreshToken) {
    if (refreshToken == null
        || refreshToken.isBlank()
        || refreshToken.length() > TAMANHO_MAXIMO_TOKEN) {
      throw new RefreshTokenInvalidoException();
    }
    Instant agora = clock.instant();
    RefreshToken atual =
        refreshTokens
            .buscarPorHashParaAtualizacao(geradorDeRefreshToken.hash(refreshToken))
            .orElseThrow(RefreshTokenInvalidoException::new);

    if (atual.estaRevogado()) {
      int revogados = refreshTokens.revogarFamilia(atual.getFamiliaId(), agora);
      log.warn(
          "Reuso de refresh token detectado: família revogada usuarioId={} tokensRevogados={}",
          atual.getUsuarioId(),
          revogados);
      throw new RefreshTokenInvalidoException();
    }
    if (atual.expirou(agora)) {
      throw new RefreshTokenInvalidoException();
    }

    Usuario usuario =
        usuarios.findById(atual.getUsuarioId()).filter(u -> u.podeAutenticar(agora)).orElse(null);
    if (usuario == null) {
      refreshTokens.revogarFamilia(atual.getFamiliaId(), agora);
      log.info(
          "Renovação recusada: usuário inativo ou bloqueado usuarioId={}", atual.getUsuarioId());
      throw new RefreshTokenInvalidoException();
    }

    return emissorDeSessao.rotacionar(atual, usuario, agora);
  }
}
