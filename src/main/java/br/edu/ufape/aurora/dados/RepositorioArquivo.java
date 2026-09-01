package br.edu.ufape.aurora.dados;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.modelo.Identificavel;

public class RepositorioArquivo<T extends Identificavel> implements Repositorio<T> {
    private final Path arquivo;
    private final Class<T> tipoEntidade;

    public RepositorioArquivo(Path diretorio, String nomeArquivo, Class<T> tipoEntidade) {
        this.arquivo = diretorio.resolve(nomeArquivo);
        this.tipoEntidade = tipoEntidade;
    }

    @Override
    public synchronized void inserir(T entidade) throws PersistenciaException {
        List<T> entidades = carregar();
        entidades.add(entidade);
        salvar(entidades);
    }

    @Override
    public synchronized void atualizar(T entidade) throws PersistenciaException {
        List<T> entidades = carregar();
        for (int indice = 0; indice < entidades.size(); indice++) {
            if (entidades.get(indice).getId().equals(entidade.getId())) {
                entidades.set(indice, entidade);
                salvar(entidades);
                return;
            }
        }
        throw new PersistenciaException(
                "Não foi possível atualizar: o registro " + entidade.getId() + " não existe.", null);
    }

    @Override
    public synchronized T buscarPorId(String id) throws PersistenciaException {
        for (T entidade : carregar()) {
            if (entidade.getId().equals(id)) {
                return entidade;
            }
        }
        return null;
    }

    @Override
    public synchronized List<T> listar() throws PersistenciaException {
        return carregar();
    }

    private List<T> carregar() throws PersistenciaException {
        if (Files.notExists(arquivo)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            Object conteudo = entrada.readObject();
            if (!(conteudo instanceof List<?> registros)) {
                throw new PersistenciaException(
                        "O arquivo " + arquivo.getFileName() + " possui formato inválido.", null);
            }

            List<T> entidades = new ArrayList<>(registros.size());
            for (Object registro : registros) {
                if (!tipoEntidade.isInstance(registro)) {
                    throw new PersistenciaException(
                            "O arquivo " + arquivo.getFileName() + " contém um registro incompatível.", null);
                }
                entidades.add(tipoEntidade.cast(registro));
            }
            return entidades;
        } catch (IOException | ClassNotFoundException exception) {
            throw new PersistenciaException(
                    "Não foi possível ler o arquivo " + arquivo.getFileName() + ".", exception);
        }
    }

    private void salvar(List<T> entidades) throws PersistenciaException {
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        try {
            Files.createDirectories(arquivo.getParent());
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(entidades);
            }
            moverTemporario(temporario);
        } catch (IOException exception) {
            try {
                Files.deleteIfExists(temporario);
            } catch (IOException ignored) {
                exception.addSuppressed(ignored);
            }
            throw new PersistenciaException(
                    "Não foi possível salvar o arquivo " + arquivo.getFileName() + ".", exception);
        }
    }

    private void moverTemporario(Path temporario) throws IOException {
        try {
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
