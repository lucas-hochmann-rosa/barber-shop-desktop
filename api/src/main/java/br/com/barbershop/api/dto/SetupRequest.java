package br.com.barbershop.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Payload de cadastro inicial da barbearia (RF01).
 */
public class SetupRequest {
    private String nomeBarbearia;
    private String cep;
    private LocalDate dataFundacao;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private String culturaValores;

    private String loginAdmin;
    private String senhaAdmin;

    private String primeiroServicoNome;
    private BigDecimal primeiroServicoPreco;
    private int primeiroServicoDuracao;

    private String primeiroBarbeiroNome;

    public SetupRequest() {}

    public String getNomeBarbearia() {
        return nomeBarbearia;
    }

    public void setNomeBarbearia(String nomeBarbearia) {
        this.nomeBarbearia = nomeBarbearia;
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

    public String getCulturaValores() {
        return culturaValores;
    }

    public void setCulturaValores(String culturaValores) {
        this.culturaValores = culturaValores;
    }

    public String getLoginAdmin() {
        return loginAdmin;
    }

    public void setLoginAdmin(String loginAdmin) {
        this.loginAdmin = loginAdmin;
    }

    public String getSenhaAdmin() {
        return senhaAdmin;
    }

    public void setSenhaAdmin(String senhaAdmin) {
        this.senhaAdmin = senhaAdmin;
    }

    public String getPrimeiroServicoNome() {
        return primeiroServicoNome;
    }

    public void setPrimeiroServicoNome(String primeiroServicoNome) {
        this.primeiroServicoNome = primeiroServicoNome;
    }

    public BigDecimal getPrimeiroServicoPreco() {
        return primeiroServicoPreco;
    }

    public void setPrimeiroServicoPreco(BigDecimal primeiroServicoPreco) {
        this.primeiroServicoPreco = primeiroServicoPreco;
    }

    public int getPrimeiroServicoDuracao() {
        return primeiroServicoDuracao;
    }

    public void setPrimeiroServicoDuracao(int primeiroServicoDuracao) {
        this.primeiroServicoDuracao = primeiroServicoDuracao;
    }

    public String getPrimeiroBarbeiroNome() {
        return primeiroBarbeiroNome;
    }

    public void setPrimeiroBarbeiroNome(String primeiroBarbeiroNome) {
        this.primeiroBarbeiroNome = primeiroBarbeiroNome;
    }
}
