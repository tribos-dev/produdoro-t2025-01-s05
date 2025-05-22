package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioCriadoResponse;
import dev.wakandaacademy.produdoro.usuario.application.api.UsuarioNovoRequest;

import java.util.UUID;

public interface UsuarioService {
	UsuarioCriadoResponse criaNovoUsuario(UsuarioNovoRequest usuarioNovo);
    UsuarioCriadoResponse buscaUsuarioPorId(UUID idUsuario);
    void mudaStatusParaPausaLonga(String emailUsuario, UUID idUsuario);
<<<<<<< HEAD
    void mudaStatusParaPausaCurta(String emailUsuario, UUID idUsuario);
=======
    void mudaStatusParaFoco(String usuario, UUID idUsuario);
>>>>>>> c73035a5d11f2f0fd631eb1eca7e9253f2794ba9
}
