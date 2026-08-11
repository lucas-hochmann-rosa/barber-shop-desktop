package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Servico;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de persistência de {@link Servico} consumido por
 * {@link br.com.barberdesk.service.SetupService} (cadastro inicial) e
 * {@link br.com.barberdesk.service.CatalogoService} (manutenção do
 * catálogo em "Minha Barbearia" - Fase 4 do roteiro de refatoração,
 * antes a UI chamava {@link br.com.barberdesk.dao.ServicoDAO} direto).
 */
public interface ServicoRepository {

    /** Insere um novo serviço e devolve o id gerado. */
    int inserir(Servico servico) throws SQLException;

    /** Lista os serviços de uma barbearia. */
    List<Servico> listarPorBarbearia(int barbeariaId) throws SQLException;

    /** Atualiza um serviço existente. */
    void atualizar(Servico servico) throws SQLException;

    /** Remove definitivamente um serviço pelo id. */
    void deletar(int id) throws SQLException;

    /** Verifica se já existe outro serviço com esse nome na barbearia (case-insensitive). */
    boolean existePorNome(int barbeariaId, String nome, int excluirId) throws SQLException;
}
