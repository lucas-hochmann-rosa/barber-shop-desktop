package br.com.barberdesk.service;

import br.com.barberdesk.dao.AgendamentoDAO;
import br.com.barberdesk.dao.ClienteDAO;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.StatusAgendamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Centraliza as transições de status de um agendamento (iniciar, concluir,
 * cancelar) — antes duplicadas em TelaHome e TelaEditarAgendamento.
 */
public class AgendaService {
    private static final Logger logger = LoggerFactory.getLogger(AgendaService.class);

    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /**
     * Cria o agendamento e garante que o cliente entra no diretório — as duas
     * coisas sempre andam juntas, então ficam num só método em vez de a UI
     * ter que lembrar de chamar as duas (era esse o caso antes: só
     * TelaNovoAgendamento registrava o cliente, então qualquer outro caminho
     * de criação de agendamento não passaria pelo diretório). Falha ao
     * registrar o cliente não desfaz o agendamento — ele já foi persistido.
     */
    public int criarAgendamento(Agendamento agendamento) throws SQLException {
        int id = agendamentoDAO.inserir(agendamento);
        try {
            clienteDAO.registrar(agendamento.getBarbeariaId(), agendamento.getClienteNome(), agendamento.getContato());
        } catch (SQLException e) {
            logger.warn("Agendamento {} criado, mas não foi possível registrar o cliente no diretório", id, e);
        }
        return id;
    }

    /**
     * Marca o agendamento como em atendimento (barbeiro iniciou o corte).
     *
     * @param id identificador do agendamento
     */
    public void iniciarAtendimento(int id) throws SQLException {
        alterarStatus(id, StatusAgendamento.EM_ATENDIMENTO, null);
    }

    /**
     * Marca o agendamento como concluído, encerrando o atendimento.
     *
     * @param id identificador do agendamento
     */
    public void concluirAtendimento(int id) throws SQLException {
        alterarStatus(id, StatusAgendamento.CONCLUIDO, null);
    }

    /**
     * Cancela o agendamento, registrando o motivo informado.
     *
     * @param id     identificador do agendamento
     * @param motivo justificativa do cancelamento, persistida junto ao agendamento
     */
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

    // Implementação compartilhada das transições de status (iniciar/concluir/
    // cancelar). Se o agendamento não existir mais (ex.: excluído em paralelo),
    // simplesmente não faz nada em vez de lançar exceção.
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
