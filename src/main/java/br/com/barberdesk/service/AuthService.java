package br.com.barberdesk.service;

import br.com.barberdesk.dao.UsuarioDAO;
import br.com.barberdesk.model.Usuario;
import br.com.barberdesk.util.HashUtil;
import java.sql.SQLException;

public class AuthService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String login, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorLogin(login);
        if (usuario == null) return null;

        if (usuario.getSalt() != null && !usuario.getSalt().isEmpty()) {
            String hash = HashUtil.hashComSalt(senha, usuario.getSalt());
            return hash.equals(usuario.getSenhaHash()) ? usuario : null;
        }

        // Conta criada antes da migração pra hash com salt: valida pelo SHA-256
        // legado e, se bater, faz upgrade silencioso pra PBKDF2 com salt nesse
        // mesmo login — não exige que o usuário troque a senha.
        if (HashUtil.hashSHA256(senha).equals(usuario.getSenhaHash())) {
            String novoSalt = HashUtil.gerarSalt();
            usuario.setSalt(novoSalt);
            usuario.setSenhaHash(HashUtil.hashComSalt(senha, novoSalt));
            usuarioDAO.atualizar(usuario);
            return usuario;
        }

        return null;
    }
}
