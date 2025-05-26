package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaUsuarioListResponse;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class TarefaApplicationService implements TarefaService {
    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest) {
        log.info("[inicia] TarefaApplicationService - criaNovaTarefa");
        Tarefa tarefaCriada = tarefaRepository.salva(new Tarefa(tarefaRequest));
        log.info("[finaliza] TarefaApplicationService - criaNovaTarefa");
        return TarefaIdResponse.builder().idTarefa(tarefaCriada.getIdTarefa()).build();
    }
    @Override
    public Tarefa detalhaTarefa(String usuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - detalhaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        log.info("[finaliza] TarefaApplicationService - detalhaTarefa");
        return tarefa;
    }

    @Override
    public void incrementaPomodoro(String emailUsuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - incrementaPomodoro");
        Usuario usuario = usuarioRepository.buscaUsuarioPorEmail(emailUsuario);
        log.info("[usuario] {}", usuario);
        Tarefa tarefa = buscaTarefaOuLancaExpection(idTarefa);
        tarefa.pertenceAoUsuario(usuario);
        usuario.mudaStatusParaFoco(usuario.getIdUsuario());
        tarefa.ativaTarefa(idTarefa);
        tarefa.incrementaPomodoro(tarefa);
        usuario.atualizaStatusUsuario();
        usuarioRepository.salva(usuario);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - incrementaPomodoro");
    }

    public void concluiTarefa(String emailUsuario, UUID idTarefa) {
        log.info("[inicia] TarefaApplicationService - concluiTarefa");
        Tarefa tarefa = detalhaTarefa(emailUsuario, idTarefa);
        log.debug("[tarefa] {}", tarefa);
        tarefa.concluiTarefa();
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - concluiTarefa");
    }
    @Override
    public void editaTarefa(String usuario, UUID idTarefa, TarefaAlteracaoRequest tarefaAlteracaoRequest) {
        log.info("[inicia] TarefaApplicationService - editaTarefa");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        tarefa.pertenceAoUsuario(usuarioPorEmail);
        tarefa.atualiza(tarefaAlteracaoRequest);
        tarefaRepository.salva(tarefa);
        log.info("[finaliza] TarefaApplicationService - editaTarefa");
    }

    @Override
    public List<TarefaUsuarioListResponse> listaTodasTarefasDoUsuario(String email, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - listaTodasTarefasUsuario");
        usuarioRepository.buscaUsuarioPorEmail(email);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        List<Tarefa> tarefas = tarefaRepository.buscaTarefasDoUsuario(idUsuario);
        log.info("[finaliza] TarefaApplicationService - listaTodasTarefasUsuario");
        return TarefaUsuarioListResponse.converte(tarefas);
    }

    private Tarefa buscaTarefaOuLancaExpection(UUID idTarefa) {
        Tarefa tarefa =
                tarefaRepository.buscaTarefaPorId(idTarefa).orElseThrow(() -> APIException.build(HttpStatus.NOT_FOUND, "Tarefa não encontrada!"));
        return tarefa;
    }

}
