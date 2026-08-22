/* Formulário de novo agendamento / edição (RF05, RF06, RF10) */

const TelaAgendamento = {

    /** Preenchido quando a página abre com ?id=, indicando edição. */
    emEdicao: null,

    /* Preparação dos campos */

    preencherSelecoes() {
        const servico = document.getElementById('servico');
        const barbeiro = document.getElementById('barbeiro');
        const origem = document.getElementById('origemContato');

        servico.innerHTML = '<option value="">Selecione…</option>'
            + Dados.servicos.map((s) =>
                `<option value="${s.id}">${s.nome} - ${Formato.moeda(s.preco)} (${s.duracaoMinutos} min)</option>`
            ).join('');

        barbeiro.innerHTML = '<option value="">Selecione…</option>'
            + Dados.barbeiros.map((b) => `<option value="${b.id}">${b.nome}</option>`).join('');

        const rotulos = {
            INSTAGRAM: 'Instagram', WHATSAPP: 'WhatsApp', PRESENCIAL: 'Presencial',
            TELEFONE: 'Telefone', OUTRO: 'Outro'
        };
        origem.innerHTML = Dados.origensContato
            .map((o) => `<option value="${o}">${rotulos[o]}</option>`).join('');
    },

    /** Lê ?servico= (vindo do card da agenda) ou ?id= (edição). */
    lerEndereco() {
        const parametros = new URLSearchParams(window.location.search);

        const idEdicao = parametros.get('id');
        if (idEdicao) {
            const agendamento = Dados.agendamentos.find((a) => a.id === Number(idEdicao));
            if (agendamento) {
                this.emEdicao = agendamento;
                this.carregarParaEdicao(agendamento);
                return;
            }
        }

        const servicoEscolhido = parametros.get('servico');
        if (servicoEscolhido && Dados.servicoPorId(servicoEscolhido)) {
            document.getElementById('servico').value = servicoEscolhido;
        }

        // Sugestão de data/hora: hoje, na próxima meia hora cheia
        const sugestao = new Date();
        sugestao.setMinutes(sugestao.getMinutes() + 30, 0, 0);
        sugestao.setMinutes(sugestao.getMinutes() < 30 ? 0 : 30);
        document.getElementById('data').value = this.paraCampoData(sugestao);
        document.getElementById('hora').value = this.paraCampoHora(sugestao);
    },

    carregarParaEdicao(a) {
        document.getElementById('tituloPagina').textContent = 'Editar agendamento';
        document.title = 'Editar agendamento · Barbershop';

        document.getElementById('clienteNome').value = a.clienteNome;
        document.getElementById('contato').value = a.contato;
        document.getElementById('origemContato').value = a.origemContato;
        document.getElementById('data').value = this.paraCampoData(a.dataHora);
        document.getElementById('hora').value = this.paraCampoHora(a.dataHora);
        document.getElementById('servico').value = a.servicoId;
        document.getElementById('barbeiro').value = a.barbeiroId;
        document.getElementById('observacoes').value = a.observacoes || '';

        // Excluir só faz sentido em cima de um agendamento existente (RF06)
        const excluir = document.getElementById('botaoExcluir');
        excluir.hidden = false;
        excluir.addEventListener('click', () => this.excluir());
    },

    paraCampoData(data) {
        const mes = String(data.getMonth() + 1).padStart(2, '0');
        const dia = String(data.getDate()).padStart(2, '0');
        return `${data.getFullYear()}-${mes}-${dia}`;
    },

    paraCampoHora(data) {
        return String(data.getHours()).padStart(2, '0') + ':'
            + String(data.getMinutes()).padStart(2, '0');
    },

    /** Junta os campos data + hora num Date, ou null se algum faltar. */
    dataHoraEscolhida() {
        const data = document.getElementById('data').value;
        const hora = document.getElementById('hora').value;
        if (!data || !hora) return null;
        return new Date(data + 'T' + hora);
    },

    servicoEscolhido() {
        return Dados.servicoPorId(document.getElementById('servico').value);
    },

    barbeiroEscolhido() {
        return Dados.barbeiroPorId(document.getElementById('barbeiro').value);
    },

    /* Painel de resumo */

    atualizarResumo() {
        const servico = this.servicoEscolhido();
        const barbeiro = this.barbeiroEscolhido();
        const quando = this.dataHoraEscolhida();

        document.getElementById('resumoServico').textContent = servico ? servico.nome : '-';
        document.getElementById('resumoDuracao').textContent = servico ? servico.duracaoMinutos + ' min' : '-';
        document.getElementById('resumoBarbeiro').textContent = barbeiro ? barbeiro.nome : '-';
        document.getElementById('resumoQuando').textContent = quando
            ? Formato.data(quando) + ' ' + Formato.hora(quando)
            : '-';
        document.getElementById('resumoTotal').textContent = Formato.moeda(servico ? servico.preco : 0);
    },

    /* Verificação de conflito (RF10) */

    verificarConflito() {
        const painel = document.getElementById('painelConflito');
        const servico = this.servicoEscolhido();
        const barbeiro = this.barbeiroEscolhido();
        const quando = this.dataHoraEscolhida();

        const mostrar = (texto, tipo) => {
            painel.className = 'aviso aviso--' + tipo;
            painel.textContent = texto;
        };

        if (!barbeiro || !quando || !servico) {
            mostrar('Escolha serviço, barbeiro, data e hora para conferir a disponibilidade.', 'neutro');
            return true;
        }

        if (!Dados.dentroDoHorario(quando, servico.duracaoMinutos)) {
            mostrar(
                `Fora do expediente: a barbearia atende das ${Dados.barbearia.horarioAbertura} `
                + `às ${Dados.barbearia.horarioFechamento}, e este serviço leva ${servico.duracaoMinutos} min.`,
                'alerta'
            );
            return false;
        }

        const ignorar = this.emEdicao ? this.emEdicao.id : null;
        const conflito = Dados.temConflito(barbeiro.id, quando, servico.duracaoMinutos, ignorar);

        if (conflito) {
            const fim = new Date(conflito.dataHora.getTime() + conflito.duracaoMinutos * 60000);
            mostrar(
                `Horário ocupado para este barbeiro: ${conflito.clienteNome} das `
                + `${Formato.hora(conflito.dataHora)} às ${Formato.hora(fim)} (${conflito.servicoNome}).`,
                'erro'
            );
            return false;
        }

        mostrar(`${barbeiro.nome} está livre neste horário.`, 'sucesso');
        return true;
    },

    /* Salvar */

    validar() {
        const formulario = document.getElementById('formAgendamento');
        Validacao.limparTodos(formulario);

        const clienteNome = document.getElementById('clienteNome');
        const contato = document.getElementById('contato');
        const data = document.getElementById('data');
        const hora = document.getElementById('hora');
        const servico = document.getElementById('servico');
        const barbeiro = document.getElementById('barbeiro');

        let valido = true;
        if (!Validacao.obrigatorio(clienteNome, 'Informe o nome do cliente.')) valido = false;

        if (!Validacao.obrigatorio(contato, 'Informe o contato do cliente.')) {
            valido = false;
        } else if (!Validacao.telefone(contato)) {
            valido = false;
        }

        if (!Validacao.obrigatorio(data, 'Escolha a data.')) {
            valido = false;
        } else if (!Validacao.dataNaoPassada(data, 'A data não pode ser anterior a hoje.')) {
            valido = false;
        }

        if (!Validacao.obrigatorio(hora, 'Escolha a hora.')) valido = false;
        if (!Validacao.obrigatorio(servico, 'Escolha o serviço.')) valido = false;
        if (!Validacao.obrigatorio(barbeiro, 'Escolha o barbeiro.')) valido = false;

        return valido;
    },

    salvar(evento) {
        evento.preventDefault();
        const self = TelaAgendamento;

        Validacao.avisar('#avisoForm', '');

        if (!self.validar()) {
            Validacao.avisar('#avisoForm', 'Confira os campos destacados abaixo.', 'erro');
            const primeiroErro = document.querySelector('[aria-invalid="true"]');
            if (primeiroErro) primeiroErro.focus();
            return;
        }

        if (!self.verificarConflito()) {
            Validacao.avisar('#avisoForm',
                'Não dá para salvar: veja a verificação de conflito ao lado.', 'erro');
            return;
        }

        const servico = self.servicoEscolhido();
        const barbeiro = self.barbeiroEscolhido();
        const quando = self.dataHoraEscolhida();

        if (self.emEdicao) {
            Object.assign(self.emEdicao, {
                clienteNome: document.getElementById('clienteNome').value.trim(),
                contato: document.getElementById('contato').value.trim(),
                origemContato: document.getElementById('origemContato').value,
                dataHora: quando,
                servicoId: servico.id,
                servicoNome: servico.nome,
                duracaoMinutos: servico.duracaoMinutos,
                valor: servico.preco,
                barbeiroId: barbeiro.id,
                barbeiroNome: barbeiro.nome,
                observacoes: document.getElementById('observacoes').value.trim()
            });
            Validacao.avisar('#avisoForm',
                'Agendamento atualizado com sucesso.',
                'sucesso');
            return;
        }

        Dados.agendamentos.push({
            id: Dados.proximoId(),
            barbeariaId: Dados.barbearia.id,
            servicoId: servico.id,
            barbeiroId: barbeiro.id,
            servicoNome: servico.nome,
            barbeiroNome: barbeiro.nome,
            duracaoMinutos: servico.duracaoMinutos,
            valor: servico.preco,
            clienteNome: document.getElementById('clienteNome').value.trim(),
            contato: document.getElementById('contato').value.trim(),
            dataHora: quando,
            origemContato: document.getElementById('origemContato').value,
            status: StatusAgendamento.AGENDADO,
            motivoCancelamento: null,
            observacoes: document.getElementById('observacoes').value.trim()
        });

        Validacao.avisar('#avisoForm',
            'Agendamento salvo com sucesso.',
            'sucesso');

        document.getElementById('formAgendamento').reset();
        self.atualizarResumo();
        self.verificarConflito();
        document.getElementById('clienteNome').focus();
    },

    excluir() {
        if (!this.emEdicao) return;

        const posicao = Dados.agendamentos.indexOf(this.emEdicao);
        if (posicao >= 0) Dados.agendamentos.splice(posicao, 1);

        this.emEdicao = null;
        document.getElementById('formAgendamento').reset();
        document.getElementById('botaoExcluir').hidden = true;
        this.atualizarResumo();

        Validacao.avisar('#avisoForm',
            'Agendamento excluído com sucesso.',
            'sucesso');
    },

    /* Partida */

    iniciar() {
        const formulario = document.getElementById('formAgendamento');
        if (!formulario) return;

        this.preencherSelecoes();
        this.lerEndereco();
        this.atualizarResumo();
        this.verificarConflito();

        Validacao.limparAoDigitar(formulario);

        // Resumo e conflito acompanham a escolha, sem esperar o Salvar
        ['servico', 'barbeiro', 'data', 'hora'].forEach((id) => {
            document.getElementById(id).addEventListener('change', () => {
                this.atualizarResumo();
                this.verificarConflito();
            });
        });

        formulario.addEventListener('submit', this.salvar);
    }
};

document.addEventListener('DOMContentLoaded', () => TelaAgendamento.iniciar());
