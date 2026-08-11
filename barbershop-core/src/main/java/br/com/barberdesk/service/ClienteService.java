package br.com.barberdesk.service;

import br.com.barberdesk.dao.repository.ClienteRepository;
import br.com.barberdesk.model.Cliente;

import java.sql.SQLException;
import java.util.List;

/**
 * Consulta do diretório de clientes de uma barbearia (aba "Clientes" em
 * "Minha Barbearia"). O registro em si acontece automaticamente ao criar
 * um agendamento — ver {@link AgendaService#criarAgendamento} — não há
 * cadastro manual de cliente, então este service só expõe leitura.
 */
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /** Lista o diretório de clientes de uma barbearia. */
    public List<Cliente> listarPorBarbearia(int barbeariaId) throws SQLException {
        return clienteRepository.listarPorBarbearia(barbeariaId);
    }
}
