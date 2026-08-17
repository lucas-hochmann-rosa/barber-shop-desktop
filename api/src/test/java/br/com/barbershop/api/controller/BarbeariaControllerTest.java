package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.BarbeariaDTO;
import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.InMemoryBarbeiroRepository;
import br.com.barbershop.api.helper.InMemoryServicoRepository;
import br.com.barbershop.api.helper.InMemoryUsuarioRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.SetupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BarbeariaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Barbearia barbearia;
    private BarbeariaService barbeariaService;
    private SetupService setupService;

    @BeforeEach
    void setUp() {
        objectMapper = TestUtils.createObjectMapper();

        barbearia = new Barbearia();
        barbearia.setId(1);
        barbearia.setNome("Barbershop Central");
        barbearia.setCep("89800000");
        barbearia.setHorarioAbertura(LocalTime.of(8, 0));
        barbearia.setHorarioFechamento(LocalTime.of(20, 0));

        InMemoryBarbeariaRepository repo = new InMemoryBarbeariaRepository(barbearia);
        barbeariaService = new BarbeariaService(repo);
        setupService = new SetupService(repo, new InMemoryUsuarioRepository(), new InMemoryServicoRepository(), new InMemoryBarbeiroRepository());

        BarbeariaController controller = new BarbeariaController(barbeariaService, setupService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/barbearia deve retornar dados da barbearia cadastrada")
    void buscarBarbearia() throws Exception {
        mockMvc.perform(get("/api/barbearia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Barbershop Central"))
                .andExpect(jsonPath("$.cep").value("89800000"));
    }

    @Test
    @DisplayName("PUT /api/barbearia deve atualizar dados com sucesso")
    void atualizarBarbearia() throws Exception {
        BarbeariaDTO dto = new BarbeariaDTO(barbearia);
        dto.setNome("Barbershop Atualizada");

        mockMvc.perform(put("/api/barbearia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));
    }
}
