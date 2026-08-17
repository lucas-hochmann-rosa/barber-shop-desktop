package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.AgendamentoRequest;
import br.com.barbershop.api.helper.InMemoryAgendamentoRepository;
import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.InMemoryBarbeiroRepository;
import br.com.barbershop.api.helper.InMemoryClienteRepository;
import br.com.barbershop.api.helper.InMemoryServicoRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.StatusAgendamento;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.CatalogoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgendaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private InMemoryAgendamentoRepository agendamentoRepo;
    private AgendaService agendaService;
    private BarbeariaService barbeariaService;
    private CatalogoService catalogoService;

    @BeforeEach
    void setUp() {
        objectMapper = TestUtils.createObjectMapper();
        agendamentoRepo = new InMemoryAgendamentoRepository();
        agendaService = new AgendaService(agendamentoRepo, new InMemoryClienteRepository());
        barbeariaService = new BarbeariaService(new InMemoryBarbeariaRepository());
        catalogoService = new CatalogoService(new InMemoryServicoRepository(), new InMemoryBarbeiroRepository());

        AgendaController controller = new AgendaController(agendaService, barbeariaService, catalogoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setConversionService(TestUtils.createConversionService())
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/agenda/hoje deve listar agendamentos com classificacao")
    void listarHoje() throws Exception {
        Agendamento a = new Agendamento(1, 1, 1, "Cliente Teste", "49999999999", LocalDateTime.now().plusHours(1), OrigemContato.WHATSAPP, StatusAgendamento.AGENDADO);
        a.setServicoNome("Corte Masculino");
        a.setBarbeiroNome("Carlos Silva");
        agendamentoRepo.inserir(a);

        mockMvc.perform(get("/api/agenda/hoje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteNome").value("Cliente Teste"))
                .andExpect(jsonPath("$[0].classificacao").exists());
    }

    @Test
    @DisplayName("POST /api/agenda deve criar agendamento se nao houver conflito")
    void criarAgendamentoSucesso() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusHours(3);
        AgendamentoRequest req = new AgendamentoRequest();
        req.setBarbeariaId(1);
        req.setServicoId(1);
        req.setBarbeiroId(1);
        req.setClienteNome("Novo Cliente");
        req.setContato("49999999999");
        req.setDataHora(dataHora);
        req.setOrigemContato(OrigemContato.WHATSAPP);

        mockMvc.perform(post("/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteNome").value("Novo Cliente"));
    }

    @Test
    @DisplayName("POST /api/agenda deve retornar 409 Conflict se houver sobreposicao de horario (RF10)")
    void criarAgendamentoConflito() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusHours(3);

        Agendamento existente = new Agendamento(1, 1, 1, "Existente", "49999999999", dataHora, OrigemContato.WHATSAPP, StatusAgendamento.AGENDADO);
        existente.setDuracaoMinutos(30);
        agendamentoRepo.inserir(existente);

        AgendamentoRequest req = new AgendamentoRequest();
        req.setBarbeariaId(1);
        req.setServicoId(1);
        req.setBarbeiroId(1);
        req.setClienteNome("Cliente Conflitante");
        req.setContato("49999999999");
        req.setDataHora(dataHora);
        req.setOrigemContato(OrigemContato.WHATSAPP);

        mockMvc.perform(post("/api/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.sucesso").value(false));
    }
}
