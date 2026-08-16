package br.com.barberdesk.app;

import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.Session;
import br.com.barberdesk.model.Usuario;

/**
 * Guarda a sessão ativa (usuário logado + barbearia atual) durante a
 * execução da aplicação desktop - é o único lugar onde o resto da UI
 * descobre quem está logado e qual é a barbearia corrente.
 *
 * Antes existiam dois mecanismos concorrentes pra mesma coisa: este
 * AppContext, que guardava barbearia e usuário em dois campos mutáveis
 * separados (podiam ficar dessincronizados entre si, ex.: setar um sem o
 * outro), e {@link Session}, um objeto de domínio imutável que já
 * amarrava os dois juntos, mas só era usado como retorno de
 * {@code SessionService.iniciarSessao} - TelaLogin recebia a Session e
 * imediatamente a desmontava em duas chamadas separadas pro AppContext.
 * Unificados aqui: AppContext passa a guardar a própria {@link Session}
 * como estado mutável único (um valor por vez, trocado inteiro a cada
 * login), em vez de duplicar os campos que ela já expõe.
 *
 * Sem sincronização proposital: é uma aplicação desktop de usuário único,
 * toda a interação acontece na Event Dispatch Thread do Swing, então não
 * há concorrência real de acesso a este estado.
 */
public class AppContext {
    private static AppContext instance;
    private Session sessaoAtual;

    private AppContext() {}

    /**
     * Retorna a instância única do contexto da aplicação, criando-a na
     * primeira chamada (lazy initialization). Sem sincronização por design -
     * ver nota da classe sobre execução single-thread na EDT.
     */
    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    /** Sessão ativa (usuário + barbearia), ou {@code null} antes do login/cadastro inicial. */
    public Session getSessaoAtual() {
        return sessaoAtual;
    }

    /** Substitui a sessão ativa inteira - não há como atualizar só usuário ou só barbearia isoladamente. */
    public void setSessaoAtual(Session sessaoAtual) {
        this.sessaoAtual = sessaoAtual;
    }

    public Barbearia getBarbeariaAtual() {
        return sessaoAtual != null ? sessaoAtual.getBarbearia() : null;
    }

    public Usuario getUsuarioLogado() {
        return sessaoAtual != null ? sessaoAtual.getUsuario() : null;
    }

    /**
     * Atalho para obter o id da barbearia atual sem precisar checar nulidade
     * na chamada. Retorna 0 se nenhuma barbearia estiver definida no contexto
     * (ex.: antes do login ou durante o cadastro inicial).
     */
    public int getBarbeariaId() {
        Barbearia b = getBarbeariaAtual();
        return b != null ? b.getId() : 0;
    }
}
