package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaUsuarioListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusAtivacaoTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


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
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request, 0));

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
    void retornarTarefasCadastradasPeloUsuarioLogado() {
        Usuario usuario = DataHelper.createUsuario();
        List<Tarefa> tarefas = DataHelper.createListTarefa();

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefasDoUsuario(usuario.getIdUsuario())).thenReturn(tarefas);
        List<TarefaUsuarioListResponse> listaTodasTarefasUsuario = tarefaApplicationService
                .listaTodasTarefasDoUsuario(usuario.getEmail(), usuario.getIdUsuario());
    }

    @Test
    void modificaOrdemDeUmaTarefa() {
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        int novaPosicao = 1;

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.buscaTarefasDoUsuario(any())).thenReturn(DataHelper.createListTarefa());

        tarefaApplicationService.usuarioModificaOrdemDeUmaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), novaPosicao);
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(any());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(any());
    }

    @Test
    void modificaOrdemDeUmaTarefaQuandoIdtarefaForInvalidoDeveLancarExcecao() {
        Usuario usuario = DataHelper.createUsuario();
        UUID idtarefa = UUID.randomUUID();
        int novaPosicao = 1;

        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.empty());
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);

        APIException ex = assertThrows(APIException.class, () -> tarefaApplicationService
                .usuarioModificaOrdemDeUmaTarefa(usuario.getEmail(), idtarefa, novaPosicao));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusException());
        assertEquals("Tarefa não encontrada!", ex.getMessage());
    }

    @Test
    void modificaOrdemDeUmaTarefaQuandoTarefaNaoPertenceAoUsuarioDeveLancarExcecao() {
        Usuario usuario = DataHelper.createUsuarioInvalido();
        Tarefa tarefa = DataHelper.createTarefa();
        int novaPosicao = 1;

        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));

        APIException ex = assertThrows(APIException.class, () -> tarefaApplicationService
                .usuarioModificaOrdemDeUmaTarefa(usuario.getEmail(), tarefa.getIdTarefa(), novaPosicao));
        verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(any());
        verify(tarefaRepository, times(1)).buscaTarefaPorId(any());

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
        assertEquals("Usuário não é dono da Tarefa solicitada!", ex.getMessage());
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
    public void deveConcluiTarefa(){
        Usuario usuario = DataHelper.createUsuario();
        Tarefa tarefa = DataHelper.createTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(any())).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.concluiTarefa(usuario.getEmail(),tarefa.getIdTarefa());
        assertEquals(tarefa.getStatus(), StatusTarefa.CONCLUIDA);
    }

    @Test
    void deveIncrementarPomodoroComSucesso() {
        String email = "email@email.com";
        UUID idTarefa = UUID.fromString("06fb5521-9d5a-461a-82fb-e67e3bedc6eb");
        Usuario usuario = DataHelper.createUsuarioConfigurado();
        Tarefa tarefa = DataHelper.createTarefa();
        when(usuarioRepository.buscaUsuarioPorEmail(email)).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(idTarefa)).thenReturn(Optional.of(tarefa));
        tarefaApplicationService.incrementaPomodoro(email, idTarefa);
        verify(usuarioRepository).salva(usuario);
        verify(tarefaRepository).salva(tarefa);
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoEncontrada() {
        Usuario usuario = DataHelper.createUsuarioConfigurado();
        UUID idTarefaInvalido = UUID.randomUUID();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(idTarefaInvalido)).thenReturn(Optional.empty());
        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), idTarefaInvalido);
        });
        assertEquals("Tarefa não encontrada!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusException());
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoPertenceAoUsuario() {
        Usuario usuario = DataHelper.createUsuarioConfigurado();
        Tarefa tarefaDeOutroUsuario = Tarefa.builder()
                .idTarefa(UUID.randomUUID())
                .descricao("tarefa de outro")
                .idUsuario(UUID.randomUUID()) // ID diferente
                .contagemPomodoro(1)
                .status(StatusTarefa.A_FAZER)
                .statusAtivacao(StatusAtivacaoTarefa.INATIVA)
                .build();
        when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
        when(tarefaRepository.buscaTarefaPorId(tarefaDeOutroUsuario.getIdTarefa())).thenReturn(Optional.of(tarefaDeOutroUsuario));
        APIException exception = assertThrows(APIException.class, () -> {
            tarefaApplicationService.incrementaPomodoro(usuario.getEmail(), tarefaDeOutroUsuario.getIdTarefa());
        });
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusException());
    }

    void deveExcluirTodasAsTarefasDoUsuario() {
            Usuario usuario = DataHelper.createUsuario();
            List<Tarefa> tarefas = DataHelper.createListTarefa();
            when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
            when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
            when(tarefaRepository.buscaTarefasDoUsuario(usuario.getIdUsuario())).thenReturn(tarefas);
            tarefaApplicationService.limparTodasAsTarefas(usuario.getEmail(), usuario.getIdUsuario());
            verify(tarefaRepository, times(1)).deletaTodasTarefasDoUsuario(tarefas);
    }
        @Test
        void deveDeletarTarefasConcluidas() {
            Usuario usuario = DataHelper.createUsuario();
            List<Tarefa> tarefasConcluidas = DataHelper.createListTarefasConcluidas();
            when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
            when(tarefaRepository.buscaTarefasConcluidas(any())).thenReturn(tarefasConcluidas);
            tarefaApplicationService.deletaTarefasConcluidas(usuario.getEmail(), usuario.getIdUsuario());
            verify(tarefaRepository, times(1)).deletaTarefasConcluidas(tarefasConcluidas);
        }
}
