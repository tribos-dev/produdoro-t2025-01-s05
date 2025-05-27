package dev.wakandaacademy.produdoro.tarefa.application.repository;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository {

    Tarefa salva(Tarefa tarefa);
    Optional<Tarefa> buscaTarefaPorId(UUID idTarefa);
<<<<<<< HEAD
    void desativaTarefaAtiva(UUID idUsuario);
=======
    void limpaTarefas(Usuario usuarioPorEmail);
    List<Tarefa> buscaTarefasDoUsuario(UUID idUsuario);
    void deletaTodasTarefasDoUsuario(List<Tarefa> tarefas);
>>>>>>> bdb5edef62d9fdf753087d3c76e21505980395c9
}
