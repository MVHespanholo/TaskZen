package com.mvhespanholo.taskzen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvhespanholo.taskzen.model.Tarefa;
import com.mvhespanholo.taskzen.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TarefaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TarefaRepository tarefaRepository;

    @BeforeEach
    void limparBase() {
        tarefaRepository.deleteAll();
    }

    @Test
    public void testCriarTarefa_Integracao() throws Exception {
        Tarefa tarefa = new Tarefa();
        tarefa.setNome("Tarefa Integração");
        tarefa.setDescricao("Testando integração");
        tarefa.setStatus("Pendente");
        tarefa.setObservacoes("Nenhuma");

        mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Tarefa Integração"));
    }

    @Test
    public void testListarTarefas_Integracao() throws Exception {
        Tarefa tarefa = new Tarefa();
        tarefa.setNome("Listar");
        tarefa.setDescricao("Tarefa para listagem");
        tarefa.setStatus("Pendente");
        tarefa.setObservacoes("Listagem");

        mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Listar"));
    }

    @Test
    public void testAtualizarTarefa_Integracao() throws Exception {
        Tarefa tarefa = new Tarefa();
        tarefa.setNome("Atualizar");
        tarefa.setDescricao("Tarefa a ser atualizada");
        tarefa.setStatus("Pendente");
        tarefa.setObservacoes("Atualização");

        String response = mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
                .andReturn().getResponse().getContentAsString();

        Tarefa tarefaCriada = objectMapper.readValue(response, Tarefa.class);
        tarefaCriada.setDescricao("Descrição atualizada");

        mockMvc.perform(put("/api/tarefas/" + tarefaCriada.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefaCriada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Descrição atualizada"));
    }

    @Test
    public void testDeletarTarefa_Integracao() throws Exception {
        Tarefa tarefa = new Tarefa();
        tarefa.setNome("Deletar");
        tarefa.setDescricao("Tarefa a ser deletada");
        tarefa.setStatus("Pendente");
        tarefa.setObservacoes("Delete");

        String response = mockMvc.perform(post("/api/tarefas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarefa)))
                .andReturn().getResponse().getContentAsString();

        Tarefa tarefaCriada = objectMapper.readValue(response, Tarefa.class);

        mockMvc.perform(delete("/api/tarefas/" + tarefaCriada.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + tarefaCriada.getId() + ")]").doesNotExist());
    }

    @Test
    public void testListarTarefas_FiltroPorStatus() throws Exception {
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setNome("Estudar");
        tarefa1.setDescricao("Estudar Spring");
        tarefa1.setStatus("Pendente");
        tarefa1.setObservacoes("Sem urgência");

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setNome("Exercício");
        tarefa2.setDescricao("Fazer caminhada");
        tarefa2.setStatus("Concluída");
        tarefa2.setObservacoes("30 minutos");

        tarefaRepository.saveAll(List.of(tarefa1, tarefa2));

        mockMvc.perform(get("/api/tarefas?status=Pendente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Estudar"));
    }

    @Test
    public void testListarTarefas_FiltroPorNome() throws Exception {
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setNome("Estudar");
        tarefa1.setDescricao("Estudar Spring");
        tarefa1.setStatus("Pendente");
        tarefa1.setObservacoes("Sem urgência");

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setNome("Exercício");
        tarefa2.setDescricao("Fazer caminhada");
        tarefa2.setStatus("Concluída");
        tarefa2.setObservacoes("30 minutos");

        tarefaRepository.saveAll(List.of(tarefa1, tarefa2));

        mockMvc.perform(get("/api/tarefas?nome=Estudar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Estudar"));
    }

}
