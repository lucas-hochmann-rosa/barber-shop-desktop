package br.com.barbershop.service;

import br.com.barbershop.dao.repository.BarbeiroRepository;
import br.com.barbershop.dao.repository.ServicoRepository;
import br.com.barbershop.model.Barbeiro;
import br.com.barbershop.model.Servico;

import java.sql.SQLException;
import java.util.List;

/**
 * Manutenção do catálogo de uma barbearia: serviços e barbeiros oferecidos.
 * Centraliza o que antes era chamado direto pela UI em
 * {@link br.com.barbershop.dao.ServicoDAO}/{@link br.com.barbershop.dao.BarbeiroDAO}
 * (aba "Minha Barbearia" de TelaHome).
 */
public class CatalogoService {

    private final ServicoRepository servicoRepository;
    private final BarbeiroRepository barbeiroRepository;

    public CatalogoService(ServicoRepository servicoRepository, BarbeiroRepository barbeiroRepository) {
        this.servicoRepository = servicoRepository;
        this.barbeiroRepository = barbeiroRepository;
    }

    /** Lista os serviços de uma barbearia. */
    public List<Servico> listarServicos(int barbeariaId) throws SQLException {
        return servicoRepository.listarPorBarbearia(barbeariaId);
    }

    /** Verifica se já existe outro serviço com esse nome na barbearia (case-insensitive). */
    public boolean existeServicoComNome(int barbeariaId, String nome, int excluirId) throws SQLException {
        return servicoRepository.existePorNome(barbeariaId, nome, excluirId);
    }

    /** Insere um novo serviço e devolve o id gerado. */
    public int salvarNovoServico(Servico servico) throws SQLException {
        return servicoRepository.inserir(servico);
    }

    /** Atualiza um serviço existente. */
    public void atualizarServico(Servico servico) throws SQLException {
        servicoRepository.atualizar(servico);
    }

    /** Remove definitivamente um serviço pelo id. */
    public void excluirServico(int id) throws SQLException {
        servicoRepository.deletar(id);
    }

    /** Lista os barbeiros de uma barbearia. */
    public List<Barbeiro> listarBarbeiros(int barbeariaId) throws SQLException {
        return barbeiroRepository.listarPorBarbearia(barbeariaId);
    }

    /** Verifica se já existe outro barbeiro com esse nome na barbearia (case-insensitive). */
    public boolean existeBarbeiroComNome(int barbeariaId, String nome, int excluirId) throws SQLException {
        return barbeiroRepository.existePorNome(barbeariaId, nome, excluirId);
    }

    /** Insere um novo barbeiro e devolve o id gerado. */
    public int salvarNovoBarbeiro(Barbeiro barbeiro) throws SQLException {
        return barbeiroRepository.inserir(barbeiro);
    }

    /** Atualiza um barbeiro existente. */
    public void atualizarBarbeiro(Barbeiro barbeiro) throws SQLException {
        barbeiroRepository.atualizar(barbeiro);
    }

    /** Remove definitivamente um barbeiro pelo id. */
    public void excluirBarbeiro(int id) throws SQLException {
        barbeiroRepository.deletar(id);
    }
}
