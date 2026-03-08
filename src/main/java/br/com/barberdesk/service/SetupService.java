package br.com.barberdesk.service;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import java.sql.SQLException;
import java.util.List;

public class SetupService {
    private BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private AuthService authService = new AuthService();

    public boolean existeBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira() != null;
    }

    public Barbearia obterBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira();
    }

    public int criarCadastroInicial(Barbearia barbearia, String login, String senha,
                                    List<Servico> servicos, List<Barbeiro> barbeiros) throws SQLException {
        // Inserir barbearia
        int barbeariaId = barbeariaDAO.inserir(barbearia);

        // Inserir usuário com senha hash
        String senhaHash = authService.gerarHashSenha(senha);
        Usuario usuario = new Usuario(barbeariaId, login, senhaHash);
        usuarioDAO.inserir(usuario);

        // Inserir serviços
        for (Servico servico : servicos) {
            servico.setBarbeariaId(barbeariaId);
            servicoDAO.inserir(servico);
        }

        // Inserir barbeiros
        for (Barbeiro barbeiro : barbeiros) {
            barbeiro.setBarbeariaId(barbeariaId);
            barbeiroDAO.inserir(barbeiro);
        }

        return barbeariaId;
    }
}
