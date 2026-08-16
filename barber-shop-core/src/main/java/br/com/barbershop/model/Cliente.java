package br.com.barbershop.model;

/**
 * Representa um cliente da barbearia, cadastrado a partir do nome e contato informados
 * em um {@link Agendamento} (não possui cadastro próprio via tela dedicada).
 */
public class Cliente {
    private int id;
    private int barbeariaId;
    private String nome;
    private String contato;

    public Cliente() {
    }

    public Cliente(int id, int barbeariaId, String nome, String contato) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.contato = contato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(int barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        return id == ((Cliente) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
