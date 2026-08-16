package br.com.barbershop.service.fake;

import br.com.barbershop.dao.repository.UsuarioRepository;
import br.com.barbershop.model.Usuario;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Repositório de usuários em memória, usado nos testes de
 * {@link br.com.barbershop.service.AuthService}.
 */
public class FakeUsuarioRepository implements UsuarioRepository {

    private final Map<String, Usuario> porLogin = new LinkedHashMap<>();
    private int proximoId = 1;

    /** Insere um usuário diretamente no fake, sem passar por {@link #inserir}, para preparar o cenário de um teste. */
    public void adicionar(Usuario usuario) {
        if (usuario.getId() == 0) usuario.setId(proximoId++);
        porLogin.put(usuario.getLogin(), usuario);
    }

    @Override
    public Usuario buscarPorLogin(String login) {
        return porLogin.get(login);
    }

    @Override
    public void atualizar(Usuario usuario) {
        porLogin.put(usuario.getLogin(), usuario);
    }

    @Override
    public int inserir(Usuario usuario) {
        adicionar(usuario);
        return usuario.getId();
    }
}
