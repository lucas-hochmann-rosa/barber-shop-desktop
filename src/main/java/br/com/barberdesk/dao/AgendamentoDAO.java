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
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), 0)) {
            throw new SQLException("Conflito: Este barbeiro já possui um agendamento em um intervalo de 30 minutos deste horário.");
        }

        String sql = "INSERT INTO agendamentos " +
                "(barbearia_id, servico_id, barbeiro_id, servico_nome_snapshot, barbeiro_nome_snapshot, " +
                " cliente_nome, contato, data_hora, origem_contato, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, a.getBarbeariaId());
            if (a.getServicoId() > 0) stmt.setInt(2, a.getServicoId()); else stmt.setNull(2, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(3, a.getBarbeiroId()); else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, a.getServicoNome());
            stmt.setString(5, a.getBarbeiroNome());

            stmt.setString(6, a.getClienteNome());
            stmt.setString(7, a.getContato());
            stmt.setTimestamp(8, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(9, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(10, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public void atualizar(Agendamento a) throws SQLException {
        if (verificarConflito(a.getBarbeiroId(), a.getDataHora(), a.getId())) {
            throw new SQLException("Conflito: Este barbeiro já possui um agendamento em um intervalo de 30 minutos deste horário.");
        }

        String sql = "UPDATE agendamentos SET " +
                "servico_id=?, barbeiro_id=?, servico_nome_snapshot=?, barbeiro_nome_snapshot=?, " +
                "cliente_nome=?, contato=?, data_hora=?, origem_contato=?, status=? " +
                "WHERE id=?";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (a.getServicoId() > 0) stmt.setInt(1, a.getServicoId()); else stmt.setNull(1, Types.INTEGER);
            if (a.getBarbeiroId() > 0) stmt.setInt(2, a.getBarbeiroId()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, a.getServicoNome());
            stmt.setString(4, a.getBarbeiroNome());
            stmt.setString(5, a.getClienteNome());
            stmt.setString(6, a.getContato());
            stmt.setTimestamp(7, Timestamp.valueOf(a.getDataHora()));
            stmt.setString(8, a.getOrigemContato() != null ? a.getOrigemContato().name() : OrigemContato.OUTRO.name());
            stmt.setString(9, a.getStatus() != null ? a.getStatus().name() : StatusAgendamento.AGENDADO.name());
            stmt.setInt(10, a.getId());

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

    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int excluirId) throws SQLException {
        if (barbeiroId <= 0 || dataHora == null) return false;
        String sql = "SELECT COUNT(*) FROM agendamentos " +
                "WHERE barbeiro_id=? " +
                "AND data_hora > ? AND data_hora < ? " +
                "AND status IN ('" + StatusAgendamento.AGENDADO.name() + "','" + StatusAgendamento.EM_ATENDIMENTO.name() + "')" +
                (excluirId > 0 ? " AND id <> ?" : "");

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, barbeiroId);
            stmt.setTimestamp(2, Timestamp.valueOf(dataHora.minusMinutes(30)));
            stmt.setTimestamp(3, Timestamp.valueOf(dataHora.plusMinutes(30)));
            if (excluirId > 0) stmt.setInt(4, excluirId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * Overload para compatibilidade com chamadas antigas que não informam o ID
     * do agendamento a ser ignorado.
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora) throws SQLException {
        return verificarConflito(barbeiroId, dataHora, 0);
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
        return a;
    }
}
