package br.com.barberdesk.service;

import br.com.barberdesk.dao.repository.AgendamentoRepository;
import br.com.barberdesk.dao.repository.ClienteRepository;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.Barbearia;
import br.com.barberdesk.model.StatusAgendamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Centraliza as transições de status de um agendamento (iniciar, concluir,
 * cancelar) — antes duplicadas em TelaHome e TelaEditarAgendamento.
 */
public class AgendaService {
    private static final Logger logger = LoggerFactory.getLogger(AgendaService.class);

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;

    public AgendaService(AgendamentoRepository agendamentoRepository, ClienteRepository clienteRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Cria o agendamento e garante que o cliente entra no diretório — as duas
     * coisas sempre andam juntas, então ficam num só método em vez de a UI
     * ter que lembrar de chamar as duas (era esse o caso antes: só
     * TelaNovoAgendamento registrava o cliente, então qualquer outro caminho
     * de criação de agendamento não passaria pelo diretório). Falha ao
     * registrar o cliente não desfaz o agendamento — ele já foi persistido.
     */
    public int criarAgendamento(Agendamento agendamento) throws SQLException {
        int id = agendamentoRepository.inserir(agendamento);
        try {
            clienteRepository.registrar(agendamento.getBarbeariaId(), agendamento.getClienteNome(), agendamento.getContato());
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

    /** Busca um agendamento pelo id, ou {@code null} se não existir. */
    public Agendamento buscarPorId(int id) throws SQLException {
        return agendamentoRepository.buscarPorId(id);
    }

    /** Lista os agendamentos ainda pendentes (AGENDADO/EM_ATENDIMENTO) de uma barbearia. */
    public List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) throws SQLException {
        return agendamentoRepository.listarPendentesPorBarbearia(barbeariaId);
    }

    /** Lista todos os agendamentos de uma barbearia, independente do status — usado no histórico. */
    public List<Agendamento> listarPorBarbearia(int barbeariaId) throws SQLException {
        return agendamentoRepository.listarPorBarbearia(barbeariaId);
    }

    /**
     * Atualiza um agendamento existente com os dados editados pelo usuário
     * (cliente, contato, data/hora, serviço, barbeiro, origem). A checagem
     * de conflito de horário acontece dentro do próprio repositório.
     */
    public void atualizar(Agendamento agendamento) throws SQLException {
        agendamentoRepository.atualizar(agendamento);
    }

    /** Remove definitivamente um agendamento pelo id. */
    public void deletar(int id) throws SQLException {
        agendamentoRepository.deletar(id);
    }

    /**
     * Verifica se o barbeiro já tem um agendamento que sobrepõe o intervalo
     * informado — usado pela UI como checagem antecipada, antes de tentar
     * criar o agendamento, pra mostrar um erro específico de conflito em vez
     * de deixar a exceção genérica do repositório estourar.
     */
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) throws SQLException {
        return agendamentoRepository.verificarConflito(barbeiroId, dataHora, duracaoMinutos);
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
        Agendamento agendamento = agendamentoRepository.buscarPorId(id);
        if (agendamento == null) return;
        agendamento.setStatus(novoStatus);
        if (motivoCancelamento != null) {
            agendamento.setMotivoCancelamento(motivoCancelamento);
        }
        agendamentoRepository.atualizar(agendamento);
    }
}
