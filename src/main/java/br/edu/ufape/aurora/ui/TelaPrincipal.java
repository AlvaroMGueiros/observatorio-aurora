package br.edu.ufape.aurora.ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import br.edu.ufape.aurora.excecao.PersistenciaException;
import br.edu.ufape.aurora.excecao.RegraNegocioException;
import br.edu.ufape.aurora.fachada.FachadaObservatorio;
import br.edu.ufape.aurora.fachada.OperacoesObservatorio;
import br.edu.ufape.aurora.modelo.AstronomoAmador;
import br.edu.ufape.aurora.modelo.NivelExperiencia;
import br.edu.ufape.aurora.modelo.PessoaObservadora;
import br.edu.ufape.aurora.modelo.Pesquisador;
import br.edu.ufape.aurora.modelo.SessaoObservacao;
import br.edu.ufape.aurora.modelo.Telescopio;
import br.edu.ufape.aurora.modelo.TipoTelescopio;

public class TelaPrincipal {
    private static final Scanner ENTRADA = new Scanner(System.in);
    private static final OperacoesObservatorio FACHADA = new FachadaObservatorio();
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private TelaPrincipal() {
    }

    public static void main(String[] args) {
        configurarSaidaUtf8();
        exibirCabecalho();
        int opcao;
        do {
            opcao = lerOpcaoMenu();
            executarOpcao(opcao);
        } while (opcao != 0);
        System.out.println("\nAté a próxima noite de descobertas!");
    }

    private static void configurarSaidaUtf8() {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }

    private static void exibirCabecalho() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║        OBSERVATÓRIO AURORA           ║");
        System.out.println("║  Ciência, comunidade e céu aberto    ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private static int lerOpcaoMenu() {
        System.out.println("\n1  Cadastrar observador");
        System.out.println("2  Cadastrar telescópio");
        System.out.println("3  Agendar sessão de observação");
        System.out.println("4  Concluir sessão e registrar diário");
        System.out.println("5  Cancelar sessão");
        System.out.println("6  Listar observadores");
        System.out.println("7  Listar telescópios");
        System.out.println("8  Listar sessões");
        System.out.println("0  Encerrar");
        return lerInteiro("Escolha: ");
    }

    private static void executarOpcao(int opcao) {
        try {
            switch (opcao) {
                case 1 -> cadastrarObservador();
                case 2 -> cadastrarTelescopio();
                case 3 -> agendarSessao();
                case 4 -> concluirSessao();
                case 5 -> cancelarSessao();
                case 6 -> imprimirLista("OBSERVADORES", FACHADA.listarObservadores());
                case 7 -> imprimirLista("TELESCÓPIOS", FACHADA.listarTelescopios());
                case 8 -> imprimirLista("SESSÕES DE OBSERVAÇÃO", FACHADA.listarSessoes());
                case 0 -> { }
                default -> System.err.println("Opção inexistente. Escolha um número de 0 a 8.");
            }
        } catch (RegraNegocioException | PersistenciaException exception) {
            System.err.println("Não foi possível concluir a operação: " + exception.getMessage());
        }
    }

    private static void cadastrarObservador()
            throws RegraNegocioException, PersistenciaException {
        System.out.println("\n1  Astrônomo amador");
        System.out.println("2  Pesquisador");
        int tipo = lerInteiro("Tipo de observador: ");
        String nome = lerTexto("Nome: ");
        String email = lerTexto("E-mail: ");

        PessoaObservadora observador;
        if (tipo == 1) {
            NivelExperiencia nivel = lerEnum("Nível de experiência", NivelExperiencia.values());
            String clube = lerTexto("Clube de astronomia (opcional): ");
            observador = new AstronomoAmador(nome, email, nivel, clube);
        } else if (tipo == 2) {
            String instituicao = lerTexto("Instituição: ");
            String areaPesquisa = lerTexto("Área de pesquisa: ");
            observador = new Pesquisador(nome, email, instituicao, areaPesquisa);
        } else {
            throw new RegraNegocioException("O tipo de observador deve ser 1 ou 2.");
        }

        PessoaObservadora cadastrado = FACHADA.cadastrarObservador(observador);
        System.out.println("Observador cadastrado com sucesso. ID: " + cadastrado.getId());
    }

