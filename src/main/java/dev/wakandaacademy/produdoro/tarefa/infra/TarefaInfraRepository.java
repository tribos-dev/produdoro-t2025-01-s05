package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.Tarefa;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;


import org.springframework.data.mongodb.core.query.Update;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Log4j2
@RequiredArgsConstructor
public class TarefaInfraRepository implements TarefaRepository {

    private final TarefaSpringMongoDBRepository tarefaSpringMongoDBRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Tarefa salva(Tarefa tarefa) {
        log.info("[inicia] TarefaInfraRepository - salva");
        try {
            tarefaSpringMongoDBRepository.save(tarefa);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Tarefa já cadastrada", e);
        }
        log.info("[finaliza] TarefaInfraRepository - salva");
        return tarefa;
    }

    @Override
    public Optional<Tarefa> buscaTarefaPorId(UUID idTarefa) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefaPorId");
        Optional<Tarefa> tarefaPorId = tarefaSpringMongoDBRepository.findByIdTarefa(idTarefa);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefaPorId");
        return tarefaPorId;
    }

    @Override
    public void desativaTarefaAtiva(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - desativaTarefaAtiva");
        Query query = new Query(Criteria.where("statusAtivacao").is("ATIVA").and("idUsuario").is(idUsuario));
        Update update = new Update().set("statusAtivacao", "INATIVA");
        mongoTemplate.updateMulti(query, update, Tarefa.class);
        log.info("[inicia] TarefaInfraRepository - desativaTarefaAtiva");
    }

    public void limpaTarefas(Usuario usuarioPorEmail) {
        log.info("[inicia] TarefaInfraRepository - limpaTarefas");
        try {
            tarefaSpringMongoDBRepository.deleteAllByIdUsuario(usuarioPorEmail);
        } catch (DataIntegrityViolationException e) {
            throw APIException.build(HttpStatus.BAD_REQUEST, "Usuário não encontrado", e);
        }
        log.info("[finaliza] TarefaInfraRepository - limpaTarefas");

    }

    @Override
    public List<Tarefa> buscaTarefasDoUsuario(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasPorUsuario");
        List<Tarefa> tarefas = tarefaSpringMongoDBRepository.findAllTarefaByidUsuario(idUsuario);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasPorUsuario");
        return tarefas;
    }

    @Override
    public void deletaTodasTarefasDoUsuario(List<Tarefa> tarefas) {
        log.info("[inicia] TarefaInfraRepository - deletaTodasTarefasDoUsuario");
        tarefaSpringMongoDBRepository.deleteAll(tarefas);
        log.info("[finaliza] TarefaInfraRepository - deletaTodasTarefasDoUsuario");

    }
}
