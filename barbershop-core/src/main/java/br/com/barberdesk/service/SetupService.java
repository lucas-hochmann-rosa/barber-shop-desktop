package br.com.barberdesk.service;

import br.com.barberdesk.dao.repository.BarbeariaRepository;
import br.com.barberdesk.dao.repository.BarbeiroRepository;
import br.com.barberdesk.dao.repository.ServicoRepository;
import br.com.barberdesk.dao.repository.UsuarioRepository;
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
    private final BarbeariaRepository barbeariaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final BarbeiroRepository barbeiroRepository;

    public SetupService(BarbeariaRepository barbeariaRepository, UsuarioRepository usuarioRepository,
                         ServicoRepository servicoRepository, BarbeiroRepository barbeiroRepository) {
        this.barbeariaRepository = barbeariaRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoRepository = servicoRepository;
        this.barbeiroRepository = barbeiroRepository;
    }

    /**
     * Verifica se já existe alguma barbearia cadastrada no banco.
     *
     * @return {@code true} se já houver uma barbearia cadastrada; {@code false} caso contrário
     */
    public boolean existeBarbearia() throws SQLException {
        return barbeariaRepository.buscarPrimeira() != null;
    }

    /**
     * Obtém a barbearia cadastrada (o sistema assume uma única barbearia por
     * instalação, então basta buscar a primeira).
     *
     * @return a barbearia cadastrada, ou {@code null} se nenhuma existir
     */
    public Barbearia obterBarbearia() throws SQLException {
        return barbeariaRepository.buscarPrimeira();
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
     * @return o usuário administrador recém-criado, com o id gerado — a UI usa isso pra montar a
     *         sessão sem precisar buscar o usuário de novo no banco
     */
    public Usuario criarCadastroInicial(Barbearia barbearia, String login, String senha,
                                    List<Servico> servicos, List<Barbeiro> barbeiros) throws SQLException {
        int barbeariaId = barbeariaRepository.inserir(barbearia);
        barbearia.setId(barbeariaId);

        String salt = HashUtil.gerarSalt();
        Usuario usuario = new Usuario(barbeariaId, login, HashUtil.hashComSalt(senha, salt));
        usuario.setSalt(salt);
        int usuarioId = usuarioRepository.inserir(usuario);
        usuario.setId(usuarioId);

        for (Servico servico : servicos) {
            servico.setBarbeariaId(barbeariaId);
            servicoRepository.inserir(servico);
        }

        for (Barbeiro barbeiro : barbeiros) {
            barbeiro.setBarbeariaId(barbeariaId);
            barbeiroRepository.inserir(barbeiro);
        }

        return usuario;
    }
}
