package br.com.barbershop.api.config;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Registra os serviços e DAOs do módulo core como Spring Beans.
 */
@Configuration
public class ServiceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConfig.class);

    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final BarbeariaDAO barbeariaDAO = new BarbeariaDAO();
    private final BarbeiroDAO barbeiroDAO = new BarbeiroDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ServicoDAO servicoDAO = new ServicoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final RelatorioDAO relatorioDAO = new RelatorioDAO();
    private final SchemaInitializer schemaInitializer = new SchemaInitializer();

    @PostConstruct
    public void inicializarBanco() {
        try {
            schemaInitializer.ensureSchema();
            logger.info("Schema do banco de dados verificado e inicializado com sucesso.");
        } catch (Exception e) {
            logger.warn("Não foi possível inicializar o banco de dados no startup (MySQL pode estar offline): {}", e.getMessage());
        }
    }

    @Bean
    public AgendamentoDAO agendamentoDAO() {
        return agendamentoDAO;
    }

    @Bean
    public BarbeariaDAO barbeariaDAO() {
        return barbeariaDAO;
    }

    @Bean
    public BarbeiroDAO barbeiroDAO() {
        return barbeiroDAO;
    }

    @Bean
    public ClienteDAO clienteDAO() {
        return clienteDAO;
    }

    @Bean
    public ServicoDAO servicoDAO() {
        return servicoDAO;
    }

    @Bean
    public UsuarioDAO usuarioDAO() {
        return usuarioDAO;
    }

    @Bean
    public RelatorioDAO relatorioDAO() {
        return relatorioDAO;
    }

    @Bean
    public AgendaService agendaService() {
        return new AgendaService(agendamentoDAO, clienteDAO);
    }

    @Bean
    public AuthService authService() {
        return new AuthService(usuarioDAO);
    }

    @Bean
    public SessionService sessionService() {
        return new SessionService(authService(), barbeariaDAO);
    }

    @Bean
    public SetupService setupService() {
        return new SetupService(barbeariaDAO, usuarioDAO, servicoDAO, barbeiroDAO);
    }

    @Bean
    public RelatorioService relatorioService() {
        return new RelatorioService(relatorioDAO);
    }

    @Bean
    public DatabaseInitService databaseInitService() {
        return new DatabaseInitService(schemaInitializer);
    }

    @Bean
    public CatalogoService catalogoService() {
        return new CatalogoService(servicoDAO, barbeiroDAO);
    }

    @Bean
    public ClienteService clienteService() {
        return new ClienteService(clienteDAO);
    }

    @Bean
    public BarbeariaService barbeariaService() {
        return new BarbeariaService(barbeariaDAO);
    }
}
