package br.com.barberdesk.dao;

import br.com.barberdesk.model.Usuario;
import java.sql.*;

public class UsuarioDAO {
    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        }
        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuario(rs);
                }
            }
        }
        return null;
    }

    public int inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (barbearia_id, login, senha_hash) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuario.getBarbeariaId());
            pstmt.setString(2, usuario.getLogin());
            pstmt.setString(3, usuario.getSenhaHash());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET login = ?, senha_hash = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getLogin());
            pstmt.setString(2, usuario.getSenhaHash());
            pstmt.setInt(3, usuario.getId());
            pstmt.executeUpdate();
        }
    }

    private Usuario extrairUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int barbeariaId = rs.getInt("barbearia_id");
        String login = rs.getString("login");
        String senhaHash = rs.getString("senha_hash");
        return new Usuario(id, barbeariaId, login, senhaHash);
    }
}
