package br.edu.ufape.aurora.fachada;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.Telescopio;

public interface OperacoesObservatorio {
    PessoaObservadora cadastrarObservador(PessoaObservadora observador)
            throws RegraNegocioException, PersistenciaException;

    Telescopio cadastrarTelescopio(Telescopio telescopio)
            throws RegraNegocioException, PersistenciaException;

    SessaoObservacao agendarSessao(String observadorId, String telescopioId, String alvoCeleste,
            LocalDateTime inicio, int duracaoMinutos)
            throws RegraNegocioException, PersistenciaException;

    SessaoObservacao concluirSessao(String sessaoId, int qualidadeCeu, String anotacoes)
            throws RegraNegocioException, PersistenciaException;

    SessaoObservacao cancelarSessao(String sessaoId)
            throws RegraNegocioException, PersistenciaException;

    List<PessoaObservadora> listarObservadores() throws PersistenciaException;

    List<Telescopio> listarTelescopios() throws PersistenciaException;

    List<SessaoObservacao> listarSessoes() throws PersistenciaException;
}
