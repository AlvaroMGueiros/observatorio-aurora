package br.edu.ufape.aurora;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.edu.ufape.aurora.dados.RepositorioArquivo;
import br.edu.ufape.aurora.fachada.FachadaObservatorio;
import br.edu.ufape.aurora.modelo.AstronomoAmador;
import br.edu.ufape.aurora.modelo.Identificavel;
import br.edu.ufape.aurora.modelo.NivelExperiencia;
import br.edu.ufape.aurora.modelo.Pesquisador;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.StatusSessao;
import br.edu.ufape.aurora.modelo.Telescopio;
import br.edu.ufape.aurora.modelo.TipoTelescopio;
import br.edu.ufape.aurora.negocio.CadastroObservador;
import br.edu.ufape.aurora.negocio.CadastroTelescopio;
import br.edu.ufape.aurora.negocio.GestaoSessao;

class EncapsulamentoTest {
    @Test
    void todosOsAtributosDevemSerPrivados() {
        List<Class<?>> classes = List.of(
                AstronomoAmador.class, Pesquisador.class, PessoaObservadora.class,
                Telescopio.class, SessaoObservacao.class, NivelExperiencia.class,
                TipoTelescopio.class, StatusSessao.class, RepositorioArquivo.class,
                CadastroObservador.class, CadastroTelescopio.class, GestaoSessao.class,
                FachadaObservatorio.class);

        for (Class<?> classe : classes) {
            for (Field atributo : classe.getDeclaredFields()) {
                if (!atributo.isEnumConstant()) {
                    assertTrue(Modifier.isPrivate(atributo.getModifiers()),
                            () -> classe.getSimpleName() + "." + atributo.getName()
                                    + " deveria ser privado.");
                }
            }
        }
    }

    @Test
    void entidadesDevemPossuirGettersESetters() {
        List<Class<?>> entidades = List.of(
                PessoaObservadora.class, AstronomoAmador.class, Pesquisador.class,
                Telescopio.class, SessaoObservacao.class);

        for (Class<?> entidade : entidades) {
            for (Field atributo : entidade.getDeclaredFields()) {
                if (Modifier.isStatic(atributo.getModifiers())) {
                    continue;
                }
                String sufixo = Character.toUpperCase(atributo.getName().charAt(0))
                        + atributo.getName().substring(1);
                String getter = atributo.getType() == boolean.class ? "is" + sufixo : "get" + sufixo;
                assertDoesNotThrow(() -> entidade.getMethod(getter));
                assertDoesNotThrow(() -> entidade.getMethod("set" + sufixo, atributo.getType()));
            }
        }
    }

    @Test
    void entidadesDevemSerSerializaveis() {
        assertTrue(Serializable.class.isAssignableFrom(Identificavel.class));
        assertTrue(Serializable.class.isAssignableFrom(PessoaObservadora.class));
        assertTrue(Serializable.class.isAssignableFrom(Telescopio.class));
        assertTrue(Serializable.class.isAssignableFrom(SessaoObservacao.class));
    }
}
