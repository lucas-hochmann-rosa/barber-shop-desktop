/* ===========================================================================
   historico.js - filtros, ordenação e paginação do histórico (RF09)
   Todos os filtros são aplicados em JavaScript sobre os dados já
   carregados; nada é buscado de novo.
   =========================================================================== */

const Historico = {

    POR_PAGINA: 12,

    filtrados: [],
    pagina: 1,
    ordem: { coluna: 'dataHora', crescente: false },

    /* --- Filtros ----------------------------------------------------------- */

    preencherBarbeiros() {
        const selecao = document.getElementById('filtroBarbeiro');
        selecao.innerHTML = '<option value="">Todos</option>'
            + Dados.barbeiros.map((b) => `<option value="${b.id}">${b.nome}</option>`).join('');
    },

    aplicarFiltros() {
        const de = document.getElementById('filtroDe').value;
        const ate = document.getElementById('filtroAte').value;
        const barbeiro = document.getElementById('filtroBarbeiro').value;
        const status = document.getElementById('filtroStatus').value;

        const inicio = de ? new Date(de + 'T00:00:00') : null;
        // "até" é inclusivo: vale o dia inteiro
        const fim = ate ? new Date(ate + 'T23:59:59') : null;

        this.filtrados = Dados.agendamentos.filter((a) => {
            if (inicio && a.dataHora < inicio) return false;
            if (fim && a.dataHora > fim) return false;
            if (barbeiro && a.barbeiroId !== Number(barbeiro)) return false;
            if (status && a.status !== status) return false;
            return true;
        });

        this.pagina = 1;
        this.ordenar();
    },

    limpar() {
        document.getElementById('formFiltros').reset();
        this.aplicarFiltros();
    },

    /* --- Ordenação ---------------------------------------------------------- */

    ordenar() {
        const { coluna, crescente } = this.ordem;
        const sinal = crescente ? 1 : -1;

        this.filtrados.sort((a, b) => {
            let x = a[coluna];
            let y = b[coluna];

            if (x instanceof Date) return (x - y) * sinal;
            if (typeof x === 'number') return (x - y) * sinal;

            x = String(x); y = String(y);
            return x.localeCompare(y, 'pt-BR') * sinal;
        });

        this.desenhar();
    },

    trocarOrdem(coluna) {
        if (this.ordem.coluna === coluna) {
            this.ordem.crescente = !this.ordem.crescente;
        } else {
            this.ordem.coluna = coluna;
            this.ordem.crescente = true;
        }
        this.pagina = 1;
        this.atualizarCabecalho();
        this.ordenar();
    },

    atualizarCabecalho() {
        document.querySelectorAll('.tabela th.ordenavel').forEach((th) => {
            const seta = th.querySelector('.seta');
            if (th.dataset.coluna === this.ordem.coluna) {
                th.setAttribute('aria-sort', this.ordem.crescente ? 'ascending' : 'descending');
                seta.textContent = this.ordem.crescente ? '▲' : '▼';
            } else {
                th.removeAttribute('aria-sort');
                seta.textContent = '▲';
            }
        });
    },

    /* --- Desenho ------------------------------------------------------------ */

    desenhar() {
        const corpo = document.getElementById('corpoHistorico');
        const vazio = document.getElementById('historicoVazio');
        const paginacao = document.getElementById('paginacao');
        const contagem = document.getElementById('contagem');

        const total = this.filtrados.length;
        contagem.textContent = total === 1
            ? '1 agendamento encontrado'
            : `${total} agendamentos encontrados`;

        if (!total) {
            corpo.innerHTML = '';
            vazio.hidden = false;
            paginacao.hidden = true;
            return;
        }

        vazio.hidden = true;

        const paginas = Math.ceil(total / this.POR_PAGINA);
        if (this.pagina > paginas) this.pagina = paginas;

        const comeco = (this.pagina - 1) * this.POR_PAGINA;
        const visiveis = this.filtrados.slice(comeco, comeco + this.POR_PAGINA);

        corpo.innerHTML = visiveis.map((a) => {
            const classeSelo = {
                AGENDADO: 'agendado',
                EM_ATENDIMENTO: 'em-atendimento',
                CONCLUIDO: 'concluido',
                CANCELADO: 'cancelado'
            }[a.status];

            const cancelado = a.status === StatusAgendamento.CANCELADO;

            return `
            <tr class="${cancelado ? 'linha-cancelada' : ''}">
                <td class="mono">${Formato.data(a.dataHora)}</td>
                <td class="hora">${Formato.hora(a.dataHora)}</td>
                <td>${a.clienteNome}</td>
                <td>${a.servicoNome}</td>
                <td>${a.barbeiroNome}</td>
                <td><span class="selo selo--${classeSelo}">${RotuloStatus[a.status]}</span></td>
                <td class="valor">${Formato.moeda(a.valor)}</td>
            </tr>`;
        }).join('');

        paginacao.hidden = paginas <= 1;
        document.getElementById('paginaEstado').textContent = `Página ${this.pagina} de ${paginas}`;
        document.getElementById('paginaAnterior').disabled = this.pagina === 1;
        document.getElementById('paginaProxima').disabled = this.pagina === paginas;
    },

    irPara(delta) {
        this.pagina += delta;
        this.desenhar();
    },

    /* --- Partida ------------------------------------------------------------ */

    iniciar() {
        if (!document.getElementById('corpoHistorico')) return;

        this.preencherBarbeiros();

        // Período padrão: últimos 30 dias até hoje
        const hoje = new Date();
        const trintaDias = new Date();
        trintaDias.setDate(trintaDias.getDate() - 30);
        document.getElementById('filtroDe').value = this.paraCampo(trintaDias);
        document.getElementById('filtroAte').value = this.paraCampo(hoje);

        this.aplicarFiltros();

        document.getElementById('formFiltros').addEventListener('submit', (evento) => {
            evento.preventDefault();
            this.aplicarFiltros();
        });

        document.getElementById('botaoLimpar').addEventListener('click', () => this.limpar());

        document.querySelectorAll('.tabela th.ordenavel button').forEach((botao) => {
            botao.addEventListener('click', () => {
                this.trocarOrdem(botao.closest('th').dataset.coluna);
            });
        });

        document.getElementById('paginaAnterior').addEventListener('click', () => this.irPara(-1));
        document.getElementById('paginaProxima').addEventListener('click', () => this.irPara(1));
    },

    paraCampo(data) {
        const mes = String(data.getMonth() + 1).padStart(2, '0');
        const dia = String(data.getDate()).padStart(2, '0');
        return `${data.getFullYear()}-${mes}-${dia}`;
    }
};

document.addEventListener('DOMContentLoaded', () => Historico.iniciar());
