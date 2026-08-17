package br.com.barbershop.service;

import br.com.barbershop.model.Usuario;
import br.com.barbershop.service.fake.FakeUsuarioRepository;
import br.com.barbershop.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de {@link AuthService} com repositório em memória: autenticação
 * bem-sucedida e malsucedida no esquema atual (PBKDF2 com salt) e o
 * upgrade silencioso de contas antigas (hash SHA-256 legado, sem salt).
 */
class AuthServiceTest {

    private FakeUsuarioRepository usuarioRepository;
    private AuthService service;

    @BeforeEach
    void setUp() {
        usuarioRepository = new FakeUsuarioRepository();
        service = new AuthService(usuarioRepository);
    }

    @Test
    void autenticarComSenhaCorretaRetornaUsuario() throws Exception {
        String salt = HashUtil.gerarSalt();
        Usuario u = new Usuario(1, "admin", HashUtil.hashComSalt("senha123", salt));
        u.setSalt(salt);
        usuarioRepository.adicionar(u);

        Usuario autenticado = service.autenticar("admin", "senha123");

        assertNotNull(autenticado);
        assertEquals("admin", autenticado.getLogin());
    }

    @Test
    void autenticarComSenhaIncorretaRetornaNulo() throws Exception {
        String salt = HashUtil.gerarSalt();
        Usuario u = new Usuario(1, "admin", HashUtil.hashComSalt("senha123", salt));
        u.setSalt(salt);
        usuarioRepository.adicionar(u);

        assertNull(service.autenticar("admin", "senhaErrada"));
    }

    @Test
    void autenticarComLoginInexistenteRetornaNulo() throws Exception {
        assertNull(service.autenticar("naoexiste", "qualquerSenha"));
    }

    @Test
    void autenticarContaLegadaComSenhaCorretaFazUpgradeSilenciosoParaSalt() throws Exception {
        Usuario u = new Usuario(1, "admin", HashUtil.hashSHA256("senhaAntiga"));
        // conta legada: sem salt
        usuarioRepository.adicionar(u);

        Usuario autenticado = service.autenticar("admin", "senhaAntiga");

        assertNotNull(autenticado);
        assertNotNull(autenticado.getSalt(), "a conta deveria ganhar um salt após o upgrade");
        assertEquals(HashUtil.hashComSalt("senhaAntiga", autenticado.getSalt()), autenticado.getSenhaHash());

        // login seguinte já usa o esquema novo, sem precisar do fallback legado
        Usuario segundoLogin = service.autenticar("admin", "senhaAntiga");
        assertNotNull(segundoLogin);
    }

    @Test
    void autenticarContaLegadaComSenhaIncorretaRetornaNulo() throws Exception {
        Usuario u = new Usuario(1, "admin", HashUtil.hashSHA256("senhaAntiga"));
        usuarioRepository.adicionar(u);

        assertNull(service.autenticar("admin", "senhaErrada"));
    }
}
