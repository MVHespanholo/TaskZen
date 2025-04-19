package com.mvhespanholo.taskzen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvhespanholo.taskzen.model.Tarefa;
import com.mvhespanholo.taskzen.service.TarefaService;
import com.mvhespanholo.taskzen.exception.TarefaNaoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarefaController.class)
public class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarefaService tarefaService;

    private Tarefa tarefa;

    @BeforeEach
    public void setUp() {
        tarefa = criarTarefaPadrao();
    }

    private Tarefa criarTarefaPadrao() {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setNome("Estudar");
        tarefa.setDescricao("Estudar para a prova");
        tarefa.setStatus("Pendente");
        tarefa.setObservacoes("Capítulo 5");
        return tarefa;
    }

    @Test
    public void testCriarTarefa() throws Exception {
        Mockito.when(tarefaService.criarTarefa(any(Tarefa.class))).thenReturn(tarefa);

        mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tarefa)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Estudar"))
                .andExpect(jsonPath("$.descricao").value("Estudar para a prova"));
    }

    @Test
    public void testCriarTarefaComDadosInvalidos() throws Exception {
        Tarefa tarefaInvalida = new Tarefa(); // Dados ausentes

        mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tarefaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Erro de validação"));
    }

    @Test
    public void testAtualizarTarefa() throws Exception {
        Mockito.when(tarefaService.atualizarTarefa(eq(1L), any(Tarefa.class))).thenReturn(tarefa);

        mockMvc.perform(put("/api/tarefas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tarefa)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.descricao").value("Estudar para a prova"));
    }

    @Test
    public void testAtualizarTarefaInexistente() throws Exception {
        Mockito.when(tarefaService.atualizarTarefa(eq(999L), any(Tarefa.class)))
               .thenThrow(new TarefaNaoEncontradaException("Tarefa não encontrada"));

        mockMvc.perform(put("/api/tarefas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(tarefa)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tarefa não encontrada")));
    }

    @Test
    public void testDeletarTarefa() throws Exception {
        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeletarTarefaInexistente() throws Exception {
        Mockito.doThrow(new TarefaNaoEncontradaException("Tarefa não encontrada"))
               .when(tarefaService).deletarTarefa(999L);

        mockMvc.perform(delete("/api/tarefas/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tarefa não encontrada")));
    }

    @Test
    public void testListarTarefas() throws Exception {
        List<Tarefa> tarefas = Arrays.asList(tarefa);
        Mockito.when(tarefaService.filtrarTarefas(null, null, "desc")).thenReturn(tarefas);

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Estudar"))
                .andExpect(jsonPath("$[0].status").value("Pendente"));
    }

    @Test
    public void testListarTarefasComStatus() throws Exception {
        Mockito.when(tarefaService.filtrarTarefas(eq("Pendente"), eq(null), eq("desc")))
                .thenReturn(List.of(tarefa));

        mockMvc.perform(get("/api/tarefas")
                .param("status", "Pendente"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("Pendente"));
    }

    @Test
    public void testListarTarefasComNome() throws Exception {
        Mockito.when(tarefaService.filtrarTarefas(eq(null), eq("Estudar"), eq("desc")))
                .thenReturn(List.of(tarefa));

        mockMvc.perform(get("/api/tarefas")
                .param("nome", "Estudar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Estudar"));
    }

    @Test
    public void testListarTarefasComTodosParametros() throws Exception {
        Mockito.when(tarefaService.filtrarTarefas(eq("Pendente"), eq("Estudar"), eq("asc")))
                .thenReturn(List.of(tarefa));

        mockMvc.perform(get("/api/tarefas")
                .param("status", "Pendente")
                .param("nome", "Estudar")
                .param("ordem", "asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Estudar"))
                .andExpect(jsonPath("$[0].status").value("Pendente"));
    }
}