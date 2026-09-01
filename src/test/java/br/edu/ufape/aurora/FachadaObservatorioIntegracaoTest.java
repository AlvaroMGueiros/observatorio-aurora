package br.edu.ufape.aurora;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.fachada.FachadaObservatorio;
import br.edu.ufape.aurora.fachada.OperacoesObservatorio;
import br.edu.ufape.aurora.modelo.AstronomoAmador;
import br.edu.ufape.aurora.modelo.NivelExperiencia;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.Pesquisador;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.StatusSessao;
import br.edu.ufape.aurora.modelo.Telescopio;
import br.edu.ufape.aurora.modelo.TipoTelescopio;

class FachadaObservatorioIntegracaoTest {
    private static final ZoneId FUSO_HORARIO = ZoneId.of("America/Fortaleza");
    private static final LocalDateTime INSTANTE_INICIAL = LocalDateTime.of(2026, 9, 1, 18, 0);

    @TempDir
    private Path diretorioDados;

    @Test
    void devePersistirOCicloCompletoDeObservacao() throws Exception {
        OperacoesObservatorio fachada = criarFachada(INSTANTE_INICIAL);
        PessoaObservadora observador = fachada.cadastrarObservador(new AstronomoAmador(
                "Lina Costa", "lina@aurora.org", NivelExperiencia.AVANCADO, "Clube Órion"));
        Telescopio telescopio = fachada.cadastrarTelescopio(new Telescopio(
                "Gaia 300", TipoTelescopio.REFLETOR, 300));
        LocalDateTime inicio = INSTANTE_INICIAL.plusHours(2);
        SessaoObservacao sessao = fachada.agendarSessao(
                observador.getId(), telescopio.getId(), "Nebulosa de Órion", inicio, 90);

        assertThrows(RegraNegocioException.class,
                () -> fachada.concluirSessao(sessao.getId(), 5, "Céu limpo"));

        OperacoesObservatorio fachadaDuranteSessao = criarFachada(inicio.plusMinutes(30));
        fachadaDuranteSessao.concluirSessao(sessao.getId(), 5, "Estrutura central muito nítida.");

        OperacoesObservatorio fachadaReaberta = criarFachada(inicio.plusHours(3));
        assertEquals(1, fachadaReaberta.listarObservadores().size());
        assertEquals(1, fachadaReaberta.listarTelescopios().size());
        assertEquals(1, fachadaReaberta.listarSessoes().size());
        assertEquals(StatusSessao.CONCLUIDA,
                fachadaReaberta.listarSessoes().get(0).getStatus());
        assertEquals("Estrutura central muito nítida.",
                fachadaReaberta.listarSessoes().get(0).getAnotacoes());
        assertTrue(Files.exists(diretorioDados.resolve("observadores.ser")));
        assertTrue(Files.exists(diretorioDados.resolve("telescopios.ser")));
        assertTrue(Files.exists(diretorioDados.resolve("sessoes.ser")));
        try (var arquivos = Files.list(diretorioDados)) {
            assertFalse(arquivos.anyMatch(path -> path.toString().endsWith(".tmp")));
        }
    }

