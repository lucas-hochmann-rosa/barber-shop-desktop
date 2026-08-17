package br.com.barbershop.service;

import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.ClassificacaoAgenda;
import br.com.barbershop.model.StatusAgendamento;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Calcula a classificação visual de um agendamento (RF11 - classificação
 * por status/proximidade), usada pelas tabelas de agenda para colorir cada
 * linha: verde para atendimento em andamento, vermelho para atrasado,
 * laranja para começar em até 1 hora, amarelo para até 2 horas e cinza para
 * mais de 2 horas.
 * <p>
 * A data de referência é recebida por parâmetro em vez de chamar
 * {@link LocalDateTime#now()} internamente - isso permite testar a regra
 * com datas fixas e determinísticas, sem depender do relógio do sistema no
 * momento em que o teste roda.
 */
public class ClassificadorAgenda {

    private ClassificadorAgenda() {
    }

    /**
     * Classifica o agendamento conforme seu status e, quando ainda estiver
     * pendente, a proximidade do horário em relação à {@code referencia}.
     *
     * @param agendamento agendamento a classificar
     * @param referencia  instante usado como "agora" para o cálculo de proximidade
     * @return a classificação visual correspondente
     * @throws IllegalArgumentException se {@code agendamento} ou sua data/hora forem nulos
     */
    public static ClassificacaoAgenda classificar(Agendamento agendamento, LocalDateTime referencia) {
        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento não pode ser nulo.");
        }
        if (agendamento.getDataHora() == null) {
            throw new IllegalArgumentException("Data/hora do agendamento não pode ser nula.");
        }

        StatusAgendamento status = agendamento.getStatus();
        if (status == StatusAgendamento.CANCELADO) {
            return ClassificacaoAgenda.CANCELADO;
        }
        if (status == StatusAgendamento.CONCLUIDO) {
            return ClassificacaoAgenda.CONCLUIDO;
        }
        if (status == StatusAgendamento.EM_ATENDIMENTO) {
            return ClassificacaoAgenda.EM_ANDAMENTO;
        }

        if (agendamento.getDataHora().isBefore(referencia)) {
            return ClassificacaoAgenda.ATRASADO;
        }

        long minutosParaComecar = Duration.between(referencia, agendamento.getDataHora()).toMinutes();
        if (minutosParaComecar <= 60) {
            return ClassificacaoAgenda.IMINENTE;
        }
        if (minutosParaComecar <= 120) {
            return ClassificacaoAgenda.PROXIMO;
        }
        return ClassificacaoAgenda.DISTANTE;
    }
}
