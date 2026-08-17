package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.BarbeariaDTO;
import br.com.barbershop.api.dto.MensagemResponse;
import br.com.barbershop.api.dto.SetupRequest;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Barbeiro;
import br.com.barbershop.model.Servico;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.SetupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Endpoints REST para gerenciamento dos dados da barbearia (RF01, RF03).
 */
@RestController
@RequestMapping("/api/barbearia")
public class BarbeariaController {

    private final BarbeariaService barbeariaService;
    private final SetupService setupService;

    public BarbeariaController(BarbeariaService barbeariaService, SetupService setupService) {
        this.barbeariaService = barbeariaService;
        this.setupService = setupService;
    }

    @GetMapping
    public ResponseEntity<?> buscarBarbearia() throws SQLException {
        Barbearia b = barbeariaService.buscarPrimeira();
        if (b == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponse(false, "Nenhuma barbearia cadastrada no sistema."));
        }
        return ResponseEntity.ok(new BarbeariaDTO(b));
    }

    @PutMapping
    public ResponseEntity<MensagemResponse> atualizarBarbearia(@RequestBody BarbeariaDTO dto) throws SQLException {
        if (dto == null || dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Nome da barbearia é obrigatório."));
        }

        Barbearia b = barbeariaService.buscarPrimeira();
        if (b == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponse(false, "Barbearia não encontrada para atualização."));
        }

        b.setNome(dto.getNome().trim());
        b.setCep(dto.getCep() != null ? dto.getCep().trim() : null);
        b.setDataFundacao(dto.getDataFundacao());
        b.setCulturaValores(dto.getCulturaValores());
        b.setHorarioAbertura(dto.getHorarioAbertura());
        b.setHorarioFechamento(dto.getHorarioFechamento());

        barbeariaService.atualizar(b);
        return ResponseEntity.ok(new MensagemResponse(true, "Dados da barbearia atualizados com sucesso."));
    }

    @PostMapping("/setup")
    public ResponseEntity<MensagemResponse> setupInicial(@RequestBody SetupRequest request) throws SQLException {
        if (request == null || request.getNomeBarbearia() == null || request.getLoginAdmin() == null) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Dados incompletos para configuração inicial."));
        }

        if (setupService.existeBarbearia()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "O sistema já foi configurado anteriormente."));
        }

        Barbearia barbearia = new Barbearia(
                request.getNomeBarbearia().trim(),
                request.getCep(),
                request.getDataFundacao(),
                request.getCulturaValores()
        );
        barbearia.setHorarioAbertura(request.getHorarioAbertura());
        barbearia.setHorarioFechamento(request.getHorarioFechamento());

        List<Servico> servicos = new ArrayList<>();
        if (request.getPrimeiroServicoNome() != null && !request.getPrimeiroServicoNome().trim().isEmpty()) {
            int duracao = request.getPrimeiroServicoDuracao() > 0 ? request.getPrimeiroServicoDuracao() : 30;
            servicos.add(new Servico(0, 0, request.getPrimeiroServicoNome().trim(), request.getPrimeiroServicoPreco(), null, duracao));
        }

        List<Barbeiro> barbeiros = new ArrayList<>();
        if (request.getPrimeiroBarbeiroNome() != null && !request.getPrimeiroBarbeiroNome().trim().isEmpty()) {
            barbeiros.add(new Barbeiro(0, 0, request.getPrimeiroBarbeiroNome().trim(), null));
        }

        setupService.criarCadastroInicial(barbearia, request.getLoginAdmin().trim(), request.getSenhaAdmin(), servicos, barbeiros);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensagemResponse(true, "Configuração inicial da barbearia concluída com sucesso."));
    }
}
