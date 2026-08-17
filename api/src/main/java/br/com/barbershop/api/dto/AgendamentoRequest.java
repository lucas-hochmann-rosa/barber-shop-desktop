package br.com.barbershop.api.dto;

import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.StatusAgendamento;

import java.time.LocalDateTime;

/**
 * Payload para criação e edição de agendamento (RF05, RF06).
 */
public class AgendamentoRequest {
    private int barbeariaId;
    private int servicoId;
    private int barbeiroId;
    private String clienteNome;
    private String contato;
    private LocalDateTime dataHora;
    private OrigemContato origemContato;
    private StatusAgendamento status;
    private String motivoCancelamento;

    public AgendamentoRequest() {}

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

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }
}
