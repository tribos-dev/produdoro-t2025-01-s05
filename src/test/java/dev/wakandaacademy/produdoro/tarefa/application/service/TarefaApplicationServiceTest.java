package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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
        when(tarefaRepository.salva(any())).thenReturn(new Tarefa(request));

        TarefaIdResponse response = tarefaApplicationService.criaNovaTarefa(request);

        assertNotNull(response);
        assertEquals(TarefaIdResponse.class, response.getClass());
        assertEquals(UUID.class, response.getIdTarefa().getClass());
    }



    public TarefaRequest getTarefaRequest() {
        TarefaRequest request = new TarefaRequest("tarefa 1", UUID.randomUUID(), null, null, 0);
        return request;
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
        assertEquals("id da tarefa inválido.", ex.getMessage());
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
        assertEquals("Usuário(a) não autorizado(a) para a requisição solicitada", ex.getMessage());
    }



}
