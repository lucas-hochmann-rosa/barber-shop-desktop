package br.com.barberdesk.service;

import br.com.barberdesk.dao.repository.RelatorioRepository;
import br.com.barberdesk.model.ItemRelatorio;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Orquestra os relatórios agregados sobre agendamentos concluídos,
 * delegando as consultas em si para {@link RelatorioRepository}. Faturamento
 * é calculado pelo preço ATUAL do serviço (servicos.preco), não pelo preço
 * no momento do agendamento - não existe snapshot de preço, só de
 * nome/duração (ver {@link br.com.barberdesk.dao.AgendamentoDAO}). Se o
 * preço de um serviço mudar, relatórios de períodos passados passam a
 * refletir o preço novo. Limitação conhecida, documentada no README
 * ("Melhorias Futuras").
 */
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;

    public RelatorioService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    /**
     * Soma o preço dos serviços de todos os agendamentos concluídos da
     * barbearia no período informado (inclusive nas duas pontas).
     *
     * @param barbeariaId identificador da barbearia
     * @param inicio      data inicial do período (inclusive)
     * @param fim         data final do período (inclusive)
     * @return soma dos preços dos serviços concluídos no período, ou zero se não houver nenhum
     */
    public BigDecimal faturamentoTotal(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException {
        return relatorioRepository.faturamentoTotal(barbeariaId, inicio, fim);
    }

    /**
     * Lista os 10 serviços com mais agendamentos concluídos no período,
     * ordenados por quantidade decrescente. O nome exibido é o snapshot
     * gravado no agendamento quando disponível, caindo para o nome atual do
     * serviço e, por fim, para "Serviço removido" caso o serviço tenha sido
     * excluído.
     *
     * @param barbeariaId identificador da barbearia
     * @param inicio      data inicial do período (inclusive)
     * @param fim         data final do período (inclusive)
     * @return até 10 itens, cada um com nome, quantidade e faturamento associado
     */
    public List<ItemRelatorio> servicosMaisVendidos(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException {
        return relatorioRepository.servicosMaisVendidos(barbeariaId, inicio, fim);
    }

    /**
     * Lista os 10 barbeiros com mais atendimentos concluídos no período,
     * ordenados por quantidade decrescente. O campo "total" sempre vem zerado
     * aqui, pois este ranking mede volume de atendimentos, não faturamento
     * por barbeiro. O nome exibido usa o mesmo esquema de fallback (snapshot
     * → nome atual → "Barbeiro removido") descrito em
     * {@link #servicosMaisVendidos(int, LocalDate, LocalDate)}.
     *
     * @param barbeariaId identificador da barbearia
     * @param inicio      data inicial do período (inclusive)
     * @param fim         data final do período (inclusive)
     * @return até 10 itens, cada um com nome, quantidade de atendimentos e total zero
     */
    public List<ItemRelatorio> rankingBarbeiros(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException {
        return relatorioRepository.rankingBarbeiros(barbeariaId, inicio, fim);
    }
}
