package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Cliente;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de persistência de {@link Cliente} consumido por
 * {@link br.com.barberdesk.service.AgendaService} (registro automático ao
 * criar agendamento) e {@link br.com.barberdesk.service.ClienteService}
 * (listagem do diretório na aba "Clientes" — Fase 4 do roteiro de
 * refatoração, antes a UI chamava {@link br.com.barberdesk.dao.ClienteDAO}
 * direto).
 */
public interface ClienteRepository {

    /**
     * Registra (upsert) o cliente a partir do nome/contato informados num
     * agendamento — contato é a chave de dedupe dentro da barbearia.
     */
    void registrar(int barbeariaId, String nome, String contato) throws SQLException;

    /** Lista o diretório de clientes de uma barbearia. */
    List<Cliente> listarPorBarbearia(int barbeariaId) throws SQLException;
}
