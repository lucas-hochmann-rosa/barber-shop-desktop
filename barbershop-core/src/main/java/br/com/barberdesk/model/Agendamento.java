package br.com.barberdesk.model;

import java.time.LocalDateTime;

/**
 * Representa um agendamento de atendimento na barbearia - cliente, serviço, barbeiro,
 * data/hora, origem do contato e status. Mantém snapshots de nome/duração do serviço e
 * do barbeiro (ver campos abaixo) para preservar o histórico mesmo que o serviço ou o
 * barbeiro original sejam posteriormente editados ou excluídos.
 */
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

    // Nome do serviço/barbeiro para exibição: vem do snapshot salvo no agendamento
    // (colunas servico_nome_snapshot/barbeiro_nome_snapshot) ou, na ausência dele, do
    // JOIN com o cadastro atual - ver AgendamentoDAO. Preserva o nome histórico mesmo
    // que o serviço/barbeiro original seja renomeado ou excluído depois.
    private String servicoNome;
    private String barbeiroNome;

    // Snapshot da duração do serviço no momento do agendamento (minutos) - usada
    // para checar conflito por sobreposição real de horário mesmo que a duração
    // do serviço mude depois. Ver AgendamentoDAO.verificarConflito.
    private int duracaoMinutos = 30;

    // Preenchido só quando status = CANCELADO.
    private String motivoCancelamento;

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

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
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
