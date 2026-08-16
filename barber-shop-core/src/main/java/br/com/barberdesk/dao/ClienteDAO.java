package br.com.barberdesk.dao;

import br.com.barberdesk.dao.repository.ClienteRepository;
import br.com.barberdesk.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados da tabela {@code clientes}: registro automático (upsert) e
 * listagem dos clientes de uma barbearia. Não há CRUD completo porque não existe
 * tela de cadastro de cliente dedicada - o cliente é criado/atualizado
 * implicitamente ao agendar um horário.
 */
public class ClienteDAO implements ClienteRepository {

    /**
     * Cria o cliente se ainda não existir (por barbearia+contato) ou atualiza o
     * nome se ele mudou desde a última vez. Chamado automaticamente ao criar um
     * agendamento - não existe tela de cadastro de cliente dedicada.
     *
     * Implementado como upsert ({@code INSERT ... ON DUPLICATE KEY UPDATE}) em vez
     * de um INSERT simples: a combinação barbearia+contato é a chave natural do
     * cliente, então se o mesmo contato agendar de novo com um nome diferente, o
     * nome é apenas atualizado em vez de gerar um cliente duplicado.
     *
     * @param barbeariaId barbearia à qual o cliente pertence
     * @param nome        nome do cliente informado no agendamento
     * @param contato     telefone/contato do cliente; se vazio ou nulo, não faz nada
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

    /**
     * Lista todos os clientes de uma barbearia, ordenados por nome.
     */
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

    /**
     * Converte a linha atual do {@link ResultSet} em um objeto {@link Cliente}.
     */
    private Cliente extrairCliente(ResultSet rs) throws SQLException {
        return new Cliente(rs.getInt("id"), rs.getInt("barbearia_id"), rs.getString("nome"), rs.getString("contato"));
    }
}
