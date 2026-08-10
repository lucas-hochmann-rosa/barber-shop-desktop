package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Agendamento;

import java.sql.SQLException;

/**
 * Contrato de persistência de {@link Agendamento} consumido por
 * {@link br.com.barberdesk.service.AgendaService}. Segue Interface
 * Segregation: só declara os métodos que o service realmente usa, não
 * toda a superfície pública de {@link br.com.barberdesk.dao.AgendamentoDAO}
 * (que também é usada diretamente pela UI para consultas específicas de
 * tela, como listagens e verificação de conflito).
 */
public interface AgendamentoRepository {

    /** Insere um novo agendamento e devolve o id gerado. */
    int inserir(Agendamento agendamento) throws SQLException;

    /** Busca um agendamento pelo id, ou {@code null} se não existir. */
    Agendamento buscarPorId(int id) throws SQLException;

    /** Atualiza um agendamento existente. */
    void atualizar(Agendamento agendamento) throws SQLException;
}
