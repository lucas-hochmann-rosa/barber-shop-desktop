package br.com.barberdesk.dao.repository;

import br.com.barberdesk.model.Barbeiro;

import java.sql.SQLException;

/**
 * Contrato de persistência de {@link Barbeiro} consumido por
 * {@link br.com.barberdesk.service.SetupService} (cadastro inicial). Só
 * declara {@code inserir} por ora — a UI ainda consulta
 * {@link br.com.barberdesk.dao.BarbeiroDAO} diretamente pras demais
 * operações (listar, editar, excluir); a interface cresce quando essas
 * operações também passarem a ser consumidas por um service (Fase 4).
 */
public interface BarbeiroRepository {

    /** Insere um novo barbeiro e devolve o id gerado. */
    int inserir(Barbeiro barbeiro) throws SQLException;
}
