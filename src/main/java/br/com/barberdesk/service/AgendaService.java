package br.com.barberdesk.service;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.StatusAgendamento;
import java.sql.SQLException;
import java.util.List;

public class AgendaService {
    private AgendamentoDAO agendamentoDAO = new AgendamentoDAO();

    public List<Agendamento> listarPendentes(int barbeariaId) throws SQLException {
        return agendamentoDAO.listarPendentes(barbeariaId);
    }

    public List<Agendamento> listarTodos(int barbeariaId) throws SQLException {
        return agendamentoDAO.listarTodos(barbeariaId);
    }

    public int criarAgendamento(Agendamento agendamento) throws SQLException {
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        return agendamentoDAO.inserir(agendamento);
    }

    public void editarAgendamento(Agendamento agendamento) throws SQLException {
        agendamentoDAO.atualizar(agendamento);
    }

    public void excluirAgendamento(int id) throws SQLException {
        agendamentoDAO.deletar(id);
    }

    public void iniciarAtendimento(int id) throws SQLException {
        Agendamento agendamento = agendamentoDAO.buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.EM_ATENDIMENTO);
            agendamentoDAO.atualizar(agendamento);
        }
    }

    public void concluirAtendimento(int id) throws SQLException {
        Agendamento agendamento = agendamentoDAO.buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            agendamentoDAO.atualizar(agendamento);
        }
    }

    public void cancelarAgendamento(int id) throws SQLException {
        Agendamento agendamento = agendamentoDAO.buscarPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.CANCELADO);
            agendamentoDAO.atualizar(agendamento);
        }
    }
}
