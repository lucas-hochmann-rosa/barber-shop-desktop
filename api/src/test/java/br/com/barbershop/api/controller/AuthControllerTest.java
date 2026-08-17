package br.com.barbershop.api.controller;

import br.com.barbershop.api.dto.LoginRequest;
import br.com.barbershop.api.helper.InMemoryBarbeariaRepository;
import br.com.barbershop.api.helper.InMemoryUsuarioRepository;
import br.com.barbershop.api.helper.TestUtils;
import br.com.barbershop.model.Usuario;
import br.com.barbershop.service.AuthService;
import br.com.barbershop.service.BarbeariaService;
import br.com.barbershop.util.HashUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private InMemoryUsuarioRepository usuarioRepo;
    private AuthService authService;
    private BarbeariaService barbeariaService;

    @BeforeEach
    void setUp() {
        objectMapper = TestUtils.createObjectMapper();
        usuarioRepo = new InMemoryUsuarioRepository();
        authService = new AuthService(usuarioRepo);
        barbeariaService = new BarbeariaService(new InMemoryBarbeariaRepository());

        AuthController controller = new AuthController(authService, barbeariaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(TestUtils.createJsonConverter())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/login com credenciais corretas deve retornar 200 e dados do usuario")
    void loginComSucesso() throws Exception {
        String salt = HashUtil.gerarSalt();
        Usuario u = new Usuario(1, "lucas", HashUtil.hashComSalt("1234", salt));
        u.setSalt(salt);
        usuarioRepo.inserir(u);

        LoginRequest req = new LoginRequest("lucas", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autenticado").value(true))
                .andExpect(jsonPath("$.login").value("lucas"))
                .andExpect(jsonPath("$.barbeariaId").value(1));
    }

    @Test
    @DisplayName("POST /api/auth/login com credenciais invalidas deve retornar 401 Unauthorized")
    void loginComFalha() throws Exception {
        LoginRequest req = new LoginRequest("invalido", "errada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.autenticado").value(false));
    }

    @Test
    @DisplayName("GET /api/auth/session deve retornar estado do sistema")
    void consultarSessao() throws Exception {
        mockMvc.perform(get("/api/auth/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sistemaConfigurado").value(true))
                .andExpect(jsonPath("$.barbeariaNome").value("Barbershop Matriz"));
    }
}
