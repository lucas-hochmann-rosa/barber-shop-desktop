package br.com.barbershop.api.helper;

import br.com.barbershop.dao.repository.UsuarioRepository;
import br.com.barbershop.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Usuario buscarPorLogin(String login) {
        return usuarios.stream()
                .filter(u -> u.getLogin().equalsIgnoreCase(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int inserir(Usuario usuario) {
        usuario.setId(nextId++);
        usuarios.add(usuario);
        return usuario.getId();
    }

    @Override
    public void atualizar(Usuario usuario) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuario.getId()) {
                usuarios.set(i, usuario);
                return;
            }
        }
    }
}