    private static void cadastrarTelescopio()
            throws RegraNegocioException, PersistenciaException {
        String nome = lerTexto("Nome do telescópio: ");
        TipoTelescopio tipo = lerEnum("Tipo", TipoTelescopio.values());
        double abertura = lerDecimal("Abertura em milímetros: ");
        Telescopio cadastrado = FACHADA.cadastrarTelescopio(
                new Telescopio(nome, tipo, abertura));
        System.out.println("Telescópio cadastrado com sucesso. ID: " + cadastrado.getId());
    }

    private static void agendarSessao()
            throws RegraNegocioException, PersistenciaException {
        imprimirLista("OBSERVADORES DISPONÍVEIS", FACHADA.listarObservadores());
        String observadorId = lerTexto("ID do observador: ");
        imprimirLista("TELESCÓPIOS DISPONÍVEIS", FACHADA.listarTelescopios());
        String telescopioId = lerTexto("ID do telescópio: ");
        String alvoCeleste = lerTexto("Alvo celeste: ");
        LocalDateTime inicio = lerDataHora("Início (dd/MM/yyyy HH:mm): ");
        int duracao = lerInteiro("Duração em minutos: ");

        SessaoObservacao sessao = FACHADA.agendarSessao(
                observadorId, telescopioId, alvoCeleste, inicio, duracao);
        System.out.println("Sessão agendada com sucesso. ID: " + sessao.getId());
    }

    private static void concluirSessao()
            throws RegraNegocioException, PersistenciaException {
        imprimirLista("SESSÕES", FACHADA.listarSessoes());
        String sessaoId = lerTexto("ID da sessão: ");
        int qualidadeCeu = lerInteiro("Qualidade do céu (1 a 5): ");
        String anotacoes = lerTexto("Anotações e descobertas: ");
        FACHADA.concluirSessao(sessaoId, qualidadeCeu, anotacoes);
        System.out.println("Sessão concluída e diário de campo registrado.");
    }

    private static void cancelarSessao()
            throws RegraNegocioException, PersistenciaException {
        imprimirLista("SESSÕES", FACHADA.listarSessoes());
        String sessaoId = lerTexto("ID da sessão: ");
        FACHADA.cancelarSessao(sessaoId);
        System.out.println("Sessão cancelada com sucesso.");
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return ENTRADA.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException exception) {
                System.err.println("Informe um número inteiro válido.");
            }
        }
    }

    private static double lerDecimal(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(mensagem).replace(',', '.'));
            } catch (NumberFormatException exception) {
                System.err.println("Informe um número decimal válido.");
            }
        }
    }

    private static LocalDateTime lerDataHora(String mensagem) {
        while (true) {
            try {
                return LocalDateTime.parse(lerTexto(mensagem), FORMATO_DATA_HORA);
            } catch (DateTimeParseException exception) {
                System.err.println("Use uma data válida no formato dd/MM/yyyy HH:mm.");
            }
        }
    }

    private static <E extends Enum<E>> E lerEnum(String titulo, E[] opcoes) {
        System.out.println(titulo + ":");
        for (int indice = 0; indice < opcoes.length; indice++) {
            String rotulo = obterRotuloEnum(opcoes[indice]);
            System.out.println((indice + 1) + "  " + rotulo);
        }

        while (true) {
            int escolha = lerInteiro("Escolha: ");
            if (escolha >= 1 && escolha <= opcoes.length) {
                return opcoes[escolha - 1];
            }
            System.err.println("Escolha uma opção entre 1 e " + opcoes.length + ".");
        }
    }

    private static String obterRotuloEnum(Enum<?> opcao) {
        if (opcao instanceof NivelExperiencia nivel) {
            return nivel.getDescricao();
        }
        if (opcao instanceof TipoTelescopio tipo) {
            return tipo.getDescricao();
        }
        return opcao.name();
    }

    private static <T> void imprimirLista(String titulo, List<T> registros) {
        System.out.println("\n── " + titulo + " ──");
        if (registros.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        for (T registro : registros) {
            System.out.println("• " + registro);
        }
    }
}
