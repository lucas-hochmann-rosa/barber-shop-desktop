package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.BarbeiroDTO;
import br.com.barbershop.api.dto.ServicoDTO;
import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.InMemoryBarbeiroRepository;
import br.com.barbershop.api.helper.InMemoryServicoRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.CatalogoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CatalogoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CatalogoService catalogoService;
    private BarbeariaService barbeariaService;

    @BeforeEach
    void setUp() {
        objectMapper = TestUtils.createObjectMapper();
        barbeariaService = new BarbeariaService(new InMemoryBarbeariaRepository());
        catalogoService = new CatalogoService(new InMemoryServicoRepository(), new InMemoryBarbeiroRepository());

        CatalogoController controller = new CatalogoController(catalogoService, barbeariaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setConversionService(TestUtils.createConversionService())
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/servicos deve listar servicos da barbearia")
    void listarServicos() throws Exception {
        mockMvc.perform(get("/api/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Corte Masculino"))
                .andExpect(jsonPath("$[0].preco").value(45.00));
    }

    @Test
    @DisplayName("POST /api/servicos deve cadastrar novo servico")
    void criarServico() throws Exception {
        ServicoDTO dto = new ServicoDTO();
        dto.setBarbeariaId(1);
        dto.setNome("Corte Degradê");
        dto.setPreco(new BigDecimal("50.00"));
        dto.setDuracaoMinutos(40);

        mockMvc.perform(post("/api/servicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Corte Degradê"));
    }

    @Test
    @DisplayName("GET /api/barbeiros deve listar barbeiros da barbearia")
    void listarBarbeiros() throws Exception {
        mockMvc.perform(get("/api/barbeiros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Carlos Silva"));
    }

    @Test
    @DisplayName("DELETE /api/servicos/{id} deve remover servico")
    void excluirServico() throws Exception {
        mockMvc.perform(delete("/api/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucesso").value(true));
    }
}
