package br.edu.ufape.aurora.negocio;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import br.edu.ufape.aurora.dados.Repositorio;
import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.StatusSessao;
import br.edu.ufape.aurora.modelo.Telescopio;

public class GestaoSessao {
    private final Repositorio<SessaoObservacao> repositorio;
    private final CadastroObservador cadastroObservador;
    private final CadastroTelescopio cadastroTelescopio;
    private final Clock relogio;

    public GestaoSessao(Repositorio<SessaoObservacao> repositorio,
            CadastroObservador cadastroObservador, CadastroTelescopio cadastroTelescopio) {
        this(repositorio, cadastroObservador, cadastroTelescopio, Clock.systemDefaultZone());
    }

    public GestaoSessao(Repositorio<SessaoObservacao> repositorio,
            CadastroObservador cadastroObservador, CadastroTelescopio cadastroTelescopio,
            Clock relogio) {
        this.repositorio = repositorio;
        this.cadastroObservador = cadastroObservador;
        this.cadastroTelescopio = cadastroTelescopio;
        this.relogio = Objects.requireNonNull(relogio, "O relógio do sistema é obrigatório.");
    }

    public SessaoObservacao agendar(String observadorId, String telescopioId, String alvoCeleste,
            LocalDateTime inicio, int duracaoMinutos)
            throws RegraNegocioException, PersistenciaException {
        PessoaObservadora observador = cadastroObservador.buscarPorId(observadorId);
        if (observador == null) {
            throw new RegraNegocioException("Não existe observador com o ID informado.");
        }

        Telescopio telescopio = cadastroTelescopio.buscarPorId(telescopioId);
        if (telescopio == null) {
            throw new RegraNegocioException("Não existe telescópio com o ID informado.");
        }
        if (!telescopio.isAtivo()) {
            throw new RegraNegocioException("O telescópio " + telescopio.getNome() + " está inativo.");
        }

        String alvoValidado = Validador.textoObrigatorio(alvoCeleste, "alvo celeste");
        if (inicio == null || !inicio.isAfter(LocalDateTime.now(relogio))) {
            throw new RegraNegocioException("A sessão deve começar em uma data e hora futuras.");
        }
        if (duracaoMinutos < 15 || duracaoMinutos > 480) {
            throw new RegraNegocioException("A duração deve estar entre 15 e 480 minutos.");
        }

        SessaoObservacao novaSessao = new SessaoObservacao(
                observador, telescopio, alvoValidado, inicio, duracaoMinutos);
        validarConflitos(novaSessao);
        repositorio.inserir(novaSessao);
        return novaSessao;
    }

    public SessaoObservacao concluir(String sessaoId, int qualidadeCeu, String anotacoes)
            throws RegraNegocioException, PersistenciaException {
        SessaoObservacao sessao = buscarSessaoAgendada(sessaoId);
        if (LocalDateTime.now(relogio).isBefore(sessao.getInicio())) {
            throw new RegraNegocioException("Uma sessão futura não pode ser concluída.");
        }
        if (qualidadeCeu < 1 || qualidadeCeu > 5) {
            throw new RegraNegocioException("A qualidade do céu deve ser avaliada de 1 a 5.");
        }

        sessao.setQualidadeCeu(qualidadeCeu);
        sessao.setAnotacoes(Validador.textoObrigatorio(anotacoes, "anotações da observação"));
        sessao.setStatus(StatusSessao.CONCLUIDA);
        repositorio.atualizar(sessao);
        return sessao;
    }

    public SessaoObservacao cancelar(String sessaoId)
            throws RegraNegocioException, PersistenciaException {
        SessaoObservacao sessao = buscarSessaoAgendada(sessaoId);
        if (!LocalDateTime.now(relogio).isBefore(sessao.getInicio())) {
            throw new RegraNegocioException("Uma sessão iniciada não pode ser cancelada.");
        }
        sessao.setStatus(StatusSessao.CANCELADA);
        repositorio.atualizar(sessao);
        return sessao;
    }

    public List<SessaoObservacao> listar() throws PersistenciaException {
        List<SessaoObservacao> sessoes = repositorio.listar();
        sessoes.sort(Comparator.comparing(SessaoObservacao::getInicio));
        return sessoes;
    }

    private SessaoObservacao buscarSessaoAgendada(String sessaoId)
            throws RegraNegocioException, PersistenciaException {
        SessaoObservacao sessao = repositorio.buscarPorId(sessaoId);
        if (sessao == null) {
            throw new RegraNegocioException("Não existe sessão com o ID informado.");
        }
        if (sessao.getStatus() != StatusSessao.AGENDADA) {
            throw new RegraNegocioException(
                    "A sessão está " + sessao.getStatus().getDescricao().toLowerCase()
                            + " e não pode ser alterada.");
        }
        return sessao;
    }

    private void validarConflitos(SessaoObservacao novaSessao) throws PersistenciaException,
            RegraNegocioException {
        for (SessaoObservacao sessaoExistente : repositorio.listar()) {
            if (sessaoExistente.getStatus() != StatusSessao.AGENDADA) {
                continue;
            }

            boolean horariosSobrepostos = novaSessao.getInicio().isBefore(sessaoExistente.getFim())
                    && novaSessao.getFim().isAfter(sessaoExistente.getInicio());
            boolean mesmoTelescopio = novaSessao.getTelescopio().getId()
                    .equals(sessaoExistente.getTelescopio().getId());
            boolean mesmoObservador = novaSessao.getObservador().getId()
                    .equals(sessaoExistente.getObservador().getId());

            if (horariosSobrepostos && mesmoTelescopio) {
                throw new RegraNegocioException(
                        "O telescópio já está reservado nesse intervalo pela sessão "
                                + sessaoExistente.getId() + ".");
            }
            if (horariosSobrepostos && mesmoObservador) {
                throw new RegraNegocioException(
                        "O observador já participa de outra sessão nesse intervalo.");
            }
        }
    }
}
