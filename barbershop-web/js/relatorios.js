/* ===========================================================================
   relatorios.js - faturamento, serviços mais vendidos e ranking (RF09)

   Só entram agendamentos CONCLUIDO, igual ao RelatorioDAO do desktop:
   cancelado não fatura e agendado ainda não aconteceu.
   =========================================================================== */

const Relatorios = {

    /** Quantas barras o gráfico mostra, no máximo. */
    MAX_BARRAS: 8,

    /* --- Seleção do período ------------------------------------------------- */

    periodo() {
        const de = document.getElementById('periodoDe').value;
        const ate = document.getElementById('periodoAte').value;
        return {
            inicio: de ? new Date(de + 'T00:00:00') : null,
            fim: ate ? new Date(ate + 'T23:59:59') : null
        };
    },

    concluidosNoPeriodo() {
        const { inicio, fim } = this.periodo();
        return Dados.agendamentos.filter((a) => {
            if (a.status !== StatusAgendamento.CONCLUIDO) return false;
            if (inicio && a.dataHora < inicio) return false;
            if (fim && a.dataHora > fim) return false;
            return true;
        });
    },

    /* --- Faturamento -------------------------------------------------------- */

    desenharFaturamento(concluidos) {
        const total = concluidos.reduce((soma, a) => soma + a.valor, 0);
        document.getElementById('faturamentoTotal').textContent = Formato.moeda(total);

        const grafico = document.getElementById('graficoFaturamento');
        const { inicio, fim } = this.periodo();

        if (!concluidos.length || !inicio || !fim) {
            grafico.innerHTML = '<p class="texto-secundario texto-pequeno">Sem dados no período.</p>';
            return;
        }

        // Divide o período em até MAX_BARRAS faixas iguais
        const umDia = 86400000;
        const dias = Math.max(1, Math.round((fim - inicio) / umDia));
        const faixas = Math.min(this.MAX_BARRAS, dias);
        const diasPorFaixa = Math.ceil(dias / faixas);

        const baldes = [];
        for (let i = 0; i < faixas; i++) {
            const comeco = new Date(inicio.getTime() + i * diasPorFaixa * umDia);
            const termina = new Date(Math.min(
                comeco.getTime() + diasPorFaixa * umDia - 1,
                fim.getTime()
            ));
            if (comeco > fim) break;
            baldes.push({ comeco, termina, total: 0 });
        }

        concluidos.forEach((a) => {
            const balde = baldes.find((b) => a.dataHora >= b.comeco && a.dataHora <= b.termina);
            if (balde) balde.total += a.valor;
        });

        const maior = Math.max(...baldes.map((b) => b.total), 1);

        grafico.innerHTML = baldes.map((b) => {
            const altura = Math.round((b.total / maior) * 100);
            const rotulo = String(b.comeco.getDate()).padStart(2, '0') + '/'
                         + String(b.comeco.getMonth() + 1).padStart(2, '0');
            return `
                <div class="grafico-barras__coluna">
                    <span class="grafico-barras__valor mono">${Math.round(b.total).toLocaleString('pt-BR')}</span>
                    <div class="grafico-barras__area">
                        <div class="grafico-barras__barra" style="height:${Math.max(altura, 2)}%"
                             title="${rotulo}: ${Formato.moeda(b.total)}"></div>
                    </div>
                    <span class="grafico-barras__rotulo mono">${rotulo}</span>
                </div>`;
        }).join('');

        // Alternativa em texto para quem usa leitor de tela
        grafico.setAttribute('aria-label',
            'Faturamento por faixa do período: '
            + baldes.map((b) => {
                const rotulo = String(b.comeco.getDate()).padStart(2, '0') + '/'
                             + String(b.comeco.getMonth() + 1).padStart(2, '0');
                return `${rotulo}, ${Formato.moeda(b.total)}`;
            }).join('; '));
    },

    /* --- Serviços mais vendidos --------------------------------------------- */

    desenharVendidos(concluidos) {
        const lista = document.getElementById('listaVendidos');
        const vazio = document.getElementById('vendidosVazio');

        const porServico = new Map();
        concluidos.forEach((a) => {
            const atual = porServico.get(a.servicoNome) || { quantidade: 0, total: 0 };
            atual.quantidade++;
            atual.total += a.valor;
            porServico.set(a.servicoNome, atual);
        });

        const ordenados = Array.from(porServico.entries())
            .map(([nome, dados]) => ({ nome, ...dados }))
            .sort((a, b) => b.quantidade - a.quantidade)
            .slice(0, 5);

        if (!ordenados.length) {
            lista.innerHTML = '';
            vazio.hidden = false;
            return;
        }
        vazio.hidden = true;

        const maior = ordenados[0].quantidade;

        lista.innerHTML = ordenados.map((item) => `
            <li class="barra-horizontal">
                <span class="barra-horizontal__nome">${item.nome}</span>
                <span class="barra-horizontal__trilho">
                    <span class="barra-horizontal__preenchimento"
                          style="width:${Math.round((item.quantidade / maior) * 100)}%"></span>
                </span>
                <span class="barra-horizontal__numero mono">${item.quantidade}</span>
            </li>`).join('');
    },

    /* --- Ranking de barbeiros ----------------------------------------------- */

    desenharRanking(concluidos) {
        const podio = document.getElementById('podio');
        const vazio = document.getElementById('rankingVazio');

        const porBarbeiro = new Map();
        concluidos.forEach((a) => {
            const atual = porBarbeiro.get(a.barbeiroId) || { quantidade: 0, total: 0 };
            atual.quantidade++;
            atual.total += a.valor;
            porBarbeiro.set(a.barbeiroId, atual);
        });

        const ordenados = Array.from(porBarbeiro.entries())
            .map(([id, dados]) => ({ barbeiro: Dados.barbeiroPorId(id), ...dados }))
            .filter((item) => item.barbeiro)
            .sort((a, b) => b.quantidade - a.quantidade)
            .slice(0, 3);

        if (!ordenados.length) {
            podio.innerHTML = '';
            vazio.hidden = false;
            return;
        }
        vazio.hidden = true;

        podio.innerHTML = ordenados.map((item, indice) => `
            <li class="podio__lugar">
                <span class="podio__posicao">${indice + 1}º</span>
                <img class="podio__foto" src="${item.barbeiro.imagem}" alt="" width="96" height="96">
                <span class="podio__nome">${item.barbeiro.nome}</span>
                <strong class="podio__numero">${item.quantidade} atendimentos</strong>
                <span class="texto-pequeno texto-secundario valor">${Formato.moeda(item.total)}</span>
            </li>`).join('');
    },

    /* --- Geração ------------------------------------------------------------ */

    gerar(evento) {
        if (evento) evento.preventDefault();
        const self = Relatorios;

        const { inicio, fim } = self.periodo();
        const aviso = document.getElementById('avisoRelatorio');

        if (inicio && fim && inicio > fim) {
            aviso.hidden = false;
            aviso.textContent = 'A data inicial não pode ser depois da data final.';
            return;
        }
        aviso.hidden = true;

        const concluidos = self.concluidosNoPeriodo();
        self.desenharFaturamento(concluidos);
        self.desenharVendidos(concluidos);
        self.desenharRanking(concluidos);
    },

    /* --- Partida ------------------------------------------------------------ */

    iniciar() {
        if (!document.getElementById('formPeriodo')) return;

        // Período padrão: últimos 30 dias, como no desktop
        const hoje = new Date();
        const trintaDias = new Date();
        trintaDias.setDate(trintaDias.getDate() - 30);

        document.getElementById('periodoDe').value = this.paraCampo(trintaDias);
        document.getElementById('periodoAte').value = this.paraCampo(hoje);

        document.getElementById('formPeriodo').addEventListener('submit', this.gerar);
        this.gerar();
    },

    paraCampo(data) {
        const mes = String(data.getMonth() + 1).padStart(2, '0');
        const dia = String(data.getDate()).padStart(2, '0');
        return `${data.getFullYear()}-${mes}-${dia}`;
    }
};

document.addEventListener('DOMContentLoaded', () => Relatorios.iniciar());
