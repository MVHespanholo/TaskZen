package com.mvhespanholo.taskzen.controller;

import com.mvhespanholo.taskzen.model.Tarefa;
import com.mvhespanholo.taskzen.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Tarefas", description = "Operações relacionadas à entidade Tarefa")
@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService service;

    @Operation(summary = "Cria uma nova tarefa")
    @PostMapping
    public ResponseEntity<Tarefa> criar(@Valid @RequestBody Tarefa tarefa) {
        return ResponseEntity.ok(service.criarTarefa(tarefa));
    }

    @Operation(summary = "Atualiza uma tarefa existente")
    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizar(@PathVariable Long id, @RequestBody Tarefa tarefa) {
        return ResponseEntity.ok(service.atualizarTarefa(id, tarefa));
    }

    @Operation(summary = "Deleta uma tarefa pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarTarefa(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista todas as tarefas com filtros opcionais")
    @GetMapping
    public ResponseEntity<List<Tarefa>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false, defaultValue = "desc") String ordem) {

        return ResponseEntity.ok(service.filtrarTarefas(status, nome, ordem));
    }
}
