package br.com.barbershop.service.fake;

import br.com.barbershop.dao.repository.ClienteRepository;
import br.com.barbershop.model.Cliente;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório de clientes em memória, usado nos testes de
 * {@link br.com.barbershop.service.AgendaService} (registro automático de
 * cliente ao criar agendamento). Permite simular falha de acesso ao banco
 * via {@link #setFalharAoRegistrar(boolean)}, para testar o comportamento
 * de {@code AgendaService#criarAgendamento} quando o registro do cliente
 * falha mas o agendamento já foi persistido.
 */
public class FakeClienteRepository implements ClienteRepository {

    private final List<Cliente> dados = new ArrayList<>();
    private int proximoId = 1;
    private boolean falharAoRegistrar = false;

    public void setFalharAoRegistrar(boolean falharAoRegistrar) {
        this.falharAoRegistrar = falharAoRegistrar;
    }

    @Override
    public void registrar(int barbeariaId, String nome, String contato) throws SQLException {
        if (falharAoRegistrar) throw new SQLException("Falha simulada ao registrar cliente");
        for (Cliente c : dados) {
            if (c.getBarbeariaId() == barbeariaId && c.getContato() != null && c.getContato().equals(contato)) {
                c.setNome(nome);
                return;
            }
        }
        dados.add(new Cliente(proximoId++, barbeariaId, nome, contato));
    }

    @Override
    public List<Cliente> listarPorBarbearia(int barbeariaId) {
        List<Cliente> lista = new ArrayList<>();
        for (Cliente c : dados) {
            if (c.getBarbeariaId() == barbeariaId) lista.add(c);
        }
        return lista;
    }
}
