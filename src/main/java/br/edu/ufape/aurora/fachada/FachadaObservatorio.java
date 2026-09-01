package br.edu.ufape.aurora.fachada;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.ufape.aurora.dados.Repositorio;
import br.edu.ufape.aurora.dados.RepositorioArquivo;
import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.Telescopio;
import br.edu.ufape.aurora.negocio.CadastroObservador;
import br.edu.ufape.aurora.negocio.CadastroTelescopio;
import br.edu.ufape.aurora.negocio.GestaoSessao;

public class FachadaObservatorio implements OperacoesObservatorio {
    private final CadastroObservador cadastroObservador;
    private final CadastroTelescopio cadastroTelescopio;
    private final GestaoSessao gestaoSessao;

    public FachadaObservatorio() {
        this(Path.of("dados"), Clock.systemDefaultZone());
    }

    public FachadaObservatorio(Path diretorioDados) {
        this(diretorioDados, Clock.systemDefaultZone());
    }

    public FachadaObservatorio(Path diretorioDados, Clock relogio) {
        Repositorio<PessoaObservadora> repositorioObservador = new RepositorioArquivo<>(
                diretorioDados, "observadores.ser", PessoaObservadora.class);
        Repositorio<Telescopio> repositorioTelescopio = new RepositorioArquivo<>(
                diretorioDados, "telescopios.ser", Telescopio.class);
        Repositorio<SessaoObservacao> repositorioSessao = new RepositorioArquivo<>(
                diretorioDados, "sessoes.ser", SessaoObservacao.class);

        cadastroObservador = new CadastroObservador(repositorioObservador);
        cadastroTelescopio = new CadastroTelescopio(repositorioTelescopio);
        gestaoSessao = new GestaoSessao(
                repositorioSessao, cadastroObservador, cadastroTelescopio, relogio);
    }

    @Override
    public PessoaObservadora cadastrarObservador(PessoaObservadora observador)
            throws RegraNegocioException, PersistenciaException {
        return cadastroObservador.cadastrar(observador);
    }

    @Override
    public Telescopio cadastrarTelescopio(Telescopio telescopio)
            throws RegraNegocioException, PersistenciaException {
        return cadastroTelescopio.cadastrar(telescopio);
    }

    @Override
    public SessaoObservacao agendarSessao(String observadorId, String telescopioId,
            String alvoCeleste, LocalDateTime inicio, int duracaoMinutos)
            throws RegraNegocioException, PersistenciaException {
        return gestaoSessao.agendar(
                observadorId, telescopioId, alvoCeleste, inicio, duracaoMinutos);
    }

    @Override
    public SessaoObservacao concluirSessao(String sessaoId, int qualidadeCeu, String anotacoes)
            throws RegraNegocioException, PersistenciaException {
        return gestaoSessao.concluir(sessaoId, qualidadeCeu, anotacoes);
    }

    @Override
    public SessaoObservacao cancelarSessao(String sessaoId)
            throws RegraNegocioException, PersistenciaException {
        return gestaoSessao.cancelar(sessaoId);
    }

    @Override
    public List<PessoaObservadora> listarObservadores() throws PersistenciaException {
        return cadastroObservador.listar();
    }

    @Override
    public List<Telescopio> listarTelescopios() throws PersistenciaException {
        return cadastroTelescopio.listar();
    }

    @Override
    public List<SessaoObservacao> listarSessoes() throws PersistenciaException {
        return gestaoSessao.listar();
    }
}
