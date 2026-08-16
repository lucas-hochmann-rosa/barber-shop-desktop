package br.com.barbershop.dao.repository;

import br.com.barbershop.model.ItemRelatorio;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrato das agregações usadas por
 * {@link br.com.barbershop.service.RelatorioService}. As consultas em si
 * (SQL de agregação sobre agendamentos concluídos) vivem na implementação
 * concreta ({@link br.com.barbershop.dao.RelatorioDAO}) - o service só
 * conhece este contrato.
 */
public interface RelatorioRepository {

    /** Soma o preço dos serviços de todos os agendamentos concluídos da barbearia no período informado. */
    BigDecimal faturamentoTotal(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException;

    /** Lista os 10 serviços com mais agendamentos concluídos no período, por quantidade decrescente. */
    List<ItemRelatorio> servicosMaisVendidos(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException;

    /** Lista os 10 barbeiros com mais atendimentos concluídos no período, por quantidade decrescente. */
    List<ItemRelatorio> rankingBarbeiros(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException;
}
