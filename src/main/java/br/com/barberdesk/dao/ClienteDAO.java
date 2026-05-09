package br.com.barberdesk.dao;

import br.com.barberdesk.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    /**
     * Cria o cliente se ainda não existir (por barbearia+contato) ou atualiza o
     * nome se ele mudou desde a última vez. Chamado automaticamente ao criar um
     * agendamento — não existe tela de cadastro de cliente dedicada.
     */
    public void registrar(int barbeariaId, String nome, String contato) throws SQLException {
        if (contato == null || contato.trim().isEmpty()) return;

        String sql = "INSERT INTO clientes (barbearia_id, nome, contato) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nome = VALUES(nome)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            pstmt.setString(2, nome);
            pstmt.setString(3, contato.trim());
            pstmt.executeUpdate();
        }
    }

    public List<Cliente> listarPorBarbearia(int barbeariaId) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE barbearia_id = ? ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, barbeariaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) lista.add(extrairCliente(rs));
            }
        }
        return lista;
    }

    private Cliente extrairCliente(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id"), rs.getInt("barbearia_id"), rs.getString("nome"), rs.getString("contato"));
    }
}
