package br.edu.ufape.aurora.modelo;

public enum StatusSessao {
    AGENDADA("Agendada"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusSessao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
