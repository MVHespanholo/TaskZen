package com.mvhespanholo.taskzen.service;

import com.mvhespanholo.taskzen.model.Tarefa;
import com.mvhespanholo.taskzen.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TarefaServiceTest {

    @InjectMocks
    private TarefaService service;

    @Mock
    private TarefaRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveFiltrarTarefasPorStatusENomeComOrdemAscendente() {
        List<Tarefa> tarefas = Arrays.asList(new Tarefa(), new Tarefa());
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(tarefas);

        List<Tarefa> resultado = service.filtrarTarefas("Pendente", "Estudar", "asc");

        assertEquals(2, resultado.size());
        verify(repository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void deveFiltrarApenasPorStatus() {
        List<Tarefa> tarefas = Arrays.asList(new Tarefa());
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(tarefas);

        List<Tarefa> resultado = service.filtrarTarefas("Concluído", null, null);

        assertEquals(1, resultado.size());
        verify(repository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void deveFiltrarApenasPorNome() {
        List<Tarefa> tarefas = Arrays.asList(new Tarefa(), new Tarefa(), new Tarefa());
        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(tarefas);

        List<Tarefa> resultado = service.filtrarTarefas(null, "trabalho", null);

        assertEquals(3, resultado.size());
        verify(repository).findAll(any(Specification.class), any(Sort.class));
    }

    @Test
    void deveListarTodasAsTarefasSemFiltroComOrdemDescendente() {
        List<Tarefa> tarefas = Arrays.asList(new Tarefa(), new Tarefa());
        when(repository.findAll(any(Sort.class))).thenReturn(tarefas);

        List<Tarefa> resultado = service.filtrarTarefas(null, null, "desc");

        assertEquals(2, resultado.size());
        verify(repository).findAll(any(Sort.class));
    }

    @Test
    void deveListarTodasAsTarefasQuandoSemFiltroENemOrdem() {
        List<Tarefa> tarefas = Arrays.asList(new Tarefa());
        when(repository.findAll(any(Sort.class))).thenReturn(tarefas);

        List<Tarefa> resultado = service.filtrarTarefas(null, null, null);

        assertEquals(1, resultado.size());
        verify(repository).findAll(any(Sort.class));
    }
}
