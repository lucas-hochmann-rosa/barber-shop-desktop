package br.com.barbershop.dao.repository;

import br.com.barbershop.model.Barbearia;

import java.sql.SQLException;

/**
 * Contrato de persistência de {@link Barbearia} consumido por
 * {@link br.com.barbershop.service.SessionService} (resolver a barbearia
 * do usuário logado), {@link br.com.barbershop.service.SetupService}
 * (cadastro inicial) e {@link br.com.barbershop.service.BarbeariaService}
 * (manutenção dos dados gerais em "Minha Barbearia").
 */
public interface BarbeariaRepository {

    /** Devolve a primeira barbearia cadastrada, ou {@code null} se não houver nenhuma. */
    Barbearia buscarPrimeira() throws SQLException;

    /** Busca uma barbearia pelo id, ou {@code null} se não existir. */
    Barbearia buscarPorId(int id) throws SQLException;

    /** Insere uma nova barbearia e devolve o id gerado. */
    int inserir(Barbearia barbearia) throws SQLException;

    /** Atualiza os dados de uma barbearia existente. */
    void atualizar(Barbearia barbearia) throws SQLException;
}
