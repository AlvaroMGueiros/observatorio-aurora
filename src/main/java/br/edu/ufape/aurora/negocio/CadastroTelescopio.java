package br.edu.ufape.aurora.negocio;

import java.util.Comparator;
import java.util.List;

import br.edu.ufape.aurora.dados.Repositorio;
import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.modelo.Telescopio;

public class CadastroTelescopio {
    private final Repositorio<Telescopio> repositorio;

    public CadastroTelescopio(Repositorio<Telescopio> repositorio) {
        this.repositorio = repositorio;
    }

    public Telescopio cadastrar(Telescopio telescopio)
            throws RegraNegocioException, PersistenciaException {
        if (telescopio == null) {
            throw new RegraNegocioException("O telescópio a cadastrar não pode ser nulo.");
        }
        telescopio.setNome(Validador.textoObrigatorio(telescopio.getNome(), "nome do telescópio"));
        if (telescopio.getTipo() == null) {
            throw new RegraNegocioException("O tipo do telescópio é obrigatório.");
        }
        if (telescopio.getAberturaMilimetros() <= 0) {
            throw new RegraNegocioException("A abertura do telescópio deve ser maior que zero.");
        }

        for (Telescopio existente : repositorio.listar()) {
            if (existente.getNome().equalsIgnoreCase(telescopio.getNome())) {
                throw new RegraNegocioException(
                        "Já existe um telescópio cadastrado com o nome " + telescopio.getNome() + ".");
            }
        }

        repositorio.inserir(telescopio);
        return telescopio;
    }

    public Telescopio buscarPorId(String id) throws PersistenciaException {
        return repositorio.buscarPorId(id);
    }

    public List<Telescopio> listar() throws PersistenciaException {
        List<Telescopio> telescopios = repositorio.listar();
        telescopios.sort(Comparator.comparing(Telescopio::getNome, String.CASE_INSENSITIVE_ORDER));
        return telescopios;
    }
}
