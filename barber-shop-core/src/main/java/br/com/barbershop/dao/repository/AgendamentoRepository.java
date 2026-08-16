package br.com.barbershop.dao.repository;

import br.com.barbershop.model.Agendamento;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de persistência de {@link Agendamento} consumido por
 * {@link br.com.barbershop.service.AgendaService}, que agora é o único
 * ponto de acesso da UI a esses dados (Fase 4 do roteiro de
 * refatoração - antes a UI chamava {@link br.com.barbershop.dao.AgendamentoDAO}
 * direto).
 */
public interface AgendamentoRepository {

    /** Insere um novo agendamento e devolve o id gerado. */
    int inserir(Agendamento agendamento) throws SQLException;

    /** Busca um agendamento pelo id, ou {@code null} se não existir. */
    Agendamento buscarPorId(int id) throws SQLException;

    /** Atualiza um agendamento existente. */
    void atualizar(Agendamento agendamento) throws SQLException;

    /** Remove definitivamente um agendamento pelo id. */
    void deletar(int id) throws SQLException;

    /** Lista os agendamentos ainda pendentes (AGENDADO/EM_ATENDIMENTO) de uma barbearia. */
    List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) throws SQLException;

    /** Lista todos os agendamentos de uma barbearia, independente do status (histórico). */
    List<Agendamento> listarPorBarbearia(int barbeariaId) throws SQLException;

    /** Verifica se o barbeiro já tem um agendamento que sobrepõe o intervalo informado. */
    boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) throws SQLException;
}
