package br.com.barberdesk.dao;

import br.com.barberdesk.dao.repository.BarbeiroRepository;
import br.com.barberdesk.model.Barbeiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados da tabela {@code barbeiros}: CRUD dos profissionais cadastrados
 * em cada barbearia, incluindo checagem de nome duplicado.
 */
public class BarbeiroDAO implements BarbeiroRepository {
    /**
     * Busca um barbeiro pelo ID.
     *
     * @return o barbeiro encontrado, ou {@code null} se não existir
     */
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

    /**
     * Lista todos os barbeiros cadastrados em uma barbearia.
     */
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

    /**
     * Insere um novo barbeiro.
     *
     * @return o ID gerado, ou -1 se não foi possível obtê-lo
     */
    public int inserir(Barbeiro barbeiro) throws SQLException {
        String sql = "INSERT INTO barbeiros (barbearia_id, nome, imagem_base64) VALUES (?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, barbeiro.getBarbeariaId());
            pstmt.setString(2, barbeiro.getNome());
            pstmt.setString(3, barbeiro.getImagemBase64());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Atualiza nome e imagem de um barbeiro existente.
     */
    public void atualizar(Barbeiro barbeiro) throws SQLException {
        String sql = "UPDATE barbeiros SET nome = ?, imagem_base64 = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barbeiro.getNome());
            pstmt.setString(2, barbeiro.getImagemBase64());
            pstmt.setInt(3, barbeiro.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Remove definitivamente um barbeiro pelo ID.
     */
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM barbeiros WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Usado para bloquear nome duplicado dentro da mesma barbearia.
     * excluirId > 0 ignora o próprio registro (caso de edição).
     */
    public boolean existePorNome(int barbeariaId, String nome, int excluirId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM barbeiros WHERE barbearia_id = ? AND LOWER(nome) = LOWER(?)" +
                (excluirId > 0 ? " AND id <> ?" : "");
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            pstmt.setString(2, nome);
            if (excluirId > 0) pstmt.setInt(3, excluirId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Compatibilidade: algumas telas chamam "excluir" em vez de "deletar".
     */
    public void excluir(int id) throws SQLException {
        deletar(id);
    }

    /**
     * Converte a linha atual do {@link ResultSet} em um objeto {@link Barbeiro}.
     */
    private Barbeiro extrairBarbeiro(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int barbeariaId = rs.getInt("barbearia_id");
        String nome = rs.getString("nome");
        String imagemBase64 = rs.getString("imagem_base64");
        return new Barbeiro(id, barbeariaId, nome, imagemBase64);
    }
}
