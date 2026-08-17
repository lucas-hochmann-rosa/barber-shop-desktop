package br.com.barbershop.api.dto;

/**
 * Resposta genérica com mensagem de sucesso ou erro.
 */
public class MensagemResponse {
    private boolean sucesso;
    private String mensagem;

    public MensagemResponse() {}

    public MensagemResponse(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
