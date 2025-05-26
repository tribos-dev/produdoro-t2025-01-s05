package dev.wakandaacademy.produdoro.tarefa.application.service;

import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaAlteracaoRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaIdResponse;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaRequest;
import dev.wakandaacademy.produdoro.tarefa.application.api.TarefaUsuarioListResponse;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;

import java.util.List;
import java.util.UUID;
public interface TarefaService {
    TarefaIdResponse criaNovaTarefa(TarefaRequest tarefaRequest);
    Tarefa detalhaTarefa(String usuario, UUID idTarefa);
    void editaTarefa(String usuario, UUID idTarefa, TarefaAlteracaoRequest tarefaAlteracaoRequest);
    List<TarefaUsuarioListResponse> listaTodasTarefasDoUsuario(String email, UUID idUsuario);
    void limparTodasAsTarefas(String usuario, UUID idUsuario);
}
