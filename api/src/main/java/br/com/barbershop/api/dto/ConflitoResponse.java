package br.com.barbershop.api.dto;

/**
 * Resposta da verificação de conflito de horário (RF10).
 */
public class ConflitoResponse {
    private boolean temConflito;
    private boolean dentroDoExpediente;
    private String mensagem;

    public ConflitoResponse() {}

    public ConflitoResponse(boolean temConflito, boolean dentroDoExpediente, String mensagem) {
        this.temConflito = temConflito;
        this.dentroDoExpediente = dentroDoExpediente;
        this.mensagem = mensagem;
    }

    public boolean isTemConflito() {
        return temConflito;
    }

    public void setTemConflito(boolean temConflito) {
        this.temConflito = temConflito;
    }

    public boolean isDentroDoExpediente() {
        return dentroDoExpediente;
    }

    public void setDentroDoExpediente(boolean dentroDoExpediente) {
        this.dentroDoExpediente = dentroDoExpediente;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
