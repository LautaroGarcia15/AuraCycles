package ar.edu.uade.repuestos.negocio;

import ar.edu.uade.repuestos.datos.UsuarioRepository;
import ar.edu.uade.repuestos.modelo.Rol;
import ar.edu.uade.repuestos.modelo.Usuario;
import ar.edu.uade.repuestos.util.PasswordHash;
import ar.edu.uade.repuestos.util.ReglaNegocioException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Optional;

@Stateless
public class ServicioDeUsuarios {
    @Inject
    private UsuarioRepository usuarioRepository;

    public Optional<Usuario> autenticar(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.porEmail(email.trim().toLowerCase())
                .filter(u -> PasswordHash.verificar(password, u.getPasswordHash()));
    }

    public Usuario registrar(String email, String password, String nombre) {
        String limpio = email == null ? "" : email.trim().toLowerCase();

        if (limpio.isBlank() || !limpio.contains("@")) {
            throw new ReglaNegocioException("El email no es valido.");
        }
        if (password == null || password.length() < 8) {
            throw new ReglaNegocioException("La contrasena necesita al menos 8 caracteres.");
        }
        if (usuarioRepository.existeEmail(limpio)) {
            throw new ReglaNegocioException("Ya hay una cuenta registrada con ese email.");
        }

        Usuario usuario = new Usuario(limpio, PasswordHash.hashear(password), nombre, Rol.CLIENTE);
        return usuarioRepository.guardar(usuario);
    }

    public Usuario actualizarPerfil(Long usuarioId, String nombre, String email) {
        Usuario usuario = usuarioRepository.porId(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe."));

        String limpio = email == null ? "" : email.trim().toLowerCase();
        if (!limpio.equals(usuario.getEmail()) && usuarioRepository.existeEmail(limpio)) {
            throw new ReglaNegocioException("Ese email ya esta en uso.");
        }

        usuario.setNombre(nombre);
        usuario.setEmail(limpio);
        return usuarioRepository.guardar(usuario);
    }

    public void cambiarPassword(Long usuarioId, String actual, String nueva) {
        Usuario usuario = usuarioRepository.porId(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe."));

        if (!PasswordHash.verificar(actual, usuario.getPasswordHash())) {
            throw new ReglaNegocioException("La contrasena actual no coincide.");
        }
        if (nueva == null || nueva.length() < 8) {
            throw new ReglaNegocioException("La contrasena nueva necesita al menos 8 caracteres.");
        }

        usuario.setPasswordHash(PasswordHash.hashear(nueva));
        usuarioRepository.guardar(usuario);
    }

    public Optional<Usuario> porId(Long id) {
        return usuarioRepository.porId(id);
    }
}
