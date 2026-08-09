package br.com.barberdesk.service;

import br.com.barberdesk.dao.*;
import br.com.barberdesk.model.*;
import br.com.barberdesk.util.HashUtil;
import java.sql.SQLException;
import java.util.List;

/**
 * Cuida do cadastro inicial da barbearia (empresa, usuário administrador,
 * serviços e barbeiros), o assistente de configuração exibido na primeira
 * execução do sistema.
 */
public class SetupService {
    private BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ServicoDAO servicoDAO = new ServicoDAO();
    private BarbeiroDAO barbeiroDAO = new BarbeiroDAO();

    /**
     * Verifica se já existe alguma barbearia cadastrada no banco.
     *
     * @return {@code true} se já houver uma barbearia cadastrada; {@code false} caso contrário
     */
    public boolean existeBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira() != null;
    }

    /**
     * Obtém a barbearia cadastrada (o sistema assume uma única barbearia por
     * instalação, então basta buscar a primeira).
     *
     * @return a barbearia cadastrada, ou {@code null} se nenhuma existir
     */
    public Barbearia obterBarbearia() throws SQLException {
        return barbeariaDAO.buscarPrimeira();
    }

    /**
     * Cria barbearia, usuário admin (com hash de senha salgado) e os serviços/
     * barbeiros iniciais. barbearia recebe o id gerado (mutação do parâmetro).
     *
     * @param barbearia dados da barbearia a cadastrar; recebe o id gerado após a inserção
     * @param login     login do usuário administrador criado
     * @param senha     senha em texto puro do usuário administrador (armazenada com salt + hash)
     * @param servicos  serviços iniciais a cadastrar, vinculados à barbearia criada
     * @param barbeiros barbeiros iniciais a cadastrar, vinculados à barbearia criada
     * @return o id gerado para a barbearia recém-criada
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
