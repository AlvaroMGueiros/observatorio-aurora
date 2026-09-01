package br.edu.ufape.aurora.dados;

import java.util.List;

import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.modelo.Identificavel;

public interface Repositorio<T extends Identificavel> {
    void inserir(T entidade) throws PersistenciaException;

    void atualizar(T entidade) throws PersistenciaException;

    T buscarPorId(String id) throws PersistenciaException;

    List<T> listar() throws PersistenciaException;
}
