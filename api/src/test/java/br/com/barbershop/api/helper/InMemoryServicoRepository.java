package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.ServicoRepository;
import br.com.barbershop.model.Servico;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InMemoryServicoRepository implements ServicoRepository {
    private final List<Servico> servicos = new ArrayList<>();
    private int nextId = 1;

    public InMemoryServicoRepository() {
        inserir(new Servico(1, 1, "Corte Masculino", new BigDecimal("45.00"), null, 30));
    }

    @Override
    public int inserir(Servico servico) {
        servico.setId(nextId++);
        servicos.add(servico);
        return servico.getId();
    }

    @Override
    public List<Servico> listarPorBarbearia(int barbeariaId) {
        return new ArrayList<>(servicos);
    }

    @Override
    public void atualizar(Servico servico) {
        for (int i = 0; i < servicos.size(); i++) {
            if (servicos.get(i).getId() == servico.getId()) {
                servicos.set(i, servico);
                return;
            }
        }
    }

    @Override
    public void deletar(int id) {
        servicos.removeIf(s -> s.getId() == id);
    }

    @Override
    public boolean existePorNome(int barbeariaId, String nome, int excluirId) {
        return servicos.stream()
                .anyMatch(s -> s.getId() != excluirId && s.getNome().equalsIgnoreCase(nome));
    }
}
