package br.com.barbershop.service.fake;

import br.com.barbershop.dao.repository.RelatorioRepository;
import br.com.barbershop.model.ItemRelatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Repositório de relatórios em memória, usado nos testes de
 * {@link br.com.barbershop.service.RelatorioService}. As agregações reais
 * são SQL puro em {@link br.com.barbershop.dao.RelatorioDAO}, então este
 * fake apenas devolve valores pré-configurados e registra os parâmetros
 * recebidos - o objetivo do teste é confirmar que o service repassa
 * corretamente os parâmetros e devolve o resultado do repositório, não
 * reimplementar a agregação.
 */
public class FakeRelatorioRepository implements RelatorioRepository {

    private BigDecimal faturamentoTotal = BigDecimal.ZERO;
    private List<ItemRelatorio> servicosMaisVendidos = Collections.emptyList();
    private List<ItemRelatorio> rankingBarbeiros = Collections.emptyList();

    private Integer ultimoBarbeariaId;
    private LocalDate ultimoInicio;
    private LocalDate ultimoFim;

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public void setServicosMaisVendidos(List<ItemRelatorio> servicosMaisVendidos) {
        this.servicosMaisVendidos = servicosMaisVendidos;
    }

    public void setRankingBarbeiros(List<ItemRelatorio> rankingBarbeiros) {
        this.rankingBarbeiros = rankingBarbeiros;
    }

    public Integer getUltimoBarbeariaId() {
        return ultimoBarbeariaId;
    }

    public LocalDate getUltimoInicio() {
        return ultimoInicio;
    }

    public LocalDate getUltimoFim() {
        return ultimoFim;
    }

    @Override
    public BigDecimal faturamentoTotal(int barbeariaId, LocalDate inicio, LocalDate fim) {
        registrarParametros(barbeariaId, inicio, fim);
        return faturamentoTotal;
    }

    @Override
    public List<ItemRelatorio> servicosMaisVendidos(int barbeariaId, LocalDate inicio, LocalDate fim) {
        registrarParametros(barbeariaId, inicio, fim);
        return servicosMaisVendidos;
    }

    @Override
    public List<ItemRelatorio> rankingBarbeiros(int barbeariaId, LocalDate inicio, LocalDate fim) {
        registrarParametros(barbeariaId, inicio, fim);
        return rankingBarbeiros;
    }

    private void registrarParametros(int barbeariaId, LocalDate inicio, LocalDate fim) {
        this.ultimoBarbeariaId = barbeariaId;
        this.ultimoInicio = inicio;
        this.ultimoFim = fim;
    }
}
