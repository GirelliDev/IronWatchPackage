package com.girellidev.ironwatchserver.security;
import com.girellidev.ironwatchserver.core.Usuario;

public class AuthService {

    public static void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new SecurityException("Usuário inexistente");
        }
        if (!usuario.isAtivo()) {
            throw new SecurityException("Usuário inativo");
        }
    }

    public static void validarPermissao(Usuario usuario, int nivelMinimo) {
        if ("ADMIN".equals(usuario.getPermissao())) {
            throw new SecurityException("Permissão insuficiente");
        }
    }
}