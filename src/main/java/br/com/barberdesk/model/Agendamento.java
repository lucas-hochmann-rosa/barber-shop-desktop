package br.com.barberdesk.model;

import java.time.LocalDateTime;

public class Agendamento {
    private int id;
    private int barbeariaId;
    private int servicoId;
    private int barbeiroId;
    private String clienteNome;
    private String contato;
    private LocalDateTime dataHora;
    private OrigemContato origemContato;
    private StatusAgendamento status;

    // Campos auxiliares para exibição (JOIN)
    private String servicoNome;
    private String barbeiroNome;

    public Agendamento() {
    }

    public Agendamento(int barbeariaId, int servicoId, int barbeiroId, String clienteNome,
                       String contato, LocalDateTime dataHora, OrigemContato origemContato,
                       StatusAgendamento status) {
        this.barbeariaId = barbeariaId;
        this.servicoId = servicoId;
        this.barbeiroId = barbeiroId;
        this.clienteNome = clienteNome;
        this.contato = contato;
        this.dataHora = dataHora;
        this.origemContato = origemContato;
        this.status = status;
    }

    public Agendamento(int id, int barbeariaId, int servicoId, int barbeiroId, String clienteNome,
                       String contato, LocalDateTime dataHora, OrigemContato origemContato,
                       StatusAgendamento status) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.servicoId = servicoId;
        this.barbeiroId = barbeiroId;
        this.clienteNome = clienteNome;
        this.contato = contato;
        this.dataHora = dataHora;
        this.origemContato = origemContato;
        this.status = status;
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

    public String getBarbeiroNome() {
        return barbeiroNome;
    }

    public void setBarbeiroNome(String barbeiroNome) {
        this.barbeiroNome = barbeiroNome;
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", clienteNome='" + clienteNome + '\'' +
                ", dataHora=" + dataHora +
                ", status=" + status +
                '}';
    }
}
