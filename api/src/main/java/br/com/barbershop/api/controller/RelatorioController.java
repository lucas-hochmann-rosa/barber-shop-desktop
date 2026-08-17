package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.RelatorioDTO;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.ItemRelatorio;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.RelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints REST para faturamento e ranking gerencial (RF09).
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final BarbeariaService barbeariaService;

    public RelatorioController(RelatorioService relatorioService, BarbeariaService barbeariaService) {
        this.relatorioService = relatorioService;
        this.barbeariaService = barbeariaService;
    }

    @GetMapping
    public ResponseEntity<RelatorioDTO> gerarRelatorio(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId,
            @RequestParam(name = "de", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(name = "ate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate) throws SQLException {
        int bId = (barbeariaId != null && barbeariaId > 0) ? barbeariaId : 1;
        Barbearia b = barbeariaService.buscarPrimeira();
        if (b != null) {
            bId = b.getId();
        }

        LocalDate dataInicio = (de != null) ? de : LocalDate.now().minusDays(30);
        LocalDate dataFim = (ate != null) ? ate : LocalDate.now();

        BigDecimal faturamento = relatorioService.faturamentoTotal(bId, dataInicio, dataFim);
        if (faturamento == null) faturamento = BigDecimal.ZERO;

        List<ItemRelatorio> servicos = relatorioService.servicosMaisVendidos(bId, dataInicio, dataFim);
        List<ItemRelatorio> barbeiros = relatorioService.rankingBarbeiros(bId, dataInicio, dataFim);

        RelatorioDTO dto = new RelatorioDTO(dataInicio, dataFim, faturamento, servicos, barbeiros);
        return ResponseEntity.ok(dto);
    }
}
