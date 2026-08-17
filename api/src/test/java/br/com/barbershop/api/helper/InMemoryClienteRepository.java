package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.ClienteRepository;
import br.com.barbershop.model.Cliente;

import java.util.ArrayList;
import java.util.List;

public class InMemoryClienteRepository implements ClienteRepository {
    private final List<Cliente> clientes = new ArrayList<>();
    private int nextId = 1;

    @Override
    public void registrar(int barbeariaId, String nome, String contato) {
        boolean existe = clientes.stream().anyMatch(c -> c.getBarbeariaId() == barbeariaId && c.getContato().equals(contato));
        if (!existe) {
            clientes.add(new Cliente(nextId++, barbeariaId, nome, contato));
        }
    }

    @Override
    public List<Cliente> listarPorBarbearia(int barbeariaId) {
        return new ArrayList<>(clientes);
    }
}
