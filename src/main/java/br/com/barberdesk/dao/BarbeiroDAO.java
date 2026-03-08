package br.com.barberdesk.dao;

import br.com.barberdesk.model.Barbeiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarbeiroDAO {
    public Barbeiro buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM barbeiros WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairBarbeiro(rs);
                }
            }
        }
        return null;
    }

    public List<Barbeiro> listarPorBarbearia(int barbeariaId) throws SQLException {
        String sql = "SELECT * FROM barbeiros WHERE barbearia_id = ?";
        List<Barbeiro> barbeiros = new ArrayList<>();
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    barbeiros.add(extrairBarbeiro(rs));
                }
            }
        }
        return barbeiros;
    }

    public int inserir(Barbeiro barbeiro) throws SQLException {
        String sql = "INSERT INTO barbeiros (barbearia_id, nome, imagem_path) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, barbeiro.getBarbeariaId());
            pstmt.setString(2, barbeiro.getNome());
            pstmt.setString(3, barbeiro.getImagemPath());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void atualizar(Barbeiro barbeiro) throws SQLException {
        String sql = "UPDATE barbeiros SET nome = ?, imagem_path = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barbeiro.getNome());
            pstmt.setString(2, barbeiro.getImagemPath());
            pstmt.setInt(3, barbeiro.getId());
            pstmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM barbeiros WHERE id = ?";
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

    private Barbeiro extrairBarbeiro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int barbeariaId = rs.getInt("barbearia_id");
        String nome = rs.getString("nome");
        String imagemPath = rs.getString("imagem_path");
        return new Barbeiro(id, barbeariaId, nome, imagemPath);
    }
}
