package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
    void limpaTarefas(Usuario usuarioPorEmail);
    List<Tarefa> buscaTarefasDoUsuario(UUID idUsuario);
    void deletaTodasTarefasDoUsuario(List<Tarefa> tarefas);

    List<Tarefa> buscaTarefasConcluidas(UUID idUsuario);

    void deletaTarefasConcluidas(List<Tarefa> tarefas);
}
