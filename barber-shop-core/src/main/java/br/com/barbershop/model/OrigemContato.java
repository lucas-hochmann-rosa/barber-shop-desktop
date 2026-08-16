package br.com.barbershop.model;

/**
 * Representa o canal pelo qual o cliente entrou em contato para marcar um agendamento.
 * Usado apenas para fins estatísticos/relatório, não afeta o fluxo de agendamento.
 */
public enum OrigemContato {
    INSTAGRAM,
    WHATSAPP,
    PRESENCIAL,
    TELEFONE,
    // Qualquer origem não coberta pelas opções acima.
    OUTRO
}
