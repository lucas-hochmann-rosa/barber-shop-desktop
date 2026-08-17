/* Dados de exemplo do Barbershop Web

   Etapa 8 não tem back-end: tudo aqui é fixo, em memória. Na próxima etapa
   estes objetos serão substituídos por respostas da API, mantendo o mesmo
   formato (os nomes dos campos seguem os das entidades Java do
   core: servicoId, barbeiroId, clienteNome, dataHora, status...).

   Os agendamentos de HOJE são montados a partir da data atual para que a
   régua do dia e a classificação (RF11) tenham sempre o que mostrar,
   independentemente do dia em que a página for aberta. O histórico dos
   últimos 60 dias é gerado com um sorteio de semente fixa, então os
   relatórios dão o mesmo resultado a cada carregamento. */

/** Sorteio determinístico (mulberry32): mesma semente, mesma sequência. */
function _sorteio(semente) {
    return function () {
        semente |= 0;
        semente = (semente + 0x6D2B79F5) | 0;
        let t = Math.imul(semente ^ (semente >>> 15), 1 | semente);
        t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
        return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
}

const Dados = {

    barbearia: {
        id: 1,
        nome: 'Barbearia do Lucas',
        cep: '90010-150',
        dataFundacao: '2019-03-12',
        culturaValores: 'Atendimento sem pressa, corte bem feito e conversa boa. '
            + 'Cliente que senta na cadeira sai satisfeito ou não paga.',
        horarioAbertura: '08:00',
        horarioFechamento: '20:00'
    },

    usuario: {
        login: 'lucas',
        nome: 'Lucas Rosa'
    },

    /** Expediente exibido na régua do dia. */
    expediente: { inicio: 8, fim: 20 },

    servicos: [
        { id: 1, nome: 'Corte masculino', preco: 45, duracaoMinutos: 30, imagem: 'img/servico-corte.svg' },
        { id: 2, nome: 'Barba tradicional', preco: 35, duracaoMinutos: 30, imagem: 'img/servico-barba.svg' },
        { id: 3, nome: 'Corte + barba', preco: 70, duracaoMinutos: 60, imagem: 'img/servico-combo.svg' },
        { id: 4, nome: 'Pezinho', preco: 20, duracaoMinutos: 15, imagem: 'img/servico-pezinho.svg' },
        { id: 5, nome: 'Sobrancelha', preco: 15, duracaoMinutos: 15, imagem: 'img/servico-sobrancelha.svg' },
        { id: 6, nome: 'Platinado', preco: 120, duracaoMinutos: 90, imagem: 'img/servico-platinado.svg' }
    ],

    barbeiros: [
        { id: 1, nome: 'Rafael Menezes', imagem: 'img/avatar-1.svg' },
        { id: 2, nome: 'Diego Torres', imagem: 'img/avatar-2.svg' },
        { id: 3, nome: 'Bruno Alencar', imagem: 'img/avatar-3.svg' },
        { id: 4, nome: 'Igor Prado', imagem: 'img/avatar-4.svg' }
    ],

    origensContato: ['INSTAGRAM', 'WHATSAPP', 'PRESENCIAL', 'TELEFONE', 'OUTRO'],

    /** Preenchido por montar(); ver o final do arquivo. */
    agendamentos: [],

    /* Consultas auxiliares */

    servicoPorId(id) {
        return this.servicos.find((s) => s.id === Number(id)) || null;
    },

    barbeiroPorId(id) {
        return this.barbeiros.find((b) => b.id === Number(id)) || null;
    },

    /** Agendamentos de um dia específico, em ordem de horário. */
    doDia(data) {
        const dia = data.toDateString();
        return this.agendamentos
            .filter((a) => a.dataHora.toDateString() === dia)
            .sort((a, b) => a.dataHora - b.dataHora);
    },

    /** Pendentes de hoje: AGENDADO ou EM_ATENDIMENTO (RF08). */
    pendentesDeHoje() {
        return this.doDia(new Date()).filter(
            (a) => a.status === StatusAgendamento.AGENDADO
                || a.status === StatusAgendamento.EM_ATENDIMENTO
        );
    },

    /**
     * RF10 - conflito por barbeiro: há sobreposição real de intervalo?
     * Mesma regra do AgendamentoDAO.verificarConflito do desktop: compara
     * início/fim considerando a duração de cada atendimento e ignora
     * cancelados e concluídos.
     */
    temConflito(barbeiroId, dataHora, duracaoMinutos, ignorarId) {
        if (!barbeiroId || !dataHora) return null;

        const inicioNovo = dataHora.getTime();
        const fimNovo = inicioNovo + (duracaoMinutos || 30) * 60000;

        return this.agendamentos.find((a) => {
            if (a.barbeiroId !== Number(barbeiroId)) return false;
            if (ignorarId && a.id === Number(ignorarId)) return false;
            if (a.status === StatusAgendamento.CANCELADO
                || a.status === StatusAgendamento.CONCLUIDO) return false;

            const inicioExistente = a.dataHora.getTime();
            const fimExistente = inicioExistente + (a.duracaoMinutos || 30) * 60000;
            return inicioExistente < fimNovo && fimExistente > inicioNovo;
        }) || null;
    },

    /** Horário de funcionamento da barbearia (mesma regra do AgendaService). */
    dentroDoHorario(dataHora, duracaoMinutos) {
        const [ha, ma] = this.barbearia.horarioAbertura.split(':').map(Number);
        const [hf, mf] = this.barbearia.horarioFechamento.split(':').map(Number);

        const minutoInicio = dataHora.getHours() * 60 + dataHora.getMinutes();
        const minutoFim = minutoInicio + (duracaoMinutos || 30);
        return minutoInicio >= ha * 60 + ma && minutoFim <= hf * 60 + mf;
    },

    /** Próximo id livre, para novos agendamentos criados na tela. */
    proximoId() {
        return this.agendamentos.reduce((maior, a) => Math.max(maior, a.id), 0) + 1;
    }
};

/* Montagem dos agendamentos */

(function montar() {
    const CLIENTES = [
        ['Marcelo Antunes', '(49) 99999-9901'], ['Vinícius Prado', '(49) 99999-9902'],
        ['Otávio Bastos', '(49) 99999-9903'], ['Henrique Sales', '(49) 99999-9904'],
        ['Caio Ferraz', '(49) 99999-9905'], ['Eduardo Lima', '(49) 99999-9906'],
        ['Rodrigo Vianna', '(49) 99999-9907'], ['Thiago Moraes', '(49) 99999-9908'],
        ['Gustavo Reis', '(49) 99999-9909'], ['Felipe Barcelos', '(49) 99999-9910'],
        ['André Kuhn', '(49) 99999-9911'], ['Leandro Pires', '(49) 99999-9912'],
        ['Murilo Tavares', '(49) 99999-9913'], ['Sérgio Andrade', '(49) 99999-9914'],
        ['Paulo Meirelles', '(49) 99999-9915'], ['Ricardo Nunes', '(49) 99999-9916']
    ];

    let sequencia = 1;

    /** Monta um agendamento já com os campos derivados (snapshot de nome/valor). */
    function criar(dataHora, servicoId, barbeiroId, indiceCliente, status, origem) {
        const servico = Dados.servicoPorId(servicoId);
        const barbeiro = Dados.barbeiroPorId(barbeiroId);
        const cliente = CLIENTES[indiceCliente % CLIENTES.length];

        return {
            id: sequencia++,
            barbeariaId: 1,
            servicoId: servico.id,
            barbeiroId: barbeiro.id,
            servicoNome: servico.nome,
            barbeiroNome: barbeiro.nome,
            duracaoMinutos: servico.duracaoMinutos,
            valor: servico.preco,
            clienteNome: cliente[0],
            contato: cliente[1],
            dataHora: dataHora,
            origemContato: origem,
            status: status,
            motivoCancelamento: null,
            observacoes: ''
        };
    }

    const agora = new Date();
    const lista = [];

    /* Dia de hoje: oito atendimentos posicionados em relação ao horário
       atual (e não em horas fixas). É de propósito: assim a régua e as
       faixas do RF11 têm sempre o que mostrar, a qualquer hora em que a
       página for aberta - do contrário, abrindo às 19h o dia inteiro já
       estaria concluído e a tela pareceria vazia.
       Os minutos abaixo são a distância até agora; os status vêm junto
       para garantir um exemplo de cada faixa (atrasado, em andamento,
       iminente, próximo e distante). */
    const DIA = [
        { minutos: -205, servico: 1, barbeiro: 1, cliente: 0, origem: 'WHATSAPP', status: StatusAgendamento.CONCLUIDO },
        { minutos: -145, servico: 3, barbeiro: 2, cliente: 1, origem: 'INSTAGRAM', status: StatusAgendamento.CONCLUIDO },
        { minutos: -85, servico: 2, barbeiro: 1, cliente: 2, origem: 'PRESENCIAL', status: StatusAgendamento.CONCLUIDO },
        { minutos: -45, servico: 1, barbeiro: 3, cliente: 3, origem: 'TELEFONE', status: StatusAgendamento.AGENDADO },
        { minutos: -12, servico: 4, barbeiro: 2, cliente: 4, origem: 'WHATSAPP', status: StatusAgendamento.EM_ATENDIMENTO },
        { minutos: +35, servico: 6, barbeiro: 4, cliente: 5, origem: 'INSTAGRAM', status: StatusAgendamento.AGENDADO },
        { minutos: +95, servico: 3, barbeiro: 1, cliente: 6, origem: 'WHATSAPP', status: StatusAgendamento.AGENDADO },
        { minutos: +190, servico: 2, barbeiro: 3, cliente: 7, origem: 'PRESENCIAL', status: StatusAgendamento.AGENDADO }
    ];

    /* Se a página for aberta fora do expediente (antes das 08h ou a partir das
       20h), ancoramos os agendamentos de exemplo em 14h (meio do expediente) em vez
       da hora atual. Isso evita que os blocos se empilhem nas bordas ou fiquem fora
       da régua, mantendo a demonstração sempre legível com agendamentos bem
       distribuídos em todos os status. Durante o expediente, mantemos a ancoragem
       na hora atual, comprimindo proporcionalmente apenas quando próximo aos limites. */
    const minutoAgora = agora.getHours() * 60 + agora.getMinutes();
    const inicioMin = Dados.expediente.inicio * 60;
    const fimMin = Dados.expediente.fim * 60;
    const foraDoExpediente = minutoAgora < inicioMin || minutoAgora >= fimMin;

    const minutoBase = foraDoExpediente ? 14 * 60 : minutoAgora;
    const MAIOR_PASSADO = 205;
    const MAIOR_FUTURO = 190;

    const decorrido = minutoBase - inicioMin;
    const restante = (fimMin - 30) - minutoBase;

    const fatorPassado = Math.max(0, Math.min(1, decorrido / MAIOR_PASSADO));
    const fatorFuturo = Math.max(0, Math.min(1, restante / MAIOR_FUTURO));

    const usados = [];

    function horarioDe(minutosDeDiferenca) {
        const fator = minutosDeDiferenca < 0 ? fatorPassado : fatorFuturo;
        let minuto = minutoBase + Math.round(minutosDeDiferenca * fator);

        const primeiro = Dados.expediente.inicio * 60;
        const ultimo = Dados.expediente.fim * 60 - 5;

        minuto = Math.round(minuto / 5) * 5;
        minuto = Math.max(primeiro, Math.min(ultimo, minuto));

        // Garante horários distintos após o arredondamento, sem sair do
        // expediente: tenta para frente e, no fim do dia, para trás
        while (usados.includes(minuto)) {
            minuto += 5;
            if (minuto > ultimo) {
                minuto = Math.min(...usados) - 5;
                if (minuto < primeiro) break;
            }
        }
        usados.push(minuto);

        const d = new Date(agora);
        d.setHours(Math.floor(minuto / 60), minuto % 60, 0, 0);
        return d;
    }

    DIA.forEach((item) => {
        lista.push(criar(
            horarioDe(item.minutos),
            item.servico, item.barbeiro, item.cliente, item.status, item.origem
        ));
    });

    /* Histórico dos 60 dias anteriores, para alimentar os relatórios. */
    const sorteia = _sorteio(20260813);
    for (let diasAtras = 1; diasAtras <= 60; diasAtras++) {
        const data = new Date();
        data.setDate(data.getDate() - diasAtras);

        // Domingo é fechado
        if (data.getDay() === 0) continue;

        const quantidade = 4 + Math.floor(sorteia() * 5);
        for (let i = 0; i < quantidade; i++) {
            const hora = 8 + Math.floor(sorteia() * 11);
            const minuto = sorteia() < 0.5 ? 0 : 30;
            const inicio = new Date(data);
            inicio.setHours(hora, minuto, 0, 0);

            const servicoId = Dados.servicos[Math.floor(sorteia() * Dados.servicos.length)].id;
            const barbeiroId = Dados.barbeiros[Math.floor(sorteia() * Dados.barbeiros.length)].id;
            const cliente = Math.floor(sorteia() * CLIENTES.length);
            const origem = Dados.origensContato[Math.floor(sorteia() * Dados.origensContato.length)];

            // Uma parcela pequena foi cancelada, como acontece de verdade
            const status = sorteia() < 0.08
                ? StatusAgendamento.CANCELADO
                : StatusAgendamento.CONCLUIDO;

            const agendamento = criar(inicio, servicoId, barbeiroId, cliente, status, origem);
            if (status === StatusAgendamento.CANCELADO) {
                agendamento.motivoCancelamento = 'Cliente desmarcou';
            }
            lista.push(agendamento);
        }
    }

    Dados.agendamentos = lista;
})();
