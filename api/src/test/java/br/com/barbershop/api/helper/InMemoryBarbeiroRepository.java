package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.BarbeiroRepository;
import br.com.barbershop.model.Barbeiro;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBarbeiroRepository implements BarbeiroRepository {
    private final List<Barbeiro> barbeiros = new ArrayList<>();
    private int nextId = 1;

    public InMemoryBarbeiroRepository() {
        inserir(new Barbeiro(1, 1, "Carlos Silva", null));
    }

    @Override
    public int inserir(Barbeiro barbeiro) {
        barbeiro.setId(nextId++);
        barbeiros.add(barbeiro);
        return barbeiro.getId();
    }

    @Override
    public List<Barbeiro> listarPorBarbearia(int barbeariaId) {
        return new ArrayList<>(barbeiros);
    }

    @Override
    public void atualizar(Barbeiro barbeiro) {
        for (int i = 0; i < barbeiros.size(); i++) {
            if (barbeiros.get(i).getId() == barbeiro.getId()) {
                barbeiros.set(i, barbeiro);
                return;
            }
        }
    }

    @Override
    public void deletar(int id) {
        barbeiros.removeIf(b -> b.getId() == id);
    }

    @Override
    public boolean existePorNome(int barbeariaId, String nome, int excluirId) {
        return barbeiros.stream()
                .anyMatch(b -> b.getId() != excluirId && b.getNome().equalsIgnoreCase(nome));
    }
}
