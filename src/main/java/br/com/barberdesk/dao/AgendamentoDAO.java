package br.com.barberdesk.dao;

import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.OrigemContato;
import br.com.barberdesk.model.StatusAgendamento;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    public int inserir(Agendamento a) throws SQLException {
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), a.getDuracaoMinutos(), 0)) {
            throw new SQLException("Conflito: este barbeiro já possui um agendamento nesse horário.");
        }

        String sql = "INSERT INTO agendamentos " +
                "(barbearia_id, servico_id, barbeiro_id, servico_nome_snapshot, barbeiro_nome_snapshot, duracao_minutos_snapshot, " +
                " cliente_nome, contato, data_hora, origem_contato, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getBarbeariaId());
            if (a.getServicoId() > 0) stmt.setInt(2, a.getServicoId()); else stmt.setNull(2, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(3, a.getBarbeiroId()); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, a.getServicoNome());
            stmt.setString(5, a.getBarbeiroNome());
            stmt.setInt(6, a.getDuracaoMinutos());

            stmt.setString(7, a.getClienteNome());
            stmt.setString(8, a.getContato());
            stmt.setTimestamp(9, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(10, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(11, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public void atualizar(Agendamento a) throws SQLException {
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), a.getDuracaoMinutos(), a.getId())) {
            throw new SQLException("Conflito: este barbeiro já possui um agendamento nesse horário.");
        }

        String sql = "UPDATE agendamentos SET " +
                "servico_id=?, barbeiro_id=?, servico_nome_snapshot=?, barbeiro_nome_snapshot=?, duracao_minutos_snapshot=?, " +
                "cliente_nome=?, contato=?, data_hora=?, origem_contato=?, status=? " +
                "WHERE id=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (a.getServicoId() > 0) stmt.setInt(1, a.getServicoId()); else stmt.setNull(1, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(2, a.getBarbeiroId()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, a.getServicoNome());
            stmt.setString(4, a.getBarbeiroNome());
            stmt.setInt(5, a.getDuracaoMinutos());
            stmt.setString(6, a.getClienteNome());
            stmt.setString(7, a.getContato());
            stmt.setTimestamp(8, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(9, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(10, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());
            stmt.setInt(11, a.getId());

            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM agendamentos WHERE id=?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Agendamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT a.*, " +
                "COALESCE(a.servico_nome_snapshot, s.nome) AS servico_nome, " +
                "COALESCE(a.barbeiro_nome_snapshot, b.nome) AS barbeiro_nome " +
                "FROM agendamentos a " +
                "LEFT JOIN servicos s ON a.servico_id = s.id " +
                "LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                "WHERE a.id=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return extrairAgendamento(rs);
            }
        }
        return null;
    }

    public List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) throws SQLException {
        String filtro = " AND a.status IN ('" + StatusAgendamento.AGENDADO.name() + "','" + StatusAgendamento.EM_ATENDIMENTO.name() + "')";
        return listar(barbeariaId, filtro);
    }

    /**
     * Alias para compatibilidade (camada service/UI usa listarPendentes).
     */
    public List<Agendamento> listarPendentes(int barbeariaId) throws SQLException {
        return listarPendentesPorBarbearia(barbeariaId);
    }

    public List<Agendamento> listarPorBarbearia(int barbeariaId) throws SQLException {
        return listar(barbeariaId, "");
    }

    /**
     * Alias para compatibilidade (camada service/UI usa listarTodos).
     */
    public List<Agendamento> listarTodos(int barbeariaId) throws SQLException {
        return listarPorBarbearia(barbeariaId);
    }

    // Duração máxima aceita para um serviço (ver DialogServico) — usada só para
    // dimensionar a janela de busca abaixo, largura o bastante para conter
    // qualquer agendamento existente que possa se sobrepor ao novo.
    private static final int DURACAO_MAXIMA_MINUTOS = 480;

    /**
     * Conflito = sobreposição real de intervalo (início/fim, considerando a duração de
     * cada serviço), não mais uma janela fixa de 30 minutos. Barbeiros diferentes podem
     * atender no mesmo horário normalmente (não há conflito de recurso entre eles).
     * Agendamentos "encostados" (um termina exatamente quando o outro começa) não contam
     * como conflito.
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos, int excluirId) throws SQLException {
        if (barbeiroId <= 0 || dataHora == null) return false;
        int duracaoNova = duracaoMinutos > 0 ? duracaoMinutos : 30;
        LocalDateTime inicioNovo = dataHora;
        LocalDateTime fimNovo = dataHora.plusMinutes(duracaoNova);

        String sql = "SELECT data_hora, duracao_minutos_snapshot FROM agendamentos " +
                "WHERE barbeiro_id=? " +
                "AND data_hora > ? AND data_hora < ? " +
                "AND status IN ('" + StatusAgendamento.AGENDADO.name() + "','" + StatusAgendamento.EM_ATENDIMENTO.name() + "')" +
                (excluirId > 0 ? " AND id <> ?" : "");

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barbeiroId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora.minusMinutes(DURACAO_MAXIMA_MINUTOS)));
            stmt.setTimestamp(3, Timestamp.valueOf(dataHora.plusMinutes(DURACAO_MAXIMA_MINUTOS)));
            if (excluirId > 0) stmt.setInt(4, excluirId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDateTime inicioExistente = rs.getTimestamp("data_hora").toLocalDateTime();
                    int duracaoExistente = rs.getInt("duracao_minutos_snapshot");
                    if (rs.wasNull() || duracaoExistente <= 0) duracaoExistente = 30;
                    LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

                    if (inicioExistente.isBefore(fimNovo) && fimExistente.isAfter(inicioNovo)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Overload de conveniência para checagem antes de existir um Agendamento
     * montado (ex.: pré-validação na tela de novo agendamento) — não há id
     * ainda para excluir da busca.
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) throws SQLException {
        return verificarConflito(barbeiroId, dataHora, duracaoMinutos, 0);
    }

    private List<Agendamento> listar(int barbeariaId, String extraSql) throws SQLException {
        List<Agendamento> lista = new ArrayList<>();
        String sql = "SELECT a.*, " +
                "COALESCE(a.servico_nome_snapshot, s.nome) AS servico_nome, " +
                "COALESCE(a.barbeiro_nome_snapshot, b.nome) AS barbeiro_nome " +
                "FROM agendamentos a " +
                "LEFT JOIN servicos s ON a.servico_id = s.id " +
                "LEFT JOIN barbeiros b ON a.barbeiro_id = b.id " +
                "WHERE a.barbearia_id=? " + (extraSql == null ? "" : extraSql) +
                " ORDER BY a.data_hora ASC";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barbeariaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(extrairAgendamento(rs));
            }
        }
        return lista;
    }

    private Agendamento extrairAgendamento(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("id"));
        a.setBarbeariaId(rs.getInt("barbearia_id"));
        a.setServicoId(rs.getInt("servico_id"));
        if (rs.wasNull()) a.setServicoId(0);
        a.setBarbeiroId(rs.getInt("barbeiro_id"));
        if (rs.wasNull()) a.setBarbeiroId(0);
        a.setClienteNome(rs.getString("cliente_nome"));
        a.setContato(rs.getString("contato"));
        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) a.setDataHora(ts.toLocalDateTime());

        String origem = rs.getString("origem_contato");
        try { a.setOrigemContato(OrigemContato.valueOf(origem)); }
        catch (Exception e) { a.setOrigemContato(OrigemContato.OUTRO); }

        String st = rs.getString("status");
        try { a.setStatus(StatusAgendamento.valueOf(st)); }
        catch (Exception e) { a.setStatus(StatusAgendamento.AGENDADO); }

        a.setServicoNome(rs.getString("servico_nome"));
        a.setBarbeiroNome(rs.getString("barbeiro_nome"));

        int duracao = rs.getInt("duracao_minutos_snapshot");
        a.setDuracaoMinutos(rs.wasNull() || duracao <= 0 ? 30 : duracao);

        return a;
    }
}
