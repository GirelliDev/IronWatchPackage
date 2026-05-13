    package com.girellidev.ironwatchserver.security;

    import com.girellidev.ironwatchserver.core.Usuario;

    public class AuthService {
    

        public static void validarUsuario(Usuario usuario) {
            if (usuario == null) {
                throw new SecurityException("Usuario inexistente");
            
            }

            if (!usuario.isAtivo()) {
                throw new SecurityException("Usuario inativo");
            }
        }

        public static void validarPermissao(Usuario usuario, int nivelMinimo) {
            validarUsuario(usuario);

            int nivelUsuario = converterPermissao(usuario.getPermissao());

            if (nivelUsuario < nivelMinimo) {
                throw new SecurityException("Permissao insuficiente");
            }
        }

        private static int converterPermissao(String permissao) {
            if (permissao == null) {
                return 0;
            }

            return switch (permissao.toUpperCase()) {
                case "CLIENTE" -> 1;
                case "EMPRESA" -> 2;
                case "ADMIN" -> 3;
                case "MASTER" -> 4;
                default -> 0;
            };
        }
    }
    