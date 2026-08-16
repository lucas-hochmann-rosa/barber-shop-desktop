package br.com.barbershop.model;

/**
 * Representa a conta de acesso ao sistema (login/senha) vinculada a uma barbearia.
 * É a entidade usada para autenticação, distinta do conceito de {@link Barbeiro}
 * (profissional cadastrado para atendimento).
 */
public class Usuario {
    private int id;
    private int barbeariaId;
    private String login;
    private String senhaHash;
    // Nulo = conta criada antes da migração para hash com salt (ver AuthService.autenticar).
    private String salt;

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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
