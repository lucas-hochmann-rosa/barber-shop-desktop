package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.AgendamentoRepository;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryAgendamentoRepository implements AgendamentoRepository {
    private final List<Agendamento> agendamentos = new ArrayList<>();
    private int nextId = 1;

    @Override
    public int inserir(Agendamento agendamento) {
        agendamento.setId(nextId++);
        agendamentos.add(agendamento);
        return agendamento.getId();
    }

    @Override
    public Agendamento buscarPorId(int id) {
        return agendamentos.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void atualizar(Agendamento agendamento) {
        for (int i = 0; i < agendamentos.size(); i++) {
            if (agendamentos.get(i).getId() == agendamento.getId()) {
                agendamentos.set(i, agendamento);
                return;
            }
        }
    }

    @Override
    public void deletar(int id) {
        agendamentos.removeIf(a -> a.getId() == id);
    }

    @Override
    public List<Agendamento> listarPendentesPorBarbearia(int barbeariaId) {
        return agendamentos.stream()
                .filter(a -> a.getBarbeariaId() == barbeariaId)
                .filter(a -> a.getStatus() == StatusAgendamento.AGENDADO || a.getStatus() == StatusAgendamento.EM_ATENDIMENTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Agendamento> listarPorBarbearia(int barbeariaId) {
        return agendamentos.stream()
                .filter(a -> a.getBarbeariaId() == barbeariaId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarConflito(int barbeiroId, LocalDateTime dataHora, int duracaoMinutos) {
        LocalDateTime fimNovo = dataHora.plusMinutes(duracaoMinutos);
        return agendamentos.stream()
                .filter(a -> a.getBarbeiroId() == barbeiroId)
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .anyMatch(a -> {
                    LocalDateTime inicioExistente = a.getDataHora();
                    LocalDateTime fimExistente = inicioExistente.plusMinutes(a.getDuracaoMinutos());
                    return dataHora.isBefore(fimExistente) && fimNovo.isAfter(inicioExistente);
                });
    }
}
