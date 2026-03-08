package br.com.barberdesk.service;

import br.com.barberdesk.dao.UsuarioDAO;
import br.com.barberdesk.model.Usuario;
import br.com.barberdesk.util.HashUtil;
import java.sql.SQLException;

public class AuthService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario != null) {
            String senhaHash = HashUtil.hashSHA256(senha);
            if (usuario.getSenhaHash().equals(senhaHash)) {
                return usuario;
            }
        }
        return null;
    }

    public String gerarHashSenha(String senha) {
        return HashUtil.hashSHA256(senha);
    }
}
