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

    @Override
    public void deletaTarefasConcluidas(String usuarioEmail, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - deletaTarefasConcluidas");
        validaUsuario(usuarioEmail, idUsuario);
        List<Tarefa> tarefas = tarefaRepository.buscaTarefasConcluidas(idUsuario);
        if (tarefas.isEmpty()) {
            throw APIException.build(HttpStatus.CONFLICT, "Usuário não possui nenhuma tarefa concluída!");
        }
        tarefaRepository.deletaTarefasConcluidas(tarefas);
        log.info("[finaliza] TarefaApplicationService - deletaTarefasConcluidas");
    }

    @Override
    public void limparTodasAsTarefas(String usuario, UUID idUsuario) {
        log.info("[inicia] TarefaApplicationService - limparTodasAsTarefas");
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuario);
        log.info("[usuarioPorEmail] {}", usuarioPorEmail);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuarioPorEmail.validaUsuario(idUsuario);
        List<Tarefa> tarefas = tarefaRepository.buscaTarefasDoUsuario(idUsuario);
        verificaSeListaEstaVazia(tarefas);
        verificaQuantidadeTarefas(tarefas);
        tarefaRepository.deletaTodasTarefasDoUsuario(tarefas);
        log.info("[finaliza] TarefaApplicationService - limparTodasAsTarefas");
    }
        private void verificaSeListaEstaVazia(List<Tarefa> tarefas) {
            if (tarefas.isEmpty()) {
                throw APIException.build(HttpStatus.CONFLICT, "Usuário não possui tarefa(s) cadastrada(s)");
            }
        }
        private void verificaQuantidadeTarefas(List<Tarefa> tarefas) {
            if (tarefas.size() < 2) {
                throw APIException.build(HttpStatus.NOT_FOUND, "Usuário não possui quantidade " +
                        "minima de tarefa(as) cadastrada(as)");
            }
    }

    private void validaUsuario(String usuarioEmail, UUID idUsuario) {
        Usuario usuarioPorEmail = usuarioRepository.buscaUsuarioPorEmail(usuarioEmail);
        usuarioRepository.buscaUsuarioPorId(idUsuario);
        usuarioPorEmail.validaUsuario(idUsuario);
    }
}

