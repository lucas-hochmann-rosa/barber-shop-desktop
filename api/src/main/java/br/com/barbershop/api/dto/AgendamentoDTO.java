package br.com.barbershop.api.dto;

import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.ClassificacaoAgenda;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.StatusAgendamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferência de agendamentos com enriquecimento de classificação (RF11).
 */
public class AgendamentoDTO {
    private int id;
    private int barbeariaId;
    private int servicoId;
    private int barbeiroId;
    private String clienteNome;
    private String contato;
    private LocalDateTime dataHora;
    private OrigemContato origemContato;
    private StatusAgendamento status;
    private String servicoNome;
    private BigDecimal preco;
    private int duracaoMinutos;
    private String barbeiroNome;
    private String motivoCancelamento;
    private ClassificacaoAgenda classificacao;

    public AgendamentoDTO() {}

    public AgendamentoDTO(Agendamento a) {
        this(a, null);
    }

    public AgendamentoDTO(Agendamento a, ClassificacaoAgenda classificacao) {
        if (a != null) {
            this.id = a.getId();
            this.barbeariaId = a.getBarbeariaId();
            this.servicoId = a.getServicoId();
            this.barbeiroId = a.getBarbeiroId();
            this.clienteNome = a.getClienteNome();
            this.contato = a.getContato();
            this.dataHora = a.getDataHora();
            this.origemContato = a.getOrigemContato();
            this.status = a.getStatus();
            this.servicoNome = a.getServicoNome();
            this.duracaoMinutos = a.getDuracaoMinutos();
            this.barbeiroNome = a.getBarbeiroNome();
            this.motivoCancelamento = a.getMotivoCancelamento();
            this.classificacao = classificacao;
        }
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

    public int getServicoId() {
        return servicoId;
    }

    public void setServicoId(int servicoId) {
        this.servicoId = servicoId;
    }

    public int getBarbeiroId() {
        return barbeiroId;
    }

    public void setBarbeiroId(int barbeiroId) {
        this.barbeiroId = barbeiroId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public OrigemContato getOrigemContato() {
        return origemContato;
    }

    public void setOrigemContato(OrigemContato origemContato) {
        this.origemContato = origemContato;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public String getServicoNome() {
        return servicoNome;
    }

    public void setServicoNome(String servicoNome) {
        this.servicoNome = servicoNome;
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

    public String getBarbeiroNome() {
        return barbeiroNome;
    }

    public void setBarbeiroNome(String barbeiroNome) {
        this.barbeiroNome = barbeiroNome;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public ClassificacaoAgenda getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoAgenda classificacao) {
        this.classificacao = classificacao;
    }
}
