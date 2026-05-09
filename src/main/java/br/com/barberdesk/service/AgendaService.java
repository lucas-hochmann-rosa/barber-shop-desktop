package br.com.barberdesk.service;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.StatusAgendamento;
import java.sql.SQLException;

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
