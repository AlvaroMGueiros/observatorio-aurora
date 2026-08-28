package br.edu.ufape.aurora.negocio;

import java.util.regex.Pattern;

import br.edu.ufape.aurora.excecao.RegraNegocioException;

final class Validador {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private Validador() {
    }

    static String textoObrigatorio(String valor, String campo) throws RegraNegocioException {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("O campo " + campo + " é obrigatório.");
        }
        return valor.trim();
    }

    static String email(String valor) throws RegraNegocioException {
        String emailNormalizado = textoObrigatorio(valor, "e-mail").toLowerCase();
        if (!EMAIL.matcher(emailNormalizado).matches()) {
            throw new RegraNegocioException("Informe um e-mail válido, como nome@dominio.com.");
        }
        return emailNormalizado;
    }
}
