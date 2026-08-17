/* Cliente HTTP assíncrono para integração com o back-end Java Web Spring REST */

const Api = (() => {
    const API_BASE = window.location.port === '8080' ? '/api' : 'http://localhost:8080/api';
    let onlineCache = null;

    async function checarConexao() {
        try {
            const res = await fetch(`${API_BASE}/auth/session`, { method: 'GET', signal: AbortSignal.timeout(1500) });
            onlineCache = res.ok;
            return onlineCache;
        } catch {
            onlineCache = false;
            return false;
        }
    }

    async function requisicao(endpoint, options = {}) {
        const url = `${API_BASE}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        const config = {
            ...options,
            headers
        };

        const resposta = await fetch(url, config);
        const json = await resposta.json().catch(() => null);

        if (!resposta.ok) {
            const mensagem = (json && json.mensagem) ? json.mensagem : `Erro na requisição HTTP (${resposta.status})`;
            const erro = new Error(mensagem);
            erro.status = resposta.status;
            erro.dados = json;
            throw erro;
        }

        return json;
    }

    return {
        API_BASE,
        checarConexao,

        // Autenticação (RF02)
        async login(login, senha) {
            return requisicao('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ login, senha })
            });
        },

        async obterSessao() {
            return requisicao('/auth/session');
        },

        async logout() {
            return requisicao('/auth/logout', { method: 'POST' });
        },

        // Barbearia (RF03)
        async obterBarbearia() {
            return requisicao('/barbearia');
        },

        async atualizarBarbearia(dados) {
            return requisicao('/barbearia', {
                method: 'PUT',
                body: JSON.stringify(dados)
            });
        },

        async setupInicial(dados) {
            return requisicao('/barbearia/setup', {
                method: 'POST',
                body: JSON.stringify(dados)
            });
        },

        // Catálogo (RF03, RF04)
        async listarServicos(barbeariaId) {
            const query = barbeariaId ? `?barbeariaId=${barbeariaId}` : '';
            return requisicao(`/servicos${query}`);
        },

        async criarServico(servico) {
            return requisicao('/servicos', {
                method: 'POST',
                body: JSON.stringify(servico)
            });
        },

        async atualizarServico(id, servico) {
            return requisicao(`/servicos/${id}`, {
                method: 'PUT',
                body: JSON.stringify(servico)
            });
        },

        async excluirServico(id) {
            return requisicao(`/servicos/${id}`, { method: 'DELETE' });
        },

        async listarBarbeiros(barbeariaId) {
            const query = barbeariaId ? `?barbeariaId=${barbeariaId}` : '';
            return requisicao(`/barbeiros${query}`);
        },

        async criarBarbeiro(barbeiro) {
            return requisicao('/barbeiros', {
                method: 'POST',
                body: JSON.stringify(barbeiro)
            });
        },

        async atualizarBarbeiro(id, barbeiro) {
            return requisicao(`/barbeiros/${id}`, {
                method: 'PUT',
                body: JSON.stringify(barbeiro)
            });
        },

        async excluirBarbeiro(id) {
            return requisicao(`/barbeiros/${id}`, { method: 'DELETE' });
        },

        // Agenda e Atendimento (RF05, RF06, RF07, RF08, RF10, RF11)
        async listarAgendaHoje(barbeariaId) {
            const query = barbeariaId ? `?barbeariaId=${barbeariaId}` : '';
            return requisicao(`/agenda/hoje${query}`);
        },

        async listarTodosAgendamentos(barbeariaId) {
            const query = barbeariaId ? `?barbeariaId=${barbeariaId}` : '';
            return requisicao(`/agenda${query}`);
        },

        async buscarAgendamentoPorId(id) {
            return requisicao(`/agenda/${id}`);
        },

        async criarAgendamento(agendamento) {
            return requisicao('/agenda', {
                method: 'POST',
                body: JSON.stringify(agendamento)
            });
        },

        async atualizarAgendamento(id, agendamento) {
            return requisicao(`/agenda/${id}`, {
                method: 'PUT',
                body: JSON.stringify(agendamento)
            });
        },

        async excluirAgendamento(id) {
            return requisicao(`/agenda/${id}`, { method: 'DELETE' });
        },

        async iniciarAtendimento(id) {
            return requisicao(`/agenda/${id}/iniciar`, { method: 'POST' });
        },

        async concluirAtendimento(id) {
            return requisicao(`/agenda/${id}/concluir`, { method: 'POST' });
        },

        async cancelarAgendamento(id, motivo) {
            const query = motivo ? `?motivo=${encodeURIComponent(motivo)}` : '';
            return requisicao(`/agenda/${id}/cancelar${query}`, { method: 'POST' });
        },

        async verificarConflito(barbeiroId, dataHora, duracaoMinutos) {
            const query = `?barbeiroId=${barbeiroId}&dataHora=${encodeURIComponent(dataHora)}&duracaoMinutos=${duracaoMinutos}`;
            return requisicao(`/agenda/conflito${query}`);
        },

        // Histórico (RF09)
        async consultarHistorico({ inicio, fim, barbeiroId, status } = {}) {
            const params = new URLSearchParams();
            if (inicio) params.append('inicio', inicio);
            if (fim) params.append('fim', fim);
            if (barbeiroId) params.append('barbeiroId', barbeiroId);
            if (status) params.append('status', status);

            const queryString = params.toString() ? `?${params.toString()}` : '';
            return requisicao(`/historico${queryString}`);
        },

        // Relatórios (RF09)
        async gerarRelatorio(de, ate) {
            const params = new URLSearchParams();
            if (de) params.append('de', de);
            if (ate) params.append('ate', ate);

            const queryString = params.toString() ? `?${params.toString()}` : '';
            return requisicao(`/relatorios${queryString}`);
        }
    };
})();

if (typeof window !== 'undefined') {
    window.Api = Api;
}
