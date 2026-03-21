package br.com.barberdesk.model;

import java.math.BigDecimal;

public class Servico {
    private int id;
    private int barbeariaId;
    private String nome;
    private BigDecimal preco;
    private String imagemPath;

    public Servico() {
    }

    public Servico(int barbeariaId, String nome, BigDecimal preco, String imagemPath) {
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.preco = preco;
        this.imagemPath = imagemPath;
    }

    public Servico(int id, int barbeariaId, String nome, BigDecimal preco, String imagemPath) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.nome = nome;
        this.preco = preco;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
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

    // Igualdade por id: permite JComboBox.setSelectedItem(servico) encontrar o
    // item certo mesmo quando é uma instância diferente da carregada no combo.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servico)) return false;
        return id == ((Servico) o).id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
