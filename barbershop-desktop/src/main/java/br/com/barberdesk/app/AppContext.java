package br.com.barberdesk.app;

import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Usuario;

/**
 * Singleton para gerenciar o contexto da aplicação (sessão).
 *
 * Sem sincronização proposital: é uma aplicação desktop de usuário único, toda a
 * interação acontece na Event Dispatch Thread do Swing, então não há concorrência
 * real de acesso a este estado.
 */
public class AppContext {
    private static AppContext instance;
    private Barbearia barbeariaAtual;
    private Usuario usuarioLogado;

    private AppContext() {}

    /**
     * Retorna a instância única do contexto da aplicação, criando-a na
     * primeira chamada (lazy initialization). Sem sincronização por design —
     * ver nota da classe sobre execução single-thread na EDT.
     */
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
    
    /**
     * Atalho para obter o id da barbearia atual sem precisar checar nulidade
     * na chamada. Retorna 0 se nenhuma barbearia estiver definida no contexto
     * (ex.: antes do login ou durante o cadastro inicial).
     */
    public int getBarbeariaId() {
        return barbeariaAtual != null ? barbeariaAtual.getId() : 0;
    }
}
