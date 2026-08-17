package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.AgendamentoDTO;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.StatusAgendamento;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.BarbeariaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints REST para consulta de histórico com múltiplos filtros (RF09).
 */
@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final AgendaService agendaService;
    private final BarbeariaService barbeariaService;

    public HistoricoController(AgendaService agendaService, BarbeariaService barbeariaService) {
        this.agendaService = agendaService;
        this.barbeariaService = barbeariaService;
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoDTO>> consultarHistorico(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId,
            @RequestParam(name = "inicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(name = "fim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(name = "barbeiroId", required = false) Integer barbeiroId,
            @RequestParam(name = "status", required = false) StatusAgendamento status) throws SQLException {
        int bId = (barbeariaId != null && barbeariaId > 0) ? barbeariaId : 1;
        Barbearia b = barbeariaService.buscarPrimeira();
        if (b != null) {
            bId = b.getId();
        }

        List<Agendamento> todos = agendaService.listarPorBarbearia(bId);

        List<AgendamentoDTO> filtrados = todos.stream()
                .filter(a -> {
                    if (inicio != null && a.getDataHora().toLocalDate().isBefore(inicio)) return false;
                    if (fim != null && a.getDataHora().toLocalDate().isAfter(fim)) return false;
                    if (barbeiroId != null && barbeiroId > 0 && a.getBarbeiroId() != barbeiroId) return false;
                    if (status != null && a.getStatus() != status) return false;
                    return true;
                })
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtrados);
    }
}
