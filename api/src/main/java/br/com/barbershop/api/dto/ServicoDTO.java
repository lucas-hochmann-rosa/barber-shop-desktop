package br.com.barbershop.api.dto;

import br.com.barbershop.model.Servico;

import java.math.BigDecimal;

/**
 * DTO para serviços oferecidos pela barbearia (RF03).
 */
public class ServicoDTO {
    private int id;
    private int barbeariaId;
    private String nome;
    private BigDecimal preco;
    private int duracaoMinutos = 30;
    private String imagemBase64;

    public ServicoDTO() {}

    public ServicoDTO(Servico s) {
        if (s != null) {
            this.id = s.getId();
            this.barbeariaId = s.getBarbeariaId();
            this.nome = s.getNome();
            this.preco = s.getPreco();
            this.duracaoMinutos = s.getDuracaoMinutos();
            this.imagemBase64 = s.getImagemBase64();
        }
    }

    public Servico toModel() {
        Servico s = new Servico(this.id, this.barbeariaId, this.nome, this.preco, this.imagemBase64, this.duracaoMinutos);
        return s;
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

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
}
