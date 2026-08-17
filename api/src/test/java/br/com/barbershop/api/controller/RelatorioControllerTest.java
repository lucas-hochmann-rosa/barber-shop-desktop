package br.com.barbershop.api.controller;

import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.dao.repository.RelatorioRepository;
import br.com.barbershop.model.ItemRelatorio;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RelatorioControllerTest {

    private MockMvc mockMvc;
    private RelatorioService relatorioService;
    private BarbeariaService barbeariaService;

    @BeforeEach
    void setUp() {
        barbeariaService = new BarbeariaService(new InMemoryBarbeariaRepository());
        relatorioService = new RelatorioService(new RelatorioRepository() {
            @Override
            public BigDecimal faturamentoTotal(int barbeariaId, LocalDate inicio, LocalDate fim) {
                return new BigDecimal("1500.00");
            }

            @Override
            public List<ItemRelatorio> servicosMaisVendidos(int barbeariaId, LocalDate inicio, LocalDate fim) {
                return List.of(new ItemRelatorio("Corte Masculino", 20, new BigDecimal("900.00")));
            }

            @Override
            public List<ItemRelatorio> rankingBarbeiros(int barbeariaId, LocalDate inicio, LocalDate fim) {
                return List.of(new ItemRelatorio("Carlos Silva", 20, BigDecimal.ZERO));
            }
        });

        RelatorioController controller = new RelatorioController(relatorioService, barbeariaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setConversionService(TestUtils.createConversionService())
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/relatorios deve retornar dados consolidados de faturamento e ranking")
    void gerarRelatorio() throws Exception {
        mockMvc.perform(get("/api/relatorios?de=2026-08-01&ate=2026-08-16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faturamentoTotal").value(1500.00))
                .andExpect(jsonPath("$.servicosMaisVendidos[0].nome").value("Corte Masculino"))
                .andExpect(jsonPath("$.rankingBarbeiros[0].nome").value("Carlos Silva"));
    }
}
