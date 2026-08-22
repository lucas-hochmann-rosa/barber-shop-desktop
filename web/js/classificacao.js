/* RF11: classificação visual do agendamento.
   Regra espelhada da classe Java ClassificadorAgenda (core),
   coberta por testes unitários em ClassificadorAgendaTest.
   A data de referência é recebida por parâmetro para garantir testabilidade determinística. */

const ClassificacaoAgenda = {
    EM_ANDAMENTO: 'EM_ANDAMENTO',
    ATRASADO: 'ATRASADO',
    IMINENTE: 'IMINENTE',
    PROXIMO: 'PROXIMO',
    DISTANTE: 'DISTANTE',
    CONCLUIDO: 'CONCLUIDO',
    CANCELADO: 'CANCELADO'
};

const StatusAgendamento = {
    AGENDADO: 'AGENDADO',
    EM_ATENDIMENTO: 'EM_ATENDIMENTO',
    CONCLUIDO: 'CONCLUIDO',
    CANCELADO: 'CANCELADO'
};

/**
 * Classifica o agendamento conforme o status e, quando ainda pendente, a
 * proximidade do horário em relação à referência.
 *
 * @param {Object} agendamento objeto com { dataHora: Date, status: string }
 * @param {Date}   referencia  instante usado como "agora"
 * @returns {string} um valor de ClassificacaoAgenda
 * @throws {Error} se o agendamento ou a data/hora forem nulos
 */
function classificar(agendamento, referencia) {
    if (!agendamento) {
        throw new Error('Agendamento não pode ser nulo.');
    }
    if (!agendamento.dataHora) {
        throw new Error('Data/hora do agendamento não pode ser nula.');
    }

    const status = agendamento.status;
    if (status === StatusAgendamento.CANCELADO) {
        return ClassificacaoAgenda.CANCELADO;
    }
    if (status === StatusAgendamento.CONCLUIDO) {
        return ClassificacaoAgenda.CONCLUIDO;
    }
    if (status === StatusAgendamento.EM_ATENDIMENTO) {
        return ClassificacaoAgenda.EM_ANDAMENTO;
    }

    if (agendamento.dataHora < referencia) {
        return ClassificacaoAgenda.ATRASADO;
    }

    // Duration.between(...).toMinutes() no Java trunca; Math.floor faz o mesmo
    const minutosParaComecar = Math.floor((agendamento.dataHora - referencia) / 60000);
    if (minutosParaComecar <= 60) {
        return ClassificacaoAgenda.IMINENTE;
    }
    if (minutosParaComecar <= 120) {
        return ClassificacaoAgenda.PROXIMO;
    }
    return ClassificacaoAgenda.DISTANTE;
}

/** Rótulo legível de cada classificação, usado na legenda e nos balões. */
const RotuloClassificacao = {
    EM_ANDAMENTO: 'Em andamento',
    ATRASADO: 'Atrasado',
    IMINENTE: 'Começa em até 1h',
    PROXIMO: 'Começa em até 2h',
    DISTANTE: 'Mais de 2h',
    CONCLUIDO: 'Concluído',
    CANCELADO: 'Cancelado'
};

/** Rótulo legível de cada status. */
const RotuloStatus = {
    AGENDADO: 'Agendado',
    EM_ATENDIMENTO: 'Em atendimento',
    CONCLUIDO: 'Concluído',
    CANCELADO: 'Cancelado'
};
