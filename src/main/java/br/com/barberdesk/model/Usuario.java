package br.com.barberdesk.model;

public class Usuario {
    private int id;
    private int barbeariaId;
    private String login;
    private String senhaHash;

    public Usuario() {
    }

    public Usuario(int barbeariaId, String login, String senhaHash) {
        this.barbeariaId = barbeariaId;
        this.login = login;
        this.senhaHash = senhaHash;
    }

    public Usuario(int id, int barbeariaId, String login, String senhaHash) {
        this.id = id;
        this.barbeariaId = barbeariaId;
        this.login = login;
        this.senhaHash = senhaHash;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", barbeariaId=" + barbeariaId +
                ", login='" + login + '\'' +
                '}';
    }
}
