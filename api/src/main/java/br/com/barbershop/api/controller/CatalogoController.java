package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.BarbeiroDTO;
import br.com.barbershop.api.dto.MensagemResponse;
import br.com.barbershop.api.dto.ServicoDTO;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Barbeiro;
import br.com.barbershop.model.Servico;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.service.CatalogoService;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Endpoints REST para manutenção do catálogo de serviços e barbeiros (RF03, RF04).
 */
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final BarbeariaService barbeariaService;

    public CatalogoController(CatalogoService catalogoService, BarbeariaService barbeariaService) {
        this.catalogoService = catalogoService;
        this.barbeariaService = barbeariaService;
    }

    private int obterBarbeariaIdPadrao(Integer paramId) throws SQLException {
        if (paramId != null && paramId > 0) return paramId;
        Barbearia b = barbeariaService.buscarPrimeira();
        return b != null ? b.getId() : 1;
    }

    // --- Serviços (RF03) ---

    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoDTO>> listarServicos(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId) throws SQLException {
        int id = obterBarbeariaIdPadrao(barbeariaId);
        List<Servico> lista = catalogoService.listarServicos(id);
        List<ServicoDTO> dtos = lista.stream().map(ServicoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/servicos")
    public ResponseEntity<?> criarServico(@RequestBody ServicoDTO dto) throws SQLException {
        if (dto == null || dto.getNome() == null || dto.getNome().trim().isEmpty() || dto.getPreco() == null) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Nome e preço do serviço são obrigatórios."));
        }

        int barbeariaId = obterBarbeariaIdPadrao(dto.getBarbeariaId());
        dto.setBarbeariaId(barbeariaId);

        if (catalogoService.existeServicoComNome(barbeariaId, dto.getNome().trim(), 0)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "Já existe um serviço com este nome."));
        }

        int id = catalogoService.salvarNovoServico(dto.toModel());
        dto.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/servicos/{id}")
    public ResponseEntity<?> atualizarServico(
            @PathVariable("id") int id,
            @RequestBody ServicoDTO dto) throws SQLException {
        if (dto == null || dto.getNome() == null || dto.getNome().trim().isEmpty() || dto.getPreco() == null) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Nome e preço do serviço são obrigatórios."));
        }

        int barbeariaId = obterBarbeariaIdPadrao(dto.getBarbeariaId());
        dto.setBarbeariaId(barbeariaId);
        dto.setId(id);

        if (catalogoService.existeServicoComNome(barbeariaId, dto.getNome().trim(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "Já existe outro serviço cadastrado com este nome."));
        }

        catalogoService.atualizarServico(dto.toModel());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/servicos/{id}")
    public ResponseEntity<MensagemResponse> excluirServico(@PathVariable("id") int id) throws SQLException {
        catalogoService.excluirServico(id);
        return ResponseEntity.ok(new MensagemResponse(true, "Serviço excluído com sucesso."));
    }

    // --- Barbeiros (RF04) ---

    @GetMapping("/barbeiros")
    public ResponseEntity<List<BarbeiroDTO>> listarBarbeiros(
            @RequestParam(name = "barbeariaId", required = false) Integer barbeariaId) throws SQLException {
        int id = obterBarbeariaIdPadrao(barbeariaId);
        List<Barbeiro> lista = catalogoService.listarBarbeiros(id);
        List<BarbeiroDTO> dtos = lista.stream().map(BarbeiroDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/barbeiros")
    public ResponseEntity<?> criarBarbeiro(@RequestBody BarbeiroDTO dto) throws SQLException {
        if (dto == null || dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Nome do barbeiro é obrigatório."));
        }

        int barbeariaId = obterBarbeariaIdPadrao(dto.getBarbeariaId());
        dto.setBarbeariaId(barbeariaId);

        if (catalogoService.existeBarbeiroComNome(barbeariaId, dto.getNome().trim(), 0)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "Já existe um barbeiro cadastrado com este nome."));
        }

        int id = catalogoService.salvarNovoBarbeiro(dto.toModel());
        dto.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/barbeiros/{id}")
    public ResponseEntity<?> atualizarBarbeiro(
            @PathVariable("id") int id,
            @RequestBody BarbeiroDTO dto) throws SQLException {
        if (dto == null || dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MensagemResponse(false, "Nome do barbeiro é obrigatório."));
        }

        int barbeariaId = obterBarbeariaIdPadrao(dto.getBarbeariaId());
        dto.setBarbeariaId(barbeariaId);
        dto.setId(id);

        if (catalogoService.existeBarbeiroComNome(barbeariaId, dto.getNome().trim(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensagemResponse(false, "Já existe outro barbeiro cadastrado com este nome."));
        }

        catalogoService.atualizarBarbeiro(dto.toModel());
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/barbeiros/{id}")
    public ResponseEntity<MensagemResponse> excluirBarbeiro(@PathVariable("id") int id) throws SQLException {
        catalogoService.excluirBarbeiro(id);
        return ResponseEntity.ok(new MensagemResponse(true, "Barbeiro excluído com sucesso."));
    }
}
