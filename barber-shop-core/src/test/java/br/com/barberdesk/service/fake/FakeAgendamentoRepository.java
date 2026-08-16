package br.com.barberdesk.service.fake;

import br.com.barberdesk.dao.repository.AgendamentoRepository;
import br.com.barberdesk.model.Agendamento;
import br.com.barberdesk.model.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositório de agendamentos em memória, usado nos testes de
 * {@link br.com.barberdesk.service.AgendaService} para não depender de um
 * banco MySQL real. Reproduz a mesma checagem de sobreposição de horário
 * por barbeiro (considerando a duração de cada agendamento e restringindo a
 * status AGENDADO/EM_ATENDIMENTO) usada por
 * {@link br.com.barberdesk.dao.AgendamentoDAO#verificarConflito}.
 */
public class FakeAgendamentoRepository implements AgendamentoRepository {

    private final Map<Integer, Agendamento> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public int inserir(Agendamento agendamento) {
        int id = proximoId++;
        agendamento.setId(id);
        dados.put(id, agendamento);
        return id;
    }

    @Override
    public Agendamento buscarPorId(int id) {
        return dados.get(id);
    }

    @Override
    public void atualizar(Agendamento agendamento) {
        dados.put(agendamento.getId(), agendamento);
    }

    @Override
    public void deletar(int id) {
        dados.remove(id);
    }

    @Override
    public List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) {
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento a : dados.values()) {
            if (a.getBarbeariaId() == barbeariaId
                    && (a.getStatus() == StatusAgendamento.AGENDADO || a.getStatus() == StatusAgendamento.EM_ATENDIMENTO)) {
                lista.add(a);
            }
        }
        return lista;
    }

    @Override
    public List<Agendamento> listarPorBarbearia(int barbeariaId) {
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento a : dados.values()) {
            if (a.getBarbeariaId() == barbeariaId) lista.add(a);
        }
        return lista;
    }

    @Override
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) {
        if (barbeiroId <= 0 || dataHora == null) return false;
        int duracaoNova = duracaoMinutos > 0 ? duracaoMinutos : 30;
        LocalDateTime inicioNovo = dataHora;
        LocalDateTime fimNovo = dataHora.plusMinutes(duracaoNova);

        for (Agendamento existente : dados.values()) {
            if (existente.getBarbeiroId() != barbeiroId) continue;
            if (existente.getStatus() != StatusAgendamento.AGENDADO && existente.getStatus() != StatusAgendamento.EM_ATENDIMENTO) continue;

            int duracaoExistente = existente.getDuracaoMinutos() > 0 ? existente.getDuracaoMinutos() : 30;
            LocalDateTime inicioExistente = existente.getDataHora();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

            if (inicioExistente.isBefore(fimNovo) && fimExistente.isAfter(inicioNovo)) {
                return true;
            }
        }
        return false;
    }
}
