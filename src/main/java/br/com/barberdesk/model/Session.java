package br.com.barberdesk.model;

/**
 * Representa a sessão do usuário logado.
 */
public class Session {
    private final Usuario usuario;
    private final Barbearia barbearia;

    public Session(Usuario usuario, Barbearia barbearia) {
        this.usuario = usuario;
        this.barbearia = barbearia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Barbearia getBarbearia() {
        return barbearia;
    }
}
