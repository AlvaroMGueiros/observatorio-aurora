package br.edu.ufape.aurora;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.fachada.FachadaObservatorio;
import br.edu.ufape.aurora.fachada.OperacoesObservatorio;
import br.edu.ufape.aurora.modelo.AstronomoAmador;
import br.edu.ufape.aurora.modelo.NivelExperiencia;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.StatusSessao;
import br.edu.ufape.aurora.modelo.Telescopio;
import br.edu.ufape.aurora.modelo.TipoTelescopio;

public final class VerificacaoIntegracao {
    private VerificacaoIntegracao() {
    }

    public static void main(String[] args) throws Exception {
        Path diretorio = Files.createTempDirectory(Path.of("target"), "verificacao-");
        OperacoesObservatorio fachada = new FachadaObservatorio(diretorio);

        PessoaObservadora observador = fachada.cadastrarObservador(new AstronomoAmador(
                "Lina Costa", "lina@aurora.org", NivelExperiencia.AVANCADO, "Clube Órion"));
        Telescopio telescopio = fachada.cadastrarTelescopio(new Telescopio(
                "Gaia 300", TipoTelescopio.REFLETOR, 300));
        LocalDateTime inicio = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
        SessaoObservacao sessao = fachada.agendarSessao(
                observador.getId(), telescopio.getId(), "Nebulosa de Órion", inicio, 90);

        exigirExcecao(() -> fachada.cadastrarObservador(new AstronomoAmador(
                "Outra Lina", "LINA@AURORA.ORG", NivelExperiencia.INICIANTE, "")));
        exigirExcecao(() -> fachada.agendarSessao(
                observador.getId(), telescopio.getId(), "Júpiter", inicio.plusMinutes(30), 60));

        OperacoesObservatorio fachadaReaberta = new FachadaObservatorio(diretorio);
        exigir(fachadaReaberta.listarObservadores().size() == 1,
                "O observador não foi recuperado do arquivo.");
        exigir(fachadaReaberta.listarTelescopios().size() == 1,
                "O telescópio não foi recuperado do arquivo.");
        exigir(fachadaReaberta.listarSessoes().size() == 1,
                "A sessão não foi recuperada do arquivo.");

        fachadaReaberta.concluirSessao(sessao.getId(), 5, "Estrutura central muito nítida.");
        exigir(fachadaReaberta.listarSessoes().get(0).getStatus() == StatusSessao.CONCLUIDA,
                "A conclusão da sessão não foi persistida.");

        System.out.println("Verificação concluída: persistência, validações e agenda estão corretas.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalStateException(mensagem);
        }
    }

    private static void exigirExcecao(Operacao operacao) throws Exception {
        try {
            operacao.executar();
        } catch (RegraNegocioException exception) {
            return;
        }
        throw new IllegalStateException("Era esperada uma RegraNegocioException.");
    }

    @FunctionalInterface
    private interface Operacao {
        void executar() throws Exception;
    }
}
