package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.LoginRequest;
import br.com.barbershop.api.dto.LoginResponse;
import br.com.barbershop.api.dto.MensagemResponse;
import br.com.barbershop.model.Barbearia;
import br.com.barbershop.model.Usuario;
import br.com.barbershop.service.AuthService;
import br.com.barbershop.service.BarbeariaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

/**
 * Endpoints REST para autenticação e sessão do usuário (RF02).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final BarbeariaService barbeariaService;

    public AuthController(AuthService authService, BarbeariaService barbeariaService) {
        this.authService = authService;
        this.barbeariaService = barbeariaService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws SQLException {
        if (request == null || request.getLogin() == null || request.getSenha() == null) {
            return ResponseEntity.badRequest().body(new LoginResponse(false, 0, 0, null, null, "Login e senha são obrigatórios."));
        }

        Usuario usuario = authService.autenticar(request.getLogin().trim(), request.getSenha());
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, 0, 0, null, null, "Credenciais inválidas. Verifique usuário e senha."));
        }

        String nomeBarbearia = "Barbershop";
        try {
            Barbearia b = barbeariaService.buscarPorId(usuario.getBarbeariaId());
            if (b != null && b.getNome() != null) {
                nomeBarbearia = b.getNome();
            }
        } catch (Exception ignored) {}

        LoginResponse response = new LoginResponse(
                true,
                usuario.getId(),
                usuario.getBarbeariaId(),
                usuario.getLogin(),
                nomeBarbearia,
                "Autenticação realizada com sucesso."
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session")
    public ResponseEntity<?> consultarSessao() throws SQLException {
        Barbearia b = barbeariaService.buscarPrimeira();
        boolean barbeariaCadastrada = (b != null);
        return ResponseEntity.ok(new Object() {
            public final boolean sistemaConfigurado = barbeariaCadastrada;
            public final int barbeariaId = barbeariaCadastrada ? b.getId() : 0;
            public final String barbeariaNome = barbeariaCadastrada ? b.getNome() : null;
        });
    }

    @PostMapping("/logout")
    public ResponseEntity<MensagemResponse> logout() {
        return ResponseEntity.ok(new MensagemResponse(true, "Sessão encerrada."));
    }
}
