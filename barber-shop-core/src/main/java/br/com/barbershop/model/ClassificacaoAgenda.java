package br.com.barbershop.model;

/**
 * Classificação visual de um {@link Agendamento}, usada para colorir as
 * linhas das tabelas de agenda (RF11 - classificação visual por
 * status/proximidade). Calculada por
 * {@link br.com.barbershop.service.ClassificadorAgenda}.
 */
public enum ClassificacaoAgenda {
    /** Atendimento em andamento (status EM_ATENDIMENTO). */
    EM_ANDAMENTO,
    /** Ainda AGENDADO, mas o horário previsto já passou. */
    ATRASADO,
    /** Começa em até 1 hora. */
    IMINENTE,
    /** Começa entre 1 e 2 horas. */
    PROXIMO,
    /** Começa em mais de 2 horas. */
    DISTANTE,
    /** Atendimento concluído. */
    CONCLUIDO,
    /** Agendamento cancelado. */
    CANCELADO
}
