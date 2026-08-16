/* Régua do dia, cartões-resumo, grade de serviços e tabela
   de pendentes (RF08, RF11, RF07) */

const Agenda = {

    /** Redesenha tudo que depende dos dados/horário. */
    desenhar() {
        const agora = new Date();
        const doDia = Dados.doDia(agora);

        this.desenharRegua(doDia, agora);
        this.desenharResumo(doDia, agora);
        this.desenharTabela(agora);
    },

    /* Régua do dia */

    /** Converte um horário na posição percentual dentro do expediente. */
    posicao(data) {
        const { inicio, fim } = Dados.expediente;
        const minutos = data.getHours() * 60 + data.getMinutes() - inicio * 60;
        const total = (fim - inicio) * 60;
        return (minutos / total) * 100;
    },

    desenharRegua(doDia, agora) {
        const trilho = document.getElementById('reguaTrilho');
        const escala = document.getElementById('reguaEscala');
        if (!trilho) return;

        const { inicio, fim } = Dados.expediente;

        // Escala de horas (de 2 em 2, como no wireframe)
        if (!escala.dataset.pronta) {
            let marcas = '';
            for (let h = inicio; h <= fim; h += 2) {
                const pos = ((h - inicio) / (fim - inicio)) * 100;
                marcas += `<span class="regua__hora" style="left:${pos}%">${String(h).padStart(2, '0')}h</span>`;
            }
            escala.innerHTML = marcas;
            escala.dataset.pronta = '1';
        }

        // Blocos: um por agendamento do dia, posicionado pelo horário
        let blocos = '<span class="regua__barra"></span>';

        doDia.forEach((a) => {
            const pos = this.posicao(a.dataHora);
            if (pos < 0 || pos > 100) return; // fora do expediente exibido

            const cls = classificar(a, agora);
            const hora = Formato.hora(a.dataHora);

            blocos += `
                <button type="button" class="regua__bloco cls-${cls}" style="left:${pos}%"
                        aria-label="${hora}, ${a.clienteNome}, ${a.servicoNome}, ${RotuloClassificacao[cls]}">
                    <span class="regua__balao" role="tooltip">
                        <strong>${a.clienteNome}</strong>
                        ${a.servicoNome}
                        <span class="hora">${hora}</span> &middot; ${a.barbeiroNome}
                    </span>
                </button>`;
        });

        // Marcador da hora atual
        const posAgora = this.posicao(agora);
        if (posAgora >= 0 && posAgora <= 100) {
            blocos += `<span class="regua__agulha" style="left:${posAgora}%" aria-hidden="true"></span>`;
        }

        trilho.innerHTML = blocos;

        // Rótulo textual da hora atual: quando fora do expediente, explicita o status
        // (antes da abertura ou expediente encerrado), mantendo a indicação clara
        const rotuloAgora = document.getElementById('reguaAgora');
        if (rotuloAgora) {
            let estadoExpediente = '';
            if (posAgora < 0) {
                estadoExpediente = ' · antes do expediente';
            } else if (posAgora > 100) {
                estadoExpediente = ' · expediente encerrado';
            }
            rotuloAgora.textContent = 'agora · ' + Formato.hora(agora) + estadoExpediente;
        }
    },

    /* Cartões-resumo */

    desenharResumo(doDia, agora) {
        const atrasados = doDia.filter(
            (a) => classificar(a, agora) === ClassificacaoAgenda.ATRASADO
        ).length;

        const emAtendimento = doDia.filter(
            (a) => a.status === StatusAgendamento.EM_ATENDIMENTO
        ).length;

        // Previsto = tudo que não foi cancelado (o que já foi feito, mais o que ainda vem)
        const previsto = doDia
            .filter((a) => a.status !== StatusAgendamento.CANCELADO)
            .reduce((soma, a) => soma + a.valor, 0);

        this.texto('resumoHoje', doDia.length + (doDia.length === 1 ? ' atendimento' : ' atendimentos'));
        this.texto('resumoAtrasados', String(atrasados));
        this.texto('resumoEmAtendimento', String(emAtendimento));
        this.texto('resumoFaturamento', Formato.moeda(previsto));
    },

    texto(id, valor) {
        const alvo = document.getElementById(id);
        if (alvo) alvo.textContent = valor;
    },

    /* Grade de serviços (RF03) */

    desenharServicos() {
        const grade = document.getElementById('gradeServicos');
        if (!grade) return;

        grade.innerHTML = Dados.servicos.map((s) => `
            <li>
                <a class="cartao-servico" href="agendamento.html?servico=${s.id}">
                    <img class="cartao-servico__foto" src="${s.imagem}" alt="" width="320" height="180">
                    <span class="cartao-servico__nome">${s.nome}</span>
                    <span class="cartao-servico__preco valor">${Formato.moeda(s.preco)}</span>
                    <span class="cartao-servico__duracao texto-pequeno texto-secundario">${s.duracaoMinutos} min</span>
                </a>
            </li>`).join('');
    },

    /* Tabela de pendentes (RF08) */

    desenharTabela(agora) {
        const corpo = document.getElementById('corpoPendentes');
        const vazio = document.getElementById('pendentesVazio');
        if (!corpo) return;

        const pendentes = Dados.pendentesDeHoje();

        if (!pendentes.length) {
            corpo.innerHTML = '';
            if (vazio) vazio.hidden = false;
            return;
        }
        if (vazio) vazio.hidden = true;

        corpo.innerHTML = pendentes.map((a) => {
            const cls = classificar(a, agora);
            const podeIniciar = a.status === StatusAgendamento.AGENDADO;
            const podeConcluir = a.status === StatusAgendamento.EM_ATENDIMENTO;

            return `
            <tr>
                <td class="celula-faixa">
                    <span class="faixa-classificacao cls-${cls}" title="${RotuloClassificacao[cls]}"></span>
                    <span class="apenas-leitor-tela">${RotuloClassificacao[cls]}</span>
                </td>
                <td class="hora">${Formato.hora(a.dataHora)}</td>
                <td>${a.clienteNome}<br><span class="texto-pequeno texto-secundario">${a.contato}</span></td>
                <td>${a.servicoNome}</td>
                <td>${a.barbeiroNome}</td>
                <td><span class="selo selo--${a.status === StatusAgendamento.EM_ATENDIMENTO ? 'em-atendimento' : 'agendado'}">${RotuloStatus[a.status]}</span></td>
                <td>
                    <div class="grupo-acoes">
                        <button type="button" class="botao-acao" data-acao="iniciar" data-id="${a.id}"
                                ${podeIniciar ? '' : 'disabled'} title="Iniciar atendimento" aria-label="Iniciar atendimento de ${a.clienteNome}">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M8 5.5 18 12 8 18.5Z"/></svg>
                        </button>
                        <button type="button" class="botao-acao" data-acao="concluir" data-id="${a.id}"
                                ${podeConcluir ? '' : 'disabled'} title="Concluir atendimento" aria-label="Concluir atendimento de ${a.clienteNome}">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12.5 10 17.5 19 7"/></svg>
                        </button>
                        <a class="botao-acao" href="agendamento.html?id=${a.id}"
                           title="Editar agendamento" aria-label="Editar agendamento de ${a.clienteNome}">
                            <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20h4L19 9l-4-4L4 16Z"/><path d="M14 6l4 4"/></svg>
                        </a>
                    </div>
                </td>
            </tr>`;
        }).join('');
    },

    /* Ações (RF07) */

    tratarAcao(evento) {
        const botao = evento.target.closest('[data-acao]');
        if (!botao || botao.disabled) return;

        const agendamento = Dados.agendamentos.find((a) => a.id === Number(botao.dataset.id));
        if (!agendamento) return;

        if (botao.dataset.acao === 'iniciar') {
            agendamento.status = StatusAgendamento.EM_ATENDIMENTO;
            Agenda.anunciar(`Atendimento de ${agendamento.clienteNome} iniciado.`);
        } else if (botao.dataset.acao === 'concluir') {
            agendamento.status = StatusAgendamento.CONCLUIDO;
            Agenda.anunciar(`Atendimento de ${agendamento.clienteNome} concluído.`);
        }

        Agenda.desenhar();
    },

    /** Mensagem na própria página (nunca alert), anunciada por leitor de tela. */
    anunciar(mensagem) {
        const painel = document.getElementById('avisoAgenda');
        if (!painel) return;

        painel.hidden = false;
        painel.className = 'aviso aviso--sucesso';
        painel.textContent = mensagem;

        clearTimeout(this._temporizador);
        this._temporizador = setTimeout(() => { painel.hidden = true; }, 4000);
    },

    /* Partida */

    iniciar() {
        this.desenharServicos();
        this.desenhar();

        document.addEventListener('click', this.tratarAcao);

        // O marcador da hora atual anda sozinho, de minuto em minuto
        setInterval(() => this.desenhar(), 60000);
    }
};

document.addEventListener('DOMContentLoaded', () => Agenda.iniciar());
