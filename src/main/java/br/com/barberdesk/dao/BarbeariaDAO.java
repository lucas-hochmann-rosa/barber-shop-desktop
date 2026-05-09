package br.com.barberdesk.dao;

import br.com.barberdesk.model.Barbearia;
import java.sql.*;
import java.time.LocalDate;

public class BarbeariaDAO {
    public Barbearia buscarPrimeira() throws SQLException {
        String sql = "SELECT * FROM barbearias LIMIT 1";
        try (Connection conn = ConexaoMySQL.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return extrairBarbearia(rs);
            }
        }
        return null;
    }

    public Barbearia buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM barbearias WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairBarbearia(rs);
                }
            }
        }
        return null;
    }

    public int inserir(Barbearia barbearia) throws SQLException {
        String sql = "INSERT INTO barbearias (nome, cep, data_fundacao, cultura_valores, horario_abertura, horario_fechamento) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, barbearia.getNome());
            pstmt.setString(2, barbearia.getCep());
            pstmt.setDate(3, barbearia.getDataFundacao() != null ?
                    Date.valueOf(barbearia.getDataFundacao()) : null);
            pstmt.setString(4, barbearia.getCulturaValores());
            pstmt.setTime(5, barbearia.getHorarioAbertura() != null ?
                    Time.valueOf(barbearia.getHorarioAbertura()) : null);
            pstmt.setTime(6, barbearia.getHorarioFechamento() != null ?
                    Time.valueOf(barbearia.getHorarioFechamento()) : null);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void atualizar(Barbearia barbearia) throws SQLException {
        String sql = "UPDATE barbearias SET nome = ?, cep = ?, data_fundacao = ?, cultura_valores = ?, horario_abertura = ?, horario_fechamento = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barbearia.getNome());
            pstmt.setString(2, barbearia.getCep());
            pstmt.setDate(3, barbearia.getDataFundacao() != null ?
                    Date.valueOf(barbearia.getDataFundacao()) : null);
            pstmt.setString(4, barbearia.getCulturaValores());
            pstmt.setTime(5, barbearia.getHorarioAbertura() != null ?
                    Time.valueOf(barbearia.getHorarioAbertura()) : null);
            pstmt.setTime(6, barbearia.getHorarioFechamento() != null ?
                    Time.valueOf(barbearia.getHorarioFechamento()) : null);
            pstmt.setInt(7, barbearia.getId());
            pstmt.executeUpdate();
        }
    }

    private Barbearia extrairBarbearia(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cep = rs.getString("cep");
        Date dataFundacao = rs.getDate("data_fundacao");
        String culturaValores = rs.getString("cultura_valores");
        Barbearia b = new Barbearia(id, nome, cep,
                dataFundacao != null ? dataFundacao.toLocalDate() : null,
                culturaValores);

        Time abertura = rs.getTime("horario_abertura");
        b.setHorarioAbertura(abertura != null ? abertura.toLocalTime() : null);
        Time fechamento = rs.getTime("horario_fechamento");
        b.setHorarioFechamento(fechamento != null ? fechamento.toLocalTime() : null);

        return b;
    }
}
