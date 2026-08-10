package br.com.barberdesk.model;

/**
 * Representa o estágio do ciclo de vida de um {@link Agendamento}, do momento em que é
 * marcado até sua conclusão ou cancelamento.
 */
public enum StatusAgendamento {
    // Marcado, aguardando o horário do atendimento.
    AGENDADO,
    // Cliente está sendo atendido no momento.
    EM_ATENDIMENTO,
    // Atendimento finalizado com sucesso.
    CONCLUIDO,
    // Cancelado antes do atendimento; motivo fica em Agendamento.motivoCancelamento.
    CANCELADO
}
