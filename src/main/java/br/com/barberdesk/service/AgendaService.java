package br.com.barberdesk.service;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.StatusAgendamento;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Centraliza as transições de status de um agendamento (iniciar, concluir,
 * cancelar) — antes duplicadas em TelaHome e TelaEditarAgendamento.
 */
public class AgendaService {
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    public void iniciarAtendimento(int id) throws SQLException {
        alterarStatus(id, StatusAgendamento.EM_ATENDIMENTO, null);
    }

    public void concluirAtendimento(int id) throws SQLException {
        alterarStatus(id, StatusAgendamento.CONCLUIDO, null);
    }

    public void cancelarAgendamento(int id, String motivo) throws SQLException {
        alterarStatus(id, StatusAgendamento.CANCELADO, motivo);
    }

    /**
     * Sem horário de abertura/fechamento configurado na barbearia = sem
     * restrição (mantém o comportamento anterior a essa funcionalidade).
     */
    public boolean dentroDoHorarioFuncionamento(Barbearia barbearia, LocalDateTime dataHora, int duracaoMinutos) {
        if (barbearia == null || dataHora == null) return true;
        LocalTime abertura = barbearia.getHorarioAbertura();
        LocalTime fechamento = barbearia.getHorarioFechamento();
        if (abertura == null || fechamento == null) return true;

        LocalTime inicio = dataHora.toLocalTime();
        LocalTime fim = inicio.plusMinutes(duracaoMinutos);
        return !inicio.isBefore(abertura) && !fim.isAfter(fechamento);
    }

    private void alterarStatus(int id, StatusAgendamento novoStatus, String motivoCancelamento) throws SQLException {
        Agendamento agendamento = agendamentoDAO.buscarPorId(id);
        if (agendamento == null) return;
        agendamento.setStatus(novoStatus);
        if (motivoCancelamento != null) {
            agendamento.setMotivoCancelamento(motivoCancelamento);
        }
        agendamentoDAO.atualizar(agendamento);
    }
}
