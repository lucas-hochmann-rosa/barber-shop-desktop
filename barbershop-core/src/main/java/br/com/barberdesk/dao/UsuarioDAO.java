package br.com.barberdesk.dao;

import br.com.barberdesk.dao.repository.UsuarioRepository;
import br.com.barberdesk.model.Usuario;
import java.sql.*;

/**
 * Acesso a dados da tabela {@code usuarios}: consulta e manutenção das contas de
 * acesso ao sistema, vinculadas a uma barbearia. Armazena apenas o hash da senha
 * e o salt usado no cálculo desse hash — a senha em texto puro nunca é
 * persistida.
 */
public class UsuarioDAO implements UsuarioRepository {
    /**
     * Busca um usuário pelo login, usado na autenticação.
     *
     * @return o usuário encontrado, ou {@code null} se não existir
     */
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

    /**
     * Busca um usuário pelo ID.
     *
     * @return o usuário encontrado, ou {@code null} se não existir
     */
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

    /**
     * Insere um novo usuário. Espera que {@code senhaHash} e {@code salt} já
     * tenham sido calculados pela camada de serviço antes de chegar aqui — este
     * DAO não faz hashing de senha.
     *
     * @return o ID gerado, ou -1 se não foi possível obtê-lo
     */
    public int inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (barbearia_id, login, senha_hash, salt) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuario.getBarbeariaId());
            pstmt.setString(2, usuario.getLogin());
            pstmt.setString(3, usuario.getSenhaHash());
            pstmt.setString(4, usuario.getSalt());
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
     * Atualiza login, hash de senha e salt de um usuário existente.
     */
    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET login = ?, senha_hash = ?, salt = ? WHERE id = ?";
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getLogin());
            pstmt.setString(2, usuario.getSenhaHash());
            pstmt.setString(3, usuario.getSalt());
            pstmt.setInt(4, usuario.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Converte a linha atual do {@link ResultSet} em um objeto {@link Usuario}.
     */
    private Usuario extrairUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int barbeariaId = rs.getInt("barbearia_id");
        String login = rs.getString("login");
        String senhaHash = rs.getString("senha_hash");
        Usuario usuario = new Usuario(id, barbeariaId, login, senhaHash);
        usuario.setSalt(rs.getString("salt"));
        return usuario;
    }
}
