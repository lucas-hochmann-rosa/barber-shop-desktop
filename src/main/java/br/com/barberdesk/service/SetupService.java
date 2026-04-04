package br.com.barberdesk.service;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import br.com.barberdesk.util.HashUtil;
import java.sql.SQLException;
import java.util.List;

public class SetupService {
    private BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();

    public boolean existeBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira() != null;
    }

    public Barbearia obterBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira();
    }

    /**
     * Cria barbearia, usuário admin (com hash de senha salgado) e os serviços/
     * barbeiros iniciais. barbearia recebe o id gerado (mutação do parâmetro).
     */
    public int criarCadastroInicial(Barbearia barbearia, String login, String senha,
                                    List<Servico> servicos, List<Barbeiro> barbeiros) throws SQLException {
        int barbeariaId = barbeariaDAO.inserir(barbearia);
        barbearia.setId(barbeariaId);

        String salt = HashUtil.gerarSalt();
        Usuario usuario = new Usuario(barbeariaId, login, HashUtil.hashComSalt(senha, salt));
        usuario.setSalt(salt);
        usuarioDAO.inserir(usuario);

        for (Servico servico : servicos) {
            servico.setBarbeariaId(barbeariaId);
            servicoDAO.inserir(servico);
        }

        for (Barbeiro barbeiro : barbeiros) {
            barbeiro.setBarbeariaId(barbeariaId);
            barbeiroDAO.inserir(barbeiro);
        }

        return barbeariaId;
    }
}
