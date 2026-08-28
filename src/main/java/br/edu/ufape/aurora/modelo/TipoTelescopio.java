package br.edu.ufape.aurora.modelo;

public enum TipoTelescopio {
    REFLETOR("Refletor"),
    REFRATOR("Refrator"),
    CATADIOPTRICO("Catadióptrico"),
    RADIO("Radiotelescópio");

    private final String descricao;

    TipoTelescopio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
