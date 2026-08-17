package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.AgendamentoDTO;
import br.com.barbershop.api.dto.AgendamentoRequest;
import br.com.barbershop.api.dto.ConflitoResponse;
import br.com.barbershop.api.dto.MensagemResponse;
import br.com.barbershop.model.Agendamento;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.ClassificacaoAgenda;
import br.com.barbershop.model.OrigemContato;
import br.com.barbershop.model.Servico;
import br.com.barbershop.model.StatusAgendamento;
import br.com.barbershop.service.AgendaService;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.CatalogoService;
import br.com.barbershop.service.ClassificadorAgenda;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints REST para gerenciamento da agenda, fluxo de atendimento e classificação visual (RF05, RF06, RF07, RF08, RF10, RF11).
 */
@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    private final AgendaService agendaService;
    private final BarbeariaService barbeariaService;
    private final CatalogoService catalogoService;

    public AgendaController(AgendaService agendaService, BarbeariaService barbeariaService, CatalogoService catalogoService) {
        this.agendaService = agendaService;
        this.barbeariaService = barbeariaService;
        this.catalogoService = catalogoService;
    }

    private int obterBarbeariaIdPadrao(Integer paramId) throws SQLException {
        if (paramId != null && paramId > 0) return paramId;
        Barbearia b = barbeariaService.buscarPrimeira();
        return b != null ? b.getId() : 1;
    }

    @GetMapping("/hoje")
    public ResponseEntity<List<AgendamentoDTO>> listarHoje(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId,
            @RequestParam(name = "referencia", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referencia) throws SQLException {
        int id = obterBarbeariaIdPadrao(barbeariaId);
        LocalDateTime agora = (referencia != null) ? referencia : LocalDateTime.now();

        List<Agendamento> pendentes = agendaService.listarPendentesPorBarbearia(id);
        List<AgendamentoDTO> dtos = pendentes.stream().map(a -> {
            ClassificacaoAgenda cls = ClassificadorAgenda.classificar(a, agora);
            return new AgendamentoDTO(a, cls);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoDTO>> listarTodos(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId,
            @RequestParam(name = "referencia", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime referencia) throws SQLException {
        int id = obterBarbeariaIdPadrao(barbeariaId);
        LocalDateTime agora = (referencia != null) ? referencia : LocalDateTime.now();

        List<Agendamento> todos = agendaService.listarPorBarbearia(id);
        List<AgendamentoDTO> dtos = todos.stream().map(a -> {
            ClassificacaoAgenda cls = ClassificadorAgenda.classificar(a, agora);
            return new AgendamentoDTO(a, cls);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") int id) throws SQLException {
        Agendamento a = agendaService.buscarPorId(id);
        if (a == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponse(false, "Agendamento não encontrado."));
        }
        ClassificacaoAgenda cls = ClassificadorAgenda.classificar(a, LocalDateTime.now());
        return ResponseEntity.ok(new AgendamentoDTO(a, cls));
    }

    @PostMapping
    public ResponseEntity<?> criarAgendamento(@RequestBody AgendamentoRequest req) throws SQLException {
        if (req == null || req.getClienteNome() == null || req.getContato() == null || req.getDataHora() == null
                || req.getServicoId() <= 0 || req.getBarbeiroId() <= 0) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Campos obrigatórios ausentes."));
        }

        int barbeariaId = obterBarbeariaIdPadrao(req.getBarbeariaId());
        req.setBarbeariaId(barbeariaId);

        // Busca dados do serviço para duração
        List<Servico> servicos = catalogoService.listarServicos(barbeariaId);
        Servico servico = servicos.stream().filter(s -> s.getId() == req.getServicoId()).findFirst().orElse(null);
        int duracao = (servico != null && servico.getDuracaoMinutos() > 0) ? servico.getDuracaoMinutos() : 30;

        // Checagem de conflito (RF10)
        if (agendaService.verificarConflito(req.getBarbeiroId(), req.getDataHora(), duracao)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "O barbeiro selecionado já possui atendimento neste horário."));
        }

        // Checagem de horário de funcionamento
        Barbearia barbearia = barbeariaService.buscarPorId(barbeariaId);
        if (barbearia != null && !agendaService.dentroDoHorarioFuncionamento(barbearia, req.getDataHora(), duracao)) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Horário fora do expediente da barbearia."));
        }

        Agendamento agendamento = new Agendamento(
                barbeariaId,
                req.getServicoId(),
                req.getBarbeiroId(),
                req.getClienteNome().trim(),
                req.getContato().trim(),
                req.getDataHora(),
                req.getOrigemContato() != null ? req.getOrigemContato() : OrigemContato.OUTRO,
                StatusAgendamento.AGENDADO
        );
        agendamento.setDuracaoMinutos(duracao);

        int id = agendaService.criarAgendamento(agendamento);
        Agendamento salvo = agendaService.buscarPorId(id);
        ClassificacaoAgenda cls = ClassificadorAgenda.classificar(salvo, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AgendamentoDTO(salvo, cls));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(@PathVariable("id") int id, @RequestBody AgendamentoRequest req) throws SQLException {
        Agendamento existente = agendaService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensagemResponse(false, "Agendamento não encontrado."));
        }

        if (req.getClienteNome() != null) existente.setClienteNome(req.getClienteNome().trim());
        if (req.getContato() != null) existente.setContato(req.getContato().trim());
        if (req.getDataHora() != null) existente.setDataHora(req.getDataHora());
        if (req.getServicoId() > 0) existente.setServicoId(req.getServicoId());
        if (req.getBarbeiroId() > 0) existente.setBarbeiroId(req.getBarbeiroId());
        if (req.getOrigemContato() != null) existente.setOrigemContato(req.getOrigemContato());
        if (req.getStatus() != null) existente.setStatus(req.getStatus());
        if (req.getMotivoCancelamento() != null) existente.setMotivoCancelamento(req.getMotivoCancelamento());

        agendaService.atualizar(existente);
        Agendamento atualizado = agendaService.buscarPorId(id);
        ClassificacaoAgenda cls = ClassificadorAgenda.classificar(atualizado, LocalDateTime.now());
        return ResponseEntity.ok(new AgendamentoDTO(atualizado, cls));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensagemResponse> excluirAgendamento(@PathVariable("id") int id) throws SQLException {
        agendaService.deletar(id);
        return ResponseEntity.ok(new MensagemResponse(true, "Agendamento excluído com sucesso."));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<MensagemResponse> iniciarAtendimento(@PathVariable("id") int id) throws SQLException {
        agendaService.iniciarAtendimento(id);
        return ResponseEntity.ok(new MensagemResponse(true, "Atendimento iniciado com sucesso."));
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<MensagemResponse> concluirAtendimento(@PathVariable("id") int id) throws SQLException {
        agendaService.concluirAtendimento(id);
        return ResponseEntity.ok(new MensagemResponse(true, "Atendimento concluído com sucesso."));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<MensagemResponse> cancelarAgendamento(
            @PathVariable("id") int id,
            @RequestParam(name = "motivo", required = false, defaultValue = "Cancelado pelo usuário") String motivo) throws SQLException {
        agendaService.cancelarAgendamento(id, motivo);
        return ResponseEntity.ok(new MensagemResponse(true, "Agendamento cancelado com sucesso."));
    }

    @GetMapping("/conflito")
    public ResponseEntity<ConflitoResponse> verificarConflito(
            @RequestParam(name = "barbeiroId") int barbeiroId,
            @RequestParam(name = "dataHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataHora,
            @RequestParam(name = "duracaoMinutos") int duracaoMinutos,
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId) throws SQLException {
        int bId = obterBarbeariaIdPadrao(barbeariaId);
        boolean conflito = agendaService.verificarConflito(barbeiroId, dataHora, duracaoMinutos);
        Barbearia barbearia = barbeariaService.buscarPorId(bId);
        boolean dentroExpediente = agendaService.dentroDoHorarioFuncionamento(barbearia, dataHora, duracaoMinutos);

        String msg;
        if (conflito) {
            msg = "O barbeiro já possui um atendimento no horário selecionado.";
        } else if (!dentroExpediente) {
            msg = "O horário selecionado está fora do expediente da barbearia.";
        } else {
            msg = "Horário disponível para agendamento.";
        }

        return ResponseEntity.ok(new ConflitoResponse(conflito, dentroExpediente, msg));
    }
}
