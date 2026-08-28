package br.edu.ufape.aurora.modelo;

public class Pesquisador extends PessoaObservadora {
    private static final long serialVersionUID = 1L;

    private String instituicao;
    private String areaPesquisa;

    public Pesquisador(String nome, String email, String instituicao, String areaPesquisa) {
        super(nome, email);
        this.instituicao = instituicao;
        this.areaPesquisa = areaPesquisa;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getAreaPesquisa() {
        return areaPesquisa;
    }

    public void setAreaPesquisa(String areaPesquisa) {
        this.areaPesquisa = areaPesquisa;
    }

    @Override
    public String getTipo() {
        return "Pesquisador";
    }

    @Override
    public String getVinculo() {
        return instituicao + " — " + areaPesquisa;
    }
}
