package br.com.barbershop.api.dto;

import br.com.barbershop.model.ItemRelatorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO consolidado com os resultados dos relatórios gerenciais (RF09).
 */
public class RelatorioDTO {
    private LocalDate inicio;
    private LocalDate fim;
    private BigDecimal faturamentoTotal;
    private List<ItemRelatorio> servicosMaisVendidos;
    private List<ItemRelatorio> rankingBarbeiros;

    public RelatorioDTO() {}

    public RelatorioDTO(LocalDate inicio, LocalDate fim, BigDecimal faturamentoTotal, List<ItemRelatorio> servicosMaisVendidos, List<ItemRelatorio> rankingBarbeiros) {
        this.inicio = inicio;
        this.fim = fim;
        this.faturamentoTotal = faturamentoTotal;
        this.servicosMaisVendidos = servicosMaisVendidos;
        this.rankingBarbeiros = rankingBarbeiros;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }

    public BigDecimal getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(BigDecimal faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public List<ItemRelatorio> getServicosMaisVendidos() {
        return servicosMaisVendidos;
    }

    public void setServicosMaisVendidos(List<ItemRelatorio> servicosMaisVendidos) {
        this.servicosMaisVendidos = servicosMaisVendidos;
    }

    public List<ItemRelatorio> getRankingBarbeiros() {
        return rankingBarbeiros;
    }

    public void setRankingBarbeiros(List<ItemRelatorio> rankingBarbeiros) {
        this.rankingBarbeiros = rankingBarbeiros;
    }
}
