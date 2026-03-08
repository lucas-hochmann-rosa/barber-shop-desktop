package br.com.barberdesk.util;

import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Usuario;

/**
 * Singleton para gerenciar o contexto da aplicação (sessão).
 */
public class AppContext {
    private static AppContext instance;
    private Barbearia barbeariaAtual;
    private Usuario usuarioLogado;

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public Barbearia getBarbeariaAtual() {
        return barbeariaAtual;
    }

    public void setBarbeariaAtual(Barbearia barbeariaAtual) {
        this.barbeariaAtual = barbeariaAtual;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
    
    public int getBarbeariaId() {
        return barbeariaAtual != null ? barbeariaAtual.getId() : 0;
    }
}
