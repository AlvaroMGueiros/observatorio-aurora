package br.edu.ufape.aurora.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SessaoObservacao implements Identificavel {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String id;
    private PessoaObservadora observador;
    private Telescopio telescopio;
    private String alvoCeleste;
    private LocalDateTime inicio;
    private int duracaoMinutos;
    private StatusSessao status;
    private Integer qualidadeCeu;
    private String anotacoes;

    public SessaoObservacao(PessoaObservadora observador, Telescopio telescopio, String alvoCeleste,
            LocalDateTime inicio, int duracaoMinutos) {
        this.id = UUID.randomUUID().toString();
        this.observador = observador;
        this.telescopio = telescopio;
        this.alvoCeleste = alvoCeleste;
        this.inicio = inicio;
        this.duracaoMinutos = duracaoMinutos;
        this.status = StatusSessao.AGENDADA;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PessoaObservadora getObservador() {
        return observador;
    }

    public void setObservador(PessoaObservadora observador) {
        this.observador = observador;
    }

    public Telescopio getTelescopio() {
        return telescopio;
    }

    public void setTelescopio(Telescopio telescopio) {
        this.telescopio = telescopio;
    }

    public String getAlvoCeleste() {
        return alvoCeleste;
    }

    public void setAlvoCeleste(String alvoCeleste) {
        this.alvoCeleste = alvoCeleste;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public StatusSessao getStatus() {
        return status;
    }

    public void setStatus(StatusSessao status) {
        this.status = status;
    }

    public Integer getQualidadeCeu() {
        return qualidadeCeu;
    }

    public void setQualidadeCeu(Integer qualidadeCeu) {
        this.qualidadeCeu = qualidadeCeu;
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public LocalDateTime getFim() {
        return inicio.plusMinutes(duracaoMinutos);
    }

    @Override
    public String toString() {
        String resultado = status == StatusSessao.CONCLUIDA
                ? " | céu " + qualidadeCeu + "/5 | " + anotacoes
                : "";
        return inicio.format(FORMATO_DATA) + " | " + alvoCeleste + " | " + observador.getNome()
                + " | " + telescopio.getNome() + " | " + duracaoMinutos + " min | "
                + status.getDescricao() + resultado + " | ID: " + id;
    }
}
