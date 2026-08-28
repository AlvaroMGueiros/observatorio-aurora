package br.edu.ufape.aurora.modelo;

import java.time.LocalDate;
import java.util.UUID;

public abstract class PessoaObservadora implements Identificavel {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private String email;
    private LocalDate dataCadastro;

    protected PessoaObservadora(String nome, String email) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = email;
        this.dataCadastro = LocalDate.now();
    }

    public abstract String getTipo();

    public abstract String getVinculo();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return nome + " | " + getTipo() + " | " + getVinculo() + " | " + email + " | ID: " + id;
    }
}
