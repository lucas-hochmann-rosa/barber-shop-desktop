package br.com.barberdesk.service;

import br.com.barberdesk.dao.ConexaoMySQL;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Relatórios agregados sobre agendamentos concluídos. Faturamento é calculado
 * pelo preço ATUAL do serviço (servicos.preco), não pelo preço no momento do
 * agendamento — não existe snapshot de preço, só de nome/duração (ver
 * AgendamentoDAO). Se o preço de um serviço mudar, relatórios de períodos
 * passados passam a refletir o preço novo. Limitação conhecida, documentada
 * no README ("Melhorias Futuras").
 */
public class RelatorioService {

    /**
     * Linha genérica de um relatório de ranking (serviço mais vendido,
     * barbeiro com mais atendimentos etc.): um nome, uma quantidade de
     * ocorrências e um valor total associado.
     */
    public static class ItemRelatorio {
        private final String nome;
        private final long quantidade;
        private final BigDecimal total;

        public ItemRelatorio(String nome, long quantidade, BigDecimal total) {
            this.nome = nome;
            this.quantidade = quantidade;
            this.total = total;
        }

        public String getNome() { return nome; }
        public long getQuantidade() { return quantidade; }
        public BigDecimal getTotal() { return total; }
    }

    /**
     * Soma o preço dos serviços de todos os agendamentos concluídos da
     * barbearia no período informado (inclusive nas duas pontas).
     *
     * Importante: usa o preço ATUAL do serviço (servicos.preco), não um
     * snapshot do preço no momento do agendamento — não existe snapshot de
     * preço, só de nome/duração. Se o preço de um serviço for alterado,
     * relatórios de períodos passados passam a refletir o preço novo
     * retroativamente. Limitação conhecida (ver javadoc da classe).
     *
     * @param barbeariaId identificador da barbearia
     * @param inicio      data inicial do período (inclusive)
     * @param fim         data final do período (inclusive)
     * @return soma dos preços dos serviços concluídos no período, ou zero se não houver nenhum
     */
    public BigDecimal faturamentoTotal(int barbeariaId, LocalDate inicio, LocalDate fim) throws SQLException {
        String sql = "SELECT COALESCE(SUM(s.preco), 0) FROM agendamentos a " +
                "LEFT JOIN servicos s ON a.servico_id = s.id " +
                "WHERE a.barbearia_id = ? AND a.status = 'CONCLUIDO' AND DATE(a.data_hora) BETWEEN ? AND ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            pstmt.setDate(2, java.sql.Date.valueOf(inicio));
            pstmt.setDate(3, java.sql.Date.valueOf(fim));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
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
        // GROUP BY repete a expressão (não usa o alias "nome"): como servicos.nome também se
        // chama "nome", agrupar pelo alias colide com a coluna real da tabela e o MySQL, em
        // sql_mode=only_full_group_by, rejeita a query por não conseguir provar dependência
        // funcional entre o COALESCE (que pode usar o snapshot OU a coluna viva) e essa coluna.
        String sql = "SELECT COALESCE(a.servico_nome_snapshot, s.nome, 'Serviço removido') AS nome, " +
                "COUNT(*) AS qtd, COALESCE(SUM(s.preco), 0) AS total " +
                "FROM agendamentos a LEFT JOIN servicos s ON a.servico_id = s.id " +
                "WHERE a.barbearia_id = ? AND a.status = 'CONCLUIDO' AND DATE(a.data_hora) BETWEEN ? AND ? " +
                "GROUP BY COALESCE(a.servico_nome_snapshot, s.nome, 'Serviço removido') ORDER BY qtd DESC LIMIT 10";
        return listarItens(barbeariaId, inicio, fim, sql);
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
        String sql = "SELECT COALESCE(a.barbeiro_nome_snapshot, b.nome, 'Barbeiro removido') AS nome, " +
                "COUNT(*) AS qtd, 0 AS total " +
                "FROM agendamentos a LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                "WHERE a.barbearia_id = ? AND a.status = 'CONCLUIDO' AND DATE(a.data_hora) BETWEEN ? AND ? " +
                "GROUP BY COALESCE(a.barbeiro_nome_snapshot, b.nome, 'Barbeiro removido') ORDER BY qtd DESC LIMIT 10";
        return listarItens(barbeariaId, inicio, fim, sql);
    }

    // Execução compartilhada das consultas de ranking: aplica os três parâmetros
    // posicionais comuns (barbearia, início, fim) e monta a lista de itens a
    // partir das colunas "nome", "qtd" e "total" do result set.
    private List<ItemRelatorio> listarItens(int barbeariaId, LocalDate inicio, LocalDate fim, String sql) throws SQLException {
        List<ItemRelatorio> lista = new ArrayList<>();
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            pstmt.setDate(2, java.sql.Date.valueOf(inicio));
            pstmt.setDate(3, java.sql.Date.valueOf(fim));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ItemRelatorio(rs.getString("nome"), rs.getLong("qtd"), rs.getBigDecimal("total")));
                }
            }
        }
        return lista;
    }
}
