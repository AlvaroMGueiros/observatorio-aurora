package br.edu.ufape.aurora.negocio;

import java.util.Comparator;
import java.util.List;

import br.edu.ufape.aurora.dados.Repositorio;
import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.modelo.AstronomoAmador;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.Pesquisador;

public class CadastroObservador {
    private final Repositorio<PessoaObservadora> repositorio;

    public CadastroObservador(Repositorio<PessoaObservadora> repositorio) {
        this.repositorio = repositorio;
    }

    public PessoaObservadora cadastrar(PessoaObservadora observador)
            throws RegraNegocioException, PersistenciaException {
        if (observador == null) {
            throw new RegraNegocioException("O observador a cadastrar não pode ser nulo.");
        }

        observador.setNome(Validador.textoObrigatorio(observador.getNome(), "nome"));
        observador.setEmail(Validador.email(observador.getEmail()));
        validarDadosEspecificos(observador);

        for (PessoaObservadora existente : repositorio.listar()) {
            if (existente.getEmail().equalsIgnoreCase(observador.getEmail())) {
                throw new RegraNegocioException(
                        "Já existe um observador cadastrado com o e-mail " + observador.getEmail() + ".");
            }
        }

        repositorio.inserir(observador);
        return observador;
    }

    public PessoaObservadora buscarPorId(String id) throws PersistenciaException {
        return repositorio.buscarPorId(id);
    }

    public List<PessoaObservadora> listar() throws PersistenciaException {
        List<PessoaObservadora> observadores = repositorio.listar();
        observadores.sort(Comparator.comparing(PessoaObservadora::getNome, String.CASE_INSENSITIVE_ORDER));
        return observadores;
    }

    private void validarDadosEspecificos(PessoaObservadora observador) throws RegraNegocioException {
        if (observador instanceof AstronomoAmador amador) {
            if (amador.getNivelExperiencia() == null) {
                throw new RegraNegocioException("O nível de experiência do astrônomo amador é obrigatório.");
            }
            return;
        }

        if (observador instanceof Pesquisador pesquisador) {
            pesquisador.setInstituicao(
                    Validador.textoObrigatorio(pesquisador.getInstituicao(), "instituição"));
            pesquisador.setAreaPesquisa(
                    Validador.textoObrigatorio(pesquisador.getAreaPesquisa(), "área de pesquisa"));
            return;
        }

        throw new RegraNegocioException("O tipo de observador informado não é aceito pelo sistema.");
    }
}
