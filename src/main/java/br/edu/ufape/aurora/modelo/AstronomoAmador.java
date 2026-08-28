package br.edu.ufape.aurora.modelo;

public class AstronomoAmador extends PessoaObservadora {
    private static final long serialVersionUID = 1L;

    private NivelExperiencia nivelExperiencia;
    private String clubeAstronomia;

    public AstronomoAmador(String nome, String email, NivelExperiencia nivelExperiencia,
            String clubeAstronomia) {
        super(nome, email);
        this.nivelExperiencia = nivelExperiencia;
        this.clubeAstronomia = clubeAstronomia;
    }

    public NivelExperiencia getNivelExperiencia() {
        return nivelExperiencia;
    }

    public void setNivelExperiencia(NivelExperiencia nivelExperiencia) {
        this.nivelExperiencia = nivelExperiencia;
    }

    public String getClubeAstronomia() {
        return clubeAstronomia;
    }

    public void setClubeAstronomia(String clubeAstronomia) {
        this.clubeAstronomia = clubeAstronomia;
    }

    @Override
    public String getTipo() {
        return "Astrônomo amador";
    }

    @Override
    public String getVinculo() {
        String clube = clubeAstronomia == null || clubeAstronomia.isBlank()
                ? "sem clube"
                : clubeAstronomia;
        return nivelExperiencia.getDescricao() + " — " + clube;
    }
}
