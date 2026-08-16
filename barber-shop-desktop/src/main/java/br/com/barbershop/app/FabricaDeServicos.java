package br.com.barbershop.app;

import br.com.barbershop.dao.AgendamentoDAO;
import br.com.barbershop.dao.BarbeariaDAO;
import br.com.barbershop.dao.BarbeiroDAO;
import br.com.barbershop.dao.ClienteDAO;
import br.com.barbershop.dao.RelatorioDAO;
import br.com.barbershop.dao.SchemaInitializer;
import br.com.barbershop.dao.ServicoDAO;
import br.com.barbershop.dao.UsuarioDAO;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.AuthService;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.CatalogoService;
import br.com.barbershop.service.ClienteService;
import br.com.barbershop.service.DatabaseInitService;
import br.com.barbershop.service.RelatorioService;
import br.com.barbershop.service.SessionService;
import br.com.barbershop.service.SetupService;

/**
 * Único ponto de montagem do grafo de objetos da aplicação: instancia os
 * DAOs concretos e injeta nos services, via construtor. Nenhuma outra
 * classe do módulo desktop deveria instanciar um DAO diretamente - quem
 * precisa de acesso a dados pede um service já pronto daqui.
 *
 * Os DAOs em si são sem estado (cada método abre/fecha sua própria conexão
 * via {@link br.com.barbershop.dao.ConexaoMySQL}), então uma única instância
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
