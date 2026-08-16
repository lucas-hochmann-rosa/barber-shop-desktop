package br.com.barberdesk.app;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.dao.BarbeariaDAO;
import br.com.barberdesk.dao.BarbeiroDAO;
import br.com.barberdesk.dao.ClienteDAO;
import br.com.barberdesk.dao.RelatorioDAO;
import br.com.barberdesk.dao.SchemaInitializer;
import br.com.barberdesk.dao.ServicoDAO;
import br.com.barberdesk.dao.UsuarioDAO;
import br.com.barberdesk.service.AgendaService;
import br.com.barberdesk.service.AuthService;
import br.com.barberdesk.service.BarbeariaService;
import br.com.barberdesk.service.CatalogoService;
import br.com.barberdesk.service.ClienteService;
import br.com.barberdesk.service.DatabaseInitService;
import br.com.barberdesk.service.RelatorioService;
import br.com.barberdesk.service.SessionService;
import br.com.barberdesk.service.SetupService;

/**
 * Único ponto de montagem do grafo de objetos da aplicação: instancia os
 * DAOs concretos e injeta nos services, via construtor. Nenhuma outra
 * classe do módulo desktop deveria instanciar um DAO diretamente - quem
 * precisa de acesso a dados pede um service já pronto daqui.
 *
 * Os DAOs em si são sem estado (cada método abre/fecha sua própria conexão
 * via {@link br.com.barberdesk.dao.ConexaoMySQL}), então uma única instância
 * de cada é suficiente e é reaproveitada entre todos os services criados
 * por esta fábrica.
 */
public class FabricaDeServicos {

    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    public AgendaService criarAgendaService() {
        return new AgendaService(agendamentoDAO, clienteDAO);
    }

    public AuthService criarAuthService() {
        return new AuthService(usuarioDAO);
    }

    public SessionService criarSessionService() {
        return new SessionService(criarAuthService(), barbeariaDAO);
    }

    public SetupService criarSetupService() {
        return new SetupService(barbeariaDAO, usuarioDAO, servicoDAO, barbeiroDAO);
    }

    public RelatorioService criarRelatorioService() {
        return new RelatorioService(relatorioDAO);
    }

    public DatabaseInitService criarDatabaseInitService() {
        return new DatabaseInitService(new SchemaInitializer());
    }

    public CatalogoService criarCatalogoService() {
        return new CatalogoService(servicoDAO, barbeiroDAO);
    }

    public ClienteService criarClienteService() {
        return new ClienteService(clienteDAO);
    }

    public BarbeariaService criarBarbeariaService() {
        return new BarbeariaService(barbeariaDAO);
    }
}
