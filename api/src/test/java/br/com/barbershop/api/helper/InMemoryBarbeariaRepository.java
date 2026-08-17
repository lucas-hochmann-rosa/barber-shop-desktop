package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.BarbeariaRepository;
import br.com.barbershop.model.Barbearia;

public class InMemoryBarbeariaRepository implements BarbeariaRepository {
    private Barbearia barbearia;

    public InMemoryBarbeariaRepository() {
        this.barbearia = new Barbearia(1, "Barbershop Matriz", "89800000", null, "Tradição");
    }

    public InMemoryBarbeariaRepository(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    @Override
    public Barbearia buscarPrimeira() {
        return barbearia;
    }

    @Override
    public Barbearia buscarPorId(int id) {
        return (barbearia != null && barbearia.getId() == id) ? barbearia : null;
    }

    @Override
    public int inserir(Barbearia b) {
        b.setId(1);
        this.barbearia = b;
        return 1;
    }

    @Override
    public void atualizar(Barbearia b) {
        this.barbearia = b;
    }
}
