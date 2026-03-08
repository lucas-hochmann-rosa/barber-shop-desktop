package br.com.barberdesk.model;

public class Barbeiro {
    private int id;
    private int barbeariaId;
    private String nome;
    private String imagemPath;

    public Barbeiro() {
    }

    public Barbeiro(int barbeariaId, String nome, String imagemPath) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.imagemPath = imagemPath;
    }

    public Barbeiro(int id, int barbeariaId, String nome, String imagemPath) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.imagemPath = imagemPath;
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

    public String getImagemPath() {
        return imagemPath;
    }

    public void setImagemPath(String imagemPath) {
        this.imagemPath = imagemPath;
    }

    // Alias para compatibilidade com as telas
    public String getFotoCaminho() {
        return imagemPath;
    }

    public void setFotoCaminho(String fotoCaminho) {
        this.imagemPath = fotoCaminho;
    }

    @Override
    public String toString() {
        return nome;
    }
}
