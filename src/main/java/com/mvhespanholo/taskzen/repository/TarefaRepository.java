package com.mvhespanholo.taskzen.repository;

import com.mvhespanholo.taskzen.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {
    List<Tarefa> findByStatus(String status);
    List<Tarefa> findByNomeContainingIgnoreCase(String nome);
}
