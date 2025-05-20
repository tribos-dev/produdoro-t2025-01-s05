package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface TarefaSpringMongoDBRepository extends MongoRepository<Tarefa, UUID> {
    Optional<Tarefa> findByIdTarefa(UUID idTarefa);
    void deleteAllByIdUsuario(Usuario usuarioPorEmail);
}
