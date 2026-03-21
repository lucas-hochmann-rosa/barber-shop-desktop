package br.com.barberdesk.dao;

import br.com.barberdesk.model.Servico;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {
    public Servico buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM servicos WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairServico(rs);
                }
            }
        }
        return null;
    }

    public List<Servico> listarPorBarbearia(int barbeariaId) throws SQLException {
        String sql = "SELECT * FROM servicos WHERE barbearia_id = ?";
        List<Servico> servicos = new ArrayList<>();
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    servicos.add(extrairServico(rs));
                }
            }
        }
        return servicos;
    }

    public int inserir(Servico servico) throws SQLException {
        String sql = "INSERT INTO servicos (barbearia_id, nome, preco, imagem_path, duracao_minutos) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, servico.getBarbeariaId());
            pstmt.setString(2, servico.getNome());
            pstmt.setBigDecimal(3, servico.getPreco());
            pstmt.setString(4, servico.getImagemPath());
            pstmt.setInt(5, servico.getDuracaoMinutos());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void atualizar(Servico servico) throws SQLException {
        String sql = "UPDATE servicos SET nome = ?, preco = ?, imagem_path = ?, duracao_minutos = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, servico.getNome());
            pstmt.setBigDecimal(2, servico.getPreco());
            pstmt.setString(3, servico.getImagemPath());
            pstmt.setInt(4, servico.getDuracaoMinutos());
            pstmt.setInt(5, servico.getId());
            pstmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Compatibilidade: algumas telas chamam "excluir" em vez de "deletar".
     */
    public void excluir(int id) throws SQLException {
        deletar(id);
    }

    private Servico extrairServico(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int barbeariaId = rs.getInt("barbearia_id");
        String nome = rs.getString("nome");
        BigDecimal preco = rs.getBigDecimal("preco");
        String imagemPath = rs.getString("imagem_path");
        int duracaoMinutos = rs.getInt("duracao_minutos");
        if (rs.wasNull()) duracaoMinutos = 30;
        return new Servico(id, barbeariaId, nome, preco, imagemPath, duracaoMinutos);
    }
}
