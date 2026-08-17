package br.com.barbershop.api.dto;

/**
 * Resposta de autenticação bem-sucedida.
 */
public class LoginResponse {
    private boolean autenticado;
    private int usuarioId;
    private int barbeariaId;
    private String login;
    private String barbeariaNome;
    private String mensagem;

    public LoginResponse() {}

    public LoginResponse(boolean autenticado, int usuarioId, int barbeariaId, String login, String barbeariaNome, String mensagem) {
        this.autenticado = autenticado;
        this.usuarioId = usuarioId;
        this.barbeariaId = barbeariaId;
        this.login = login;
        this.barbeariaNome = barbeariaNome;
        this.mensagem = mensagem;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getBarbeariaId() {
        return barbeariaId;
    }

    public void setBarbeariaId(int barbeariaId) {
        this.barbeariaId = barbeariaId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getBarbeariaNome() {
        return barbeariaNome;
    }

    public void setBarbeariaNome(String barbeariaNome) {
        this.barbeariaNome = barbeariaNome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
