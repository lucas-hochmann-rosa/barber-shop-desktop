package br.com.barberdesk.service;

import br.com.barberdesk.dao.repository.BarbeariaRepository;
import br.com.barberdesk.model.Barbearia;

import java.sql.SQLException;

/**
 * Consulta e manutenção dos dados gerais de uma barbearia já cadastrada
 * (aba "Minha Barbearia" - nome, CEP, cultura/valores, horário de
 * funcionamento). O cadastro inicial da barbearia é responsabilidade de
 * {@link SetupService}; este service cobre a edição posterior.
 */
public class BarbeariaService {

    private final BarbeariaRepository barbeariaRepository;

    public BarbeariaService(BarbeariaRepository barbeariaRepository) {
        this.barbeariaRepository = barbeariaRepository;
    }

    /** Busca uma barbearia pelo id, ou {@code null} se não existir. */
    public Barbearia buscarPorId(int id) throws SQLException {
        return barbeariaRepository.buscarPorId(id);
    }

    /** Atualiza os dados de uma barbearia existente. */
    public void atualizar(Barbearia barbearia) throws SQLException {
        barbeariaRepository.atualizar(barbearia);
    }
}
