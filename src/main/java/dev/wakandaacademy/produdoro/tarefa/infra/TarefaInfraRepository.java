package dev.wakandaacademy.produdoro.tarefa.infra;

import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.tarefa.application.repository.TarefaRepository;
import dev.wakandaacademy.produdoro.tarefa.domain.StatusTarefa;
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
import org.springframework.web.bind.annotation.PathVariable;


import org.springframework.data.mongodb.core.query.Update;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Override
    public List<Tarefa> buscaTarefasConcluidas(UUID idUsuario) {
        log.info("[inicia] TarefaInfraRepository - buscaTarefasConcluidas");
        List <Tarefa> tarefasConcluidas = tarefaSpringMongoDBRepository.findAllByIdUsuarioAndStatus(idUsuario, StatusTarefa.CONCLUIDA);
        log.info("[finaliza] TarefaInfraRepository - buscaTarefasConcluidas");
        return tarefasConcluidas;
    }

    @Override
    public void deletaTarefasConcluidas(List<Tarefa> tarefas) {
        log.info("[inicia] TarefaInfraRepository - deletaTarefasConcluidas");
        tarefaSpringMongoDBRepository.deleteAll(tarefas);
        log.info("[finaliza] TarefaInfraRepository - deletaTarefasConcluidas");
    }

    @Override
    public void modificaOrdemTarefa(Tarefa tarefa, List<Tarefa> tarefasUsuario, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - modificaOrdemTarefa");
        int menorPosicao = (novaPosicao < 0) ? 0 : Math.min(tarefa.getPosicao(), novaPosicao);
        int maiorPosicao = (novaPosicao >= (tarefasUsuario.size())) ? tarefasUsuario.size() - 1
                : Math.max(tarefa.getPosicao(), novaPosicao);
        validaNovaPosicao(tarefasUsuario.size(), tarefa.getPosicao(), novaPosicao);
        novaPosicao = Math.max(0, Math.min(novaPosicao, tarefasUsuario.size() - 1));
        salvaVariasTarefas(tarefasUsuario, tarefa.getPosicao(), novaPosicao, menorPosicao, maiorPosicao);
        log.info("[finaliza] TarefaInfraRepository - modificaOrdemTarefa");
    }

    @Override
    public int contarTarefas(UUID idUsuario) {
        return tarefaSpringMongoDBRepository.countTarefaByIdUsuario(idUsuario);

    }

    private void validaNovaPosicao(int tamanhoLista, int posicaoOrigem, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - validaNovaPosicao");
        if (!(novaPosicao >= 0 && novaPosicao < tamanhoLista)){
            throw APIException.build(HttpStatus.BAD_REQUEST, "Posição inválida.");
        }
        log.info("[finaliza] TarefaInfraRepository - validaNovaPosicao");
    }

    private void salvaVariasTarefas(List<Tarefa> tarefas, int origem, int destino, int menorPosicao, int maiorPosicao) {
        log.info("[inicia] TarefaInfraRepository - salvaVariasTarefas");
        List<Tarefa> tarefasAtualizadas = IntStream.range(menorPosicao, maiorPosicao)
                .mapToObj(posicao -> {
                    return novaPosicaoTarefa(tarefas, origem, destino, posicao);
                })
                .collect(Collectors.toList());
        log.info("[finaliza] TarefaInfraRepository - salvaVariasTarefas");
        tarefaSpringMongoDBRepository.saveAll(tarefasAtualizadas);
    }

    private Tarefa novaPosicaoTarefa(List<Tarefa> tarefas, int origem, int destino, int posicao) {
        log.info("[inicia] TarefaInfraRepository - novaPosicaoTarefa");
        Tarefa tarefa = destino < origem  ? atualizaTarefa(tarefas.get(posicao), posicao + 1) :  atualizaTarefa(tarefas.get(posicao + 1), posicao);
        log.info("[finaliza] TarefaInfraRepository - novaPosicaoTarefa");
        return tarefa;
    }

    private Tarefa atualizaTarefa(Tarefa tarefa, int novaPosicao) {
        log.info("[inicia] TarefaInfraRepository - atualizaTarefa");
        tarefa.alteraPosicao(novaPosicao);
        log.info("[finaliza] TarefaInfraRepository - atualizaTarefa");
        return tarefa;
    }

    @Override
    public void ajustaPosicaoDasTarefas(List<Tarefa> tarefasDoUsuario) {
        log.info("[inicia] TarefaInfraRepository - ajustaPosicaoDasTarefas");
        int tamanhoDaLista = tarefasDoUsuario.size();
        List<Tarefa> tarefasAjustadas = IntStream.range(0, tamanhoDaLista)
                .mapToObj(i -> this.ajustaPosicaoDaTarefa(tarefasDoUsuario.get(i), i))
                .collect(Collectors.toList());
        tarefaSpringMongoDBRepository.saveAll(tarefasAjustadas);
        log.info("[finaliza] TarefaInfraRepository - ajustaPosicaoDasTarefas");
    }

    private Tarefa ajustaPosicaoDaTarefa(Tarefa tarefa, int novaPosicao) {
        tarefa.ajustaPosicao(novaPosicao);
        return tarefa;
    }
}
