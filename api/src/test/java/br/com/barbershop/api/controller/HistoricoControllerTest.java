package br.com.barbershop.api.controller;

import br.com.barbershop.api.helper.InMemoryAgendamentoRepository;
import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.InMemoryClienteRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.StatusAgendamento;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.BarbeariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HistoricoControllerTest {

    private MockMvc mockMvc;
    private InMemoryAgendamentoRepository agendamentoRepo;
    private AgendaService agendaService;
    private BarbeariaService barbeariaService;

    @BeforeEach
    void setUp() {
        agendamentoRepo = new InMemoryAgendamentoRepository();
        agendaService = new AgendaService(agendamentoRepo, new InMemoryClienteRepository());
        barbeariaService = new BarbeariaService(new InMemoryBarbeariaRepository());

        HistoricoController controller = new HistoricoController(agendaService, barbeariaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setConversionService(TestUtils.createConversionService())
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/historico deve retornar agendamentos filtrados")
    void consultarHistorico() throws Exception {
        Agendamento a1 = new Agendamento(1, 1, 1, "Cliente A", "49999999901", LocalDateTime.of(2026, 8, 10, 10, 0), OrigemContato.WHATSAPP, StatusAgendamento.CONCLUIDO);
        Agendamento a2 = new Agendamento(1, 1, 2, "Cliente B", "49999999902", LocalDateTime.of(2026, 8, 12, 14, 0), OrigemContato.WHATSAPP, StatusAgendamento.CANCELADO);
        agendamentoRepo.inserir(a1);
        agendamentoRepo.inserir(a2);

        mockMvc.perform(get("/api/historico?status=CONCLUIDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clienteNome").value("Cliente A"));
    }
}
