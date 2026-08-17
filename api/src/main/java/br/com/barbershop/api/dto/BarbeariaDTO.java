package br.com.barbershop.api.dto;

import br.com.barbershop.model.Barbearia;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para transferência dos dados cadastrais da barbearia.
 */
public class BarbeariaDTO {
    private int id;
    private String nome;
    private String cep;
    private LocalDate dataFundacao;
    private String culturaValores;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    public BarbeariaDTO() {}

    public BarbeariaDTO(Barbearia barbearia) {
        if (barbearia != null) {
            this.id = barbearia.getId();
            this.nome = barbearia.getNome();
            this.cep = barbearia.getCep();
            this.dataFundacao = barbearia.getDataFundacao();
            this.culturaValores = barbearia.getCulturaValores();
            this.horarioAbertura = barbearia.getHorarioAbertura();
            this.horarioFechamento = barbearia.getHorarioFechamento();
        }
    }

    public Barbearia toModel() {
        Barbearia b = new Barbearia();
        b.setId(this.id);
        b.setNome(this.nome);
        b.setCep(this.cep);
        b.setDataFundacao(this.dataFundacao);
        b.setCulturaValores(this.culturaValores);
        b.setHorarioAbertura(this.horarioAbertura);
        b.setHorarioFechamento(this.horarioFechamento);
        return b;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public LocalDate getDataFundacao() {
        return dataFundacao;
    }

    public void setDataFundacao(LocalDate dataFundacao) {
        this.dataFundacao = dataFundacao;
    }

    public String getCulturaValores() {
        return culturaValores;
    }

    public void setCulturaValores(String culturaValores) {
        this.culturaValores = culturaValores;
    }

    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }
}
