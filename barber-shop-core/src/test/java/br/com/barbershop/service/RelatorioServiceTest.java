package br.com.barbershop.service;

import br.com.barbershop.model.ItemRelatorio;
import br.com.barbershop.service.fake.FakeRelatorioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link RelatorioService} com repositório em memória. Como as
 * agregações em si são SQL puro em {@link br.com.barbershop.dao.RelatorioDAO},
 * o objetivo aqui é confirmar que o service repassa os parâmetros
 * corretamente ao repositório e devolve o resultado tal como recebido.
 */
class RelatorioServiceTest {

    private FakeRelatorioRepository relatorioRepository;
    private RelatorioService service;

    @BeforeEach
    void setUp() {
        relatorioRepository = new FakeRelatorioRepository();
        service = new RelatorioService(relatorioRepository);
    }

    @Test
    void faturamentoTotalRepassaParametrosEDevolveResultadoDoRepositorio() throws Exception {
        relatorioRepository.setFaturamentoTotal(new BigDecimal("150.00"));
        LocalDate inicio = LocalDate.of(2026, 8, 1);
        LocalDate fim = LocalDate.of(2026, 8, 31);

        BigDecimal total = service.faturamentoTotal(1, inicio, fim);

        assertEquals(new BigDecimal("150.00"), total);
        assertEquals(1, relatorioRepository.getUltimoBarbeariaId());
        assertEquals(inicio, relatorioRepository.getUltimoInicio());
        assertEquals(fim, relatorioRepository.getUltimoFim());
    }

    @Test
    void servicosMaisVendidosDevolveListaDoRepositorio() throws Exception {
        List<ItemRelatorio> itens = List.of(new ItemRelatorio("Corte", 5, new BigDecimal("250.00")));
        relatorioRepository.setServicosMaisVendidos(itens);

        List<ItemRelatorio> resultado = service.servicosMaisVendidos(1, LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(itens, resultado);
    }

    @Test
    void rankingBarbeirosDevolveListaDoRepositorio() throws Exception {
        List<ItemRelatorio> itens = List.of(new ItemRelatorio("Barbeiro A", 8, BigDecimal.ZERO));
        relatorioRepository.setRankingBarbeiros(itens);

        List<ItemRelatorio> resultado = service.rankingBarbeiros(1, LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(itens, resultado);
    }
}