    @Test
    void deveValidarCadastrosEConflitosDeAgenda() throws Exception {
        OperacoesObservatorio fachada = criarFachada(INSTANTE_INICIAL);

        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarObservador(null));
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarObservador(
                new AstronomoAmador("", "email-invalido", NivelExperiencia.INICIANTE, "")));
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarObservador(
                new Pesquisador("Cecília", "cecilia@aurora.org", "", "Astrofísica")));

        PessoaObservadora ana = fachada.cadastrarObservador(new AstronomoAmador(
                "  Ana Silva  ", "ANA@AURORA.ORG", NivelExperiencia.INTERMEDIARIO, "Sirius"));
        PessoaObservadora bia = fachada.cadastrarObservador(new Pesquisador(
                "Bia Lima", "bia@ufape.edu.br", "UFAPE", "Exoplanetas"));
        assertEquals("Ana Silva", ana.getNome());
        assertEquals("ana@aurora.org", ana.getEmail());
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarObservador(
                new AstronomoAmador("Outra Ana", "ANA@AURORA.ORG",
                        NivelExperiencia.INICIANTE, "")));

        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarTelescopio(null));
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarTelescopio(
                new Telescopio("Sem tipo", null, 100)));
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarTelescopio(
                new Telescopio("Sem abertura", TipoTelescopio.REFLETOR, 0)));

        Telescopio gaia = fachada.cadastrarTelescopio(
                new Telescopio("Gaia 300", TipoTelescopio.REFLETOR, 300));
        Telescopio kepler = fachada.cadastrarTelescopio(
                new Telescopio("Kepler 120", TipoTelescopio.REFRATOR, 120));
        assertThrows(RegraNegocioException.class, () -> fachada.cadastrarTelescopio(
                new Telescopio(" gaia 300 ", TipoTelescopio.RADIO, 500)));

        LocalDateTime inicio = INSTANTE_INICIAL.plusHours(2);
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                "id-inexistente", gaia.getId(), "Marte", inicio, 60));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), "id-inexistente", "Marte", inicio, 60));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), gaia.getId(), "", inicio, 60));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), gaia.getId(), "Marte", INSTANTE_INICIAL.minusMinutes(1), 60));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), gaia.getId(), "Marte", inicio, 14));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), gaia.getId(), "Marte", inicio, 481));

        SessaoObservacao principal = fachada.agendarSessao(
                ana.getId(), gaia.getId(), "Nebulosa de Órion", inicio, 90);
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                bia.getId(), gaia.getId(), "Júpiter", inicio.plusMinutes(30), 30));
        assertThrows(RegraNegocioException.class, () -> fachada.agendarSessao(
                ana.getId(), kepler.getId(), "Saturno", inicio.plusMinutes(30), 30));

        SessaoObservacao adjacente = fachada.agendarSessao(
                bia.getId(), gaia.getId(), "Júpiter", inicio.plusMinutes(90), 30);
        fachada.cancelarSessao(adjacente.getId());
        assertThrows(RegraNegocioException.class,
                () -> fachada.cancelarSessao(adjacente.getId()));
        assertEquals(StatusSessao.AGENDADA,
                fachada.listarSessoes().stream()
                        .filter(sessao -> sessao.getId().equals(principal.getId()))
                        .findFirst().orElseThrow().getStatus());
    }

    @Test
    void deveImpedirCancelamentoDepoisDoInicio() throws Exception {
        OperacoesObservatorio fachada = criarFachada(INSTANTE_INICIAL);
        PessoaObservadora observador = fachada.cadastrarObservador(new AstronomoAmador(
                "Nara", "nara@aurora.org", NivelExperiencia.INICIANTE, ""));
        Telescopio telescopio = fachada.cadastrarTelescopio(new Telescopio(
                "Aurora 80", TipoTelescopio.REFRATOR, 80));
        LocalDateTime inicio = INSTANTE_INICIAL.plusHours(1);
        SessaoObservacao sessao = fachada.agendarSessao(
                observador.getId(), telescopio.getId(), "Lua", inicio, 30);

        OperacoesObservatorio fachadaAposInicio = criarFachada(inicio.plusMinutes(1));
        assertThrows(RegraNegocioException.class,
                () -> fachadaAposInicio.cancelarSessao(sessao.getId()));
    }

    @Test
    void deveInformarArquivoSerializadoCorrompido() throws Exception {
        Files.writeString(diretorioDados.resolve("telescopios.ser"), "conteúdo inválido");
        OperacoesObservatorio fachada = criarFachada(INSTANTE_INICIAL);

        PersistenciaException exception = assertThrows(
                PersistenciaException.class, fachada::listarTelescopios);
        assertTrue(exception.getMessage().contains("telescopios.ser"));
    }

    private OperacoesObservatorio criarFachada(LocalDateTime dataHora) {
        Clock relogio = Clock.fixed(dataHora.atZone(FUSO_HORARIO).toInstant(), FUSO_HORARIO);
        return new FachadaObservatorio(diretorioDados, relogio);
    }
}
