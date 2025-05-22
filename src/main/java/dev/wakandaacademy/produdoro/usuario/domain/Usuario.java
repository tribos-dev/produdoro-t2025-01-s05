package dev.wakandaacademy.produdoro.usuario.domain;

import java.util.UUID;

import javax.validation.constraints.Email;

import dev.wakandaacademy.produdoro.handler.APIException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dev.wakandaacademy.produdoro.pomodoro.domain.ConfiguracaoPadrao;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@Document(collection = "Usuario")
public class Usuario {
	@Id
	private UUID idUsuario;
	@Email
	@Indexed(unique = true)
	private String email;
	private ConfiguracaoUsuario configuracao;
	@Builder.Default
	private StatusUsuario status = StatusUsuario.FOCO;
	@Builder.Default
	private Integer quantidadePomodorosPausaCurta = 0;

	public Usuario(UsuarioNovoRequest usuarioNovo, ConfiguracaoPadrao configuracaoPadrao) {
		this.idUsuario = UUID.randomUUID();
		this.email = usuarioNovo.getEmail();
		this.status = StatusUsuario.FOCO;
		this.configuracao = new ConfiguracaoUsuario(configuracaoPadrao);
	}

	public void validaUsuario(UUID idUsuario) {
		if (!this.idUsuario.equals(idUsuario)) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida");
		}
	}

	public void mudaStatusParaPausaLonga(UUID idUsuario) {
		validaUsuario(idUsuario);
		verificaSeJaEstaEmPausaLonga();
		iniciaPausaLonga();
	}

	private void iniciaPausaLonga() {
		this.status = StatusUsuario.PAUSA_LONGA;
	}

	private void verificaSeJaEstaEmPausaLonga() {
		if (this.status.equals(StatusUsuario.PAUSA_LONGA)) {
			throw APIException.build(HttpStatus.CONFLICT, "Usuário já esta em PAUSA LONGA!");
		}
	}

<<<<<<< HEAD
	public void mudaStatusParaPausaCurta(UUID idUsuario) {
		pertenceAoUsuario(idUsuario);
		verificaSeJaEstaEmPausaCurta();
		iniciaPausaCurta();
	}

	private void iniciaPausaCurta() {
		this.status = StatusUsuario.PAUSA_CURTA;
	}

	private void pertenceAoUsuario(UUID idUsuario) {
		if (!this.idUsuario.equals(idUsuario)) {
			throw APIException.build(HttpStatus.UNAUTHORIZED, "Credencial de autenticação não é válida.");
		}
	}

	private void verificaSeJaEstaEmPausaCurta() {
		if (this.status.equals(StatusUsuario.PAUSA_CURTA)) {
			throw APIException.build(HttpStatus.CONFLICT, "Usuário já esta em PAUSA CURTA!");
		}
	}
}
=======
    public void mudaStatusParaFoco(UUID idUsuario) {
		validaUsuario(idUsuario);
		validaSeUsuarioJaEstaEmFoco();
		this.status = StatusUsuario.FOCO;

    }

	private void validaSeUsuarioJaEstaEmFoco() {
		if (this.status.equals(StatusUsuario.FOCO)) {
			throw APIException.build(HttpStatus.BAD_REQUEST,"Usuário já está em FOCO");
		}
	}
}
>>>>>>> c73035a5d11f2f0fd631eb1eca7e9253f2794ba9
