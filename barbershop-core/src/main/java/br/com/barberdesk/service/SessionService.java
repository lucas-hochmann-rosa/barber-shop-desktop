package br.com.barberdesk.service;

import br.com.barberdesk.dao.BarbeariaDAO;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Session;
import br.com.barberdesk.model.Usuario;

import java.sql.SQLException;

/**
 * Centraliza o início de sessão (login) e garante que o contexto
 * tenha usuário + barbearia válidos.
 */
public class SessionService {
    private final AuthService authService = new AuthService();
    private final BarbeariaDAO barbeariaDAO = new BarbeariaDAO();

    /**
     * Autentica o usuário e monta a sessão com a barbearia vinculada a ele.
     *
     * Se o usuário não tiver uma barbearia vinculada (dado antigo/inconsistente),
     * recorre à primeira barbearia cadastrada no banco como fallback, em vez de
     * falhar — cenário raro, mas evita travar o login por causa de um vínculo
     * ausente.
     *
     * @param login login do usuário
     * @param senha senha em texto puro informada no formulário de login
     * @return a sessão com usuário e barbearia, ou {@code null} se as credenciais forem inválidas
     * @throws SQLException          em caso de falha de acesso ao banco de dados
     * @throws IllegalStateException se não houver nenhuma barbearia cadastrada no banco
     */
    public Session iniciarSessao(String login, String senha) throws SQLException {
        Usuario usuario = authService.autenticar(login, senha);
        if (usuario == null) return null;

        Barbearia b = null;
        if (usuario.getBarbeariaId() > 0) {
            b = barbeariaDAO.buscarPorId(usuario.getBarbeariaId());
        }

        // Fallback: caso o usuário esteja sem vínculo por algum dado antigo/inconsistente
        if (b == null) {
            b = barbeariaDAO.buscarPrimeira();
        }

        if (b == null) {
            throw new IllegalStateException("Nenhuma barbearia cadastrada no banco. Execute o cadastro inicial.");
        }

        return new Session(usuario, b);
    }
}
