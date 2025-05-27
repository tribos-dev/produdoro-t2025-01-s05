package dev.wakandaacademy.produdoro.tarefa.application.service;

<<<<<<< HEAD
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
=======
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
>>>>>>> bdb5edef62d9fdf753087d3c76e21505980395c9

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
<<<<<<< HEAD

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import dev.wakandaacademy.produdoro.usuario.infra.UsuarioRepositoryMongoDB;
=======
import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
>>>>>>> bdb5edef62d9fdf753087d3c76e21505980395c9
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaUsuarioListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;


import org.springframework.http.HttpStatus;


import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class TarefaApplicationServiceTest {

    //	@Autowired
    @InjectMocks
    TarefaApplicationService tarefaApplicationService;

    //	@MockBean
    @Mock
    TarefaRepository tarefaRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Test
    void deveRetornarIdTarefaNovaCriada() {
        TarefaRequest request = getTarefaRequest();
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }

    @Test
    void deveEditarTarefa(){
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();
        TarefaAlteracaoRequest tarefaAlteracaoRequest = DataHelper.createAlteracaoTarefa();


        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        tarefaApplicationService.editaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), tarefaAlteracaoRequest);
        assertEquals(tarefa.getDescricao(), tarefaAlteracaoRequest.getDescricao());
    }

    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
    }

    @Test
    void ativaTarefaDeveAtivarTarefa() {
        UUID idTarefa = DataHelper.createTarefa().getIdTarefa();
        UUID idUsuario = DataHelper.createUsuario().getIdUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        Usuario usuario = DataHelper.createUsuario();
        String email = "email@gmail.com";
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.ativaTarefa(email, idTarefa);
        verify(tarefaRepository, times(1)).buscaTarefaPorId(idTarefa);
        verify(tarefaRepository, times(1)).desativaTarefaAtiva(idUsuario);
        assertEquals(StatusAtivacaoTarefa.ATIVA, tarefa.getStatusAtivacao());
    }

    @Test
    void ativaTarefaDeveRetornarErro() {
        UUID idTarefaInvalido = UUID.randomUUID();
        String email = "email@gmail.com";
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        APIException ex = assertThrows(APIException.class, () -> {
            tarefaApplicationService.ativaTarefa(email, idTarefaInvalido);
        });
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
    }
}

    void retornarTarefasCadastradasPeloUsuarioLogado() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasDoUsuario(usuario.getIdUsuario())).thenReturn(tarefas);
        List<TarefaUsuarioListResponse> listaTodasTarefasUsuario = tarefaApplicationService
                .listaTodasTarefasDoUsuario(usuario.getEmail(), usuario.getIdUsuario());

        assertEquals(8, listaTodasTarefasUsuario.size());
        verify(tarefaRepository, times(1)).buscaTarefasDoUsuario(usuario.getIdUsuario());
    }

    @Test
    void retornarListaVaziaDoUsuario() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> listaVazia = new ArrayList<>();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasDoUsuario(usuario.getIdUsuario())).thenReturn(listaVazia);
        List<TarefaUsuarioListResponse> listaVaziaDoUsuario = tarefaApplicationService
                .listaTodasTarefasDoUsuario(usuario.getEmail(), usuario.getIdUsuario());

        assertEquals(0, listaVaziaDoUsuario.size());
        verify(tarefaRepository, times(1)).buscaTarefasDoUsuario(usuario.getIdUsuario());
    }

    @Test
    public void lancarExcecaoQuandoUsuarioSolicitarTarefaENaoEstiverLogado() {
        UUID usuarioInexistente = UUID.randomUUID();
        when(usuarioRepository.buscaUsuarioPorId(usuarioInexistente))
                .thenThrow((APIException.build(HttpStatus.BAD_REQUEST, "Usuario não encontrado!")));
        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.listaTodasTarefasDoUsuario("email@exemplo.com", usuarioInexistente);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
        assertEquals("Usuario não encontrado!", exception.getMessage());
        verify(usuarioRepository, times(1)).buscaUsuarioPorId(usuarioInexistente);

    }
        @Test
        void deveExcluirTodasAsTarefasDoUsuario() {
            Usuario usuario = DataHelper.createUsuario();
            List<Tarefa> tarefas = DataHelper.createListTarefa();

            when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
            when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
            when(tarefaRepository.buscaTarefasDoUsuario(usuario.getIdUsuario())).thenReturn(tarefas);
            tarefaApplicationService.limparTodasAsTarefas(usuario.getEmail(), usuario.getIdUsuario());
            verify(tarefaRepository, times(1)).deletaTodasTarefasDoUsuario(tarefas);
        }
    }
