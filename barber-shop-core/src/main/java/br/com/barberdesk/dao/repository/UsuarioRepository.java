package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Usuario;

import java.sql.SQLException;

/**
 * Contrato de persistência de {@link Usuario} consumido por
 * {@link br.com.barberdesk.service.AuthService} (autenticação e upgrade
 * de hash legado) e {@link br.com.barberdesk.service.SetupService}
 * (cadastro inicial).
 */
public interface UsuarioRepository {

    /** Busca um usuário pelo login, ou {@code null} se não existir. */
    Usuario buscarPorLogin(String login) throws SQLException;

    /** Atualiza um usuário existente (ex.: upgrade de hash/salt no login). */
    void atualizar(Usuario usuario) throws SQLException;

    /** Insere um novo usuário e devolve o id gerado. */
    int inserir(Usuario usuario) throws SQLException;
}
