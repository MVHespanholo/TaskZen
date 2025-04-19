package com.mvhespanholo.taskzen.service;

import com.mvhespanholo.taskzen.exception.TarefaNaoEncontradaException;
import com.mvhespanholo.taskzen.model.Tarefa;
import com.mvhespanholo.taskzen.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository repository;

    public Tarefa criarTarefa(Tarefa tarefa) {
        return repository.save(tarefa);
    }

    public Tarefa atualizarTarefa(Long id, Tarefa tarefaAtualizada) {
        // Verifica se a tarefa existe, caso contrário lança a exceção personalizada
        Tarefa tarefa = repository.findById(id)
            .orElseThrow(() -> new TarefaNaoEncontradaException("Tarefa com ID " + id + " não encontrada"));

        // Atualiza os campos da tarefa
        tarefa.setNome(tarefaAtualizada.getNome());
        tarefa.setDescricao(tarefaAtualizada.getDescricao());
        tarefa.setStatus(tarefaAtualizada.getStatus());
        tarefa.setObservacoes(tarefaAtualizada.getObservacoes());
        return repository.save(tarefa);
    }

    public void deletarTarefa(Long id) {
        // Verifica se a tarefa existe antes de deletar
        if (!repository.existsById(id)) {
            throw new TarefaNaoEncontradaException("Tarefa com ID " + id + " não encontrada");
        }
        repository.deleteById(id);
    }

    public List<Tarefa> listarTarefas() {
        return filtrarTarefas(null, null, null);
    }

    public List<Tarefa> filtrarTarefas(String status, String nome, String ordem) {
        Sort sort = ordem != null && ordem.equalsIgnoreCase("asc")
            ? Sort.by("dataCriacao").ascending()
            : Sort.by("dataCriacao").descending();

        if (status != null && nome != null) {
            return repository.findAll(Specification.where((root, query, cb) -> cb.and(
                    cb.equal(root.get("status"), status),
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%")
            )), sort);
        } else if (status != null) {
            return repository.findAll(Specification.where((root, query, cb) ->
                    cb.equal(root.get("status"), status)), sort);
        } else if (nome != null) {
            return repository.findAll(Specification.where((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%")), sort);
        } else {
            return repository.findAll(sort);
        }
    }
}