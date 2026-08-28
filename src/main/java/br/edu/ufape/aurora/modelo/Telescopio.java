package br.edu.ufape.aurora.modelo;

import java.util.UUID;

public class Telescopio implements Identificavel {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private TipoTelescopio tipo;
    private double aberturaMilimetros;
    private boolean ativo;

    public Telescopio(String nome, TipoTelescopio tipo, double aberturaMilimetros) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.tipo = tipo;
        this.aberturaMilimetros = aberturaMilimetros;
        this.ativo = true;
    }

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

    public TipoTelescopio getTipo() {
        return tipo;
    }

    public void setTipo(TipoTelescopio tipo) {
        this.tipo = tipo;
    }

    public double getAberturaMilimetros() {
        return aberturaMilimetros;
    }

    public void setAberturaMilimetros(double aberturaMilimetros) {
        this.aberturaMilimetros = aberturaMilimetros;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        String disponibilidade = ativo ? "ativo" : "inativo";
        return nome + " | " + tipo.getDescricao() + " | " + aberturaMilimetros + " mm | "
                + disponibilidade + " | ID: " + id;
    }
}
