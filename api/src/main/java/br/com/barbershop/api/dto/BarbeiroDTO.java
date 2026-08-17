package br.com.barbershop.api.dto;

import br.com.barbershop.model.Barbeiro;

/**
 * DTO para profissionais barbeiros (RF04).
 */
public class BarbeiroDTO {
    private int id;
    private int barbeariaId;
    private String nome;
    private String imagemBase64;

    public BarbeiroDTO() {}

    public BarbeiroDTO(Barbeiro b) {
        if (b != null) {
            this.id = b.getId();
            this.barbeariaId = b.getBarbeariaId();
            this.nome = b.getNome();
            this.imagemBase64 = b.getImagemBase64();
        }
    }

    public Barbeiro toModel() {
        Barbeiro b = new Barbeiro(this.id, this.barbeariaId, this.nome, this.imagemBase64);
        return b;
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

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
}
