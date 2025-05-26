package dev.wakandaacademy.produdoro.tarefa.application.api;

import java.util.List;
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

    @PatchMapping("/conclui-tarefa/{idTarefa}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void patchConcluiTarefa(@RequestHeader(name = "Authorization",required = true) String token,
                            @PathVariable UUID idTarefa);

    @PatchMapping("edita-tarefa/{idTarefa}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void editaTarefa(@RequestHeader(name = "Authorization",required = true) String token,
                     @PathVariable UUID idTarefa, @RequestBody @Valid TarefaAlteracaoRequest tarefaAlteracaoRequest);

    @GetMapping("/lista-tarefas/{idUsuario}")
    @ResponseStatus(code = HttpStatus.OK)
    List<TarefaUsuarioListResponse> listaTodasTarefasDoUsuario(
            @RequestHeader(name = "Authorization", required = true) String token, @PathVariable UUID idUsuario);
}
