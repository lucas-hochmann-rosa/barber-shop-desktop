package br.com.barbershop.service;

import br.com.barbershop.dao.repository.UsuarioRepository;
import br.com.barbershop.model.Usuario;
import br.com.barbershop.util.HashUtil;
import java.sql.SQLException;

/**
 * Responsável pela autenticação de usuários. Também cuida do upgrade
 * silencioso e transparente de contas antigas, que ainda usam o hash
 * SHA-256 sem salt, para o esquema atual (PBKDF2 com salt por usuário).
 */
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida as credenciais informadas contra o usuário cadastrado.
     *
     * Se a conta já usa o esquema novo (possui salt), a senha é validada
     * diretamente com PBKDF2. Se a conta é antiga (sem salt), a senha é
     * validada pelo hash SHA-256 legado e, em caso de sucesso, a conta é
     * migrada nesse mesmo login: gera-se um salt novo, recalcula-se o hash
     * com PBKDF2 e grava-se no banco - sem exigir troca de senha do usuário.
     *
     * @param login login do usuário
     * @param senha senha em texto puro informada no formulário de login
     * @return o {@link Usuario} autenticado, ou {@code null} se o login não existir
     *         ou a senha não conferir
     * @throws SQLException em caso de falha de acesso ao banco de dados
     */
    public Usuario autenticar(String login, String senha) throws SQLException {
        Usuario usuario = usuarioRepository.buscarPorLogin(login);
        if (usuario == null) return null;

        if (usuario.getSalt() != null && !usuario.getSalt().isEmpty()) {
            String hash = HashUtil.hashComSalt(senha, usuario.getSalt());
            return hash.equals(usuario.getSenhaHash()) ? usuario : null;
        }

        // Conta criada antes da migração pra hash com salt: valida pelo SHA-256
        // legado e, se bater, faz upgrade silencioso pra PBKDF2 com salt nesse
        // mesmo login - não exige que o usuário troque a senha.
        if (HashUtil.hashSHA256(senha).equals(usuario.getSenhaHash())) {
            String novoSalt = HashUtil.gerarSalt();
            usuario.setSalt(novoSalt);
            usuario.setSenhaHash(HashUtil.hashComSalt(senha, novoSalt));
            usuarioRepository.atualizar(usuario);
            return usuario;
        }

        return null;
    }
}
