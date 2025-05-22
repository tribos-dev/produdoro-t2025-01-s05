package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tarefa")
public interface TarefaAPI {
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    TarefaIdResponse postNovaTarefa(@RequestBody @Valid TarefaRequest tarefaRequest);

    @GetMapping("/{idTarefa}")
    @ResponseStatus(code = HttpStatus.OK)
    TarefaDetalhadoResponse detalhaTarefa(@RequestHeader(name = "Authorization",required = true) String token, 
    		@PathVariable UUID idTarefa);

    @PatchMapping("/{idTarefa}/modifica-posicao-tarefa")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void usuarioModificaOrdemDeUmaTarefa(@RequestHeader(name = "Authorization", required = true) String token,
                                         @PathVariable UUID idTarefa, @RequestParam(required = true, name = "posicao") int novaPosicao);
}
