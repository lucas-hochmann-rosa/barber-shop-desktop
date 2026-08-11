package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Barbeiro;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de persistência de {@link Barbeiro} consumido por
 * {@link br.com.barberdesk.service.SetupService} (cadastro inicial) e
 * {@link br.com.barberdesk.service.CatalogoService} (manutenção do
 * catálogo em "Minha Barbearia" — Fase 4 do roteiro de refatoração,
 * antes a UI chamava {@link br.com.barberdesk.dao.BarbeiroDAO} direto).
 */
public interface BarbeiroRepository {

    /** Insere um novo barbeiro e devolve o id gerado. */
    int inserir(Barbeiro barbeiro) throws SQLException;

    /** Lista os barbeiros de uma barbearia. */
    List<Barbeiro> listarPorBarbearia(int barbeariaId) throws SQLException;

    /** Atualiza um barbeiro existente. */
    void atualizar(Barbeiro barbeiro) throws SQLException;

    /** Remove definitivamente um barbeiro pelo id. */
    void deletar(int id) throws SQLException;

    /** Verifica se já existe outro barbeiro com esse nome na barbearia (case-insensitive). */
    boolean existePorNome(int barbeariaId, String nome, int excluirId) throws SQLException;
}
