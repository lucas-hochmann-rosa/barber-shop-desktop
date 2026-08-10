package br.com.barberdesk.dao.repository;

import java.sql.SQLException;

/**
 * Contrato de persistência de {@link br.com.barberdesk.model.Cliente}
 * consumido por {@link br.com.barberdesk.service.AgendaService}. Só
 * declara {@code registrar} — é o único método que o service usa; a
 * listagem do diretório de clientes é consumida direto da UI (ver
 * {@link br.com.barberdesk.dao.ClienteDAO#listarPorBarbearia}).
 */
public interface ClienteRepository {

    /**
     * Registra (upsert) o cliente a partir do nome/contato informados num
     * agendamento — contato é a chave de dedupe dentro da barbearia.
     */
    void registrar(int barbeariaId, String nome, String contato) throws SQLException;
}
