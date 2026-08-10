package br.com.barberdesk.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa a barbearia cadastrada no sistema — dados institucionais (nome, CEP, data de
 * fundação, cultura/valores) e horário de funcionamento. O sistema é single-tenant por
 * instalação, mas as demais entidades (usuário, cliente, barbeiro, serviço, agendamento)
 * referenciam o id da barbearia para permitir múltiplas unidades no mesmo banco.
 */
public class Barbearia {
    private int id;
    private String nome;
    private String cep;
    private LocalDate dataFundacao;
    private String culturaValores;
    // Nulos = sem horário de funcionamento configurado (agendamento permitido a qualquer hora).
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    public Barbearia() {
    }

    public Barbearia(String nome, String cep, LocalDate dataFundacao, String culturaValores) {
        this.nome = nome;
        this.cep = cep;
        this.dataFundacao = dataFundacao;
        this.culturaValores = culturaValores;
    }

    public Barbearia(int id, String nome, String cep, LocalDate dataFundacao, String culturaValores) {
        this.id = id;
        this.nome = nome;
        this.cep = cep;
        this.dataFundacao = dataFundacao;
        this.culturaValores = culturaValores;
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

    @Override
    public String toString() {
        return "Barbearia{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cep='" + cep + '\'' +
                ", dataFundacao=" + dataFundacao +
                ", culturaValores='" + culturaValores + '\'' +
                '}';
    }
}
