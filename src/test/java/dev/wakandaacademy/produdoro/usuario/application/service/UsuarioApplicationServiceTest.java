package dev.wakandaacademy.produdoro.usuario.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.credencial.application.service.CredencialService;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.pomodoro.application.service.PomodoroService;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {
    @InjectMocks
    private UsuarioApplicationService usuarioApplicationService;

    @Mock
    private PomodoroService pomodoroService;

    @Mock
    private CredencialService credencialService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void deveMudarStatusParaPausaLongaComSucesso() {
        Usuario usuario = DataHelper.createUsuarioFoco();
        String email = usuario.getEmail();
        UUID idUsuario = usuario.getIdUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaPausaLonga(email, idUsuario);
        assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus(), "O status deve mudar para PAUSA_LONGA");
        verify(usuarioRepository).buscaUsuarioPorEmail(email);
        verify(usuarioRepository).buscaUsuarioPorId(idUsuario);
        verify(usuarioRepository).salva(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoTemIdValido() {
        Usuario usuario = DataHelper.createUsuarioFoco();
        UUID idInvalido = UUID.randomUUID();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(idInvalido)).thenReturn(null);
        APIException ex = assertThrows(APIException.class, () -> {
            usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idInvalido);
        });
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("Credencial de autenticação não é válida", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioJaEstaEmPausaLonga() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idUsuario = usuario.getIdUsuario();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(idUsuario)).thenReturn(usuario);
        APIException ex = assertThrows(APIException.class, () -> {
            usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idUsuario);
        });
        assertEquals(HttpStatus.CONFLICT, ex.getStatusException());
        assertEquals("Usuário já esta em PAUSA LONGA!", ex.getMessage());
    }

    @Test
    void mudaStatusParaFoco() {
        Usuario usuario = DataHelper.createUsuario();
        when(usuarioRepository.salva(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuario.getIdUsuario());
        verify(usuarioRepository, times(1)).salva(usuario);
        assertEquals(StatusUsuario.FOCO, usuario.getStatus());
    }

}

