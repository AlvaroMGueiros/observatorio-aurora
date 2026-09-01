# Observatório Aurora

Sistema de console para organizar uma comunidade de observação astronômica. O projeto permite cadastrar diferentes perfis de observadores, catalogar telescópios, reservar horários sem conflitos e manter um diário das sessões realizadas.

## Equipe

- Álvaro Miguel Gueiros

## Tema e escopo

O Observatório Aurora atende um pequeno observatório comunitário que recebe astrônomos amadores e pesquisadores. O sistema cuida do ciclo operacional da observação: cadastro dos participantes e equipamentos, agendamento, prevenção de reservas simultâneas, cancelamento e registro do resultado da noite.

O escopo não inclui autenticação, pagamentos, controle de estoque, interface gráfica ou integração com serviços meteorológicos. Esses itens ficaram de fora para manter o projeto concentrado nos requisitos da disciplina.

## Funcionalidades e requisitos

- Cadastrar astrônomos amadores, informando experiência e clube opcional.
- Cadastrar pesquisadores, informando instituição e área de pesquisa.
- Impedir e-mails duplicados e validar os dados de cada perfil.
- Cadastrar telescópios de diferentes tipos e aberturas.
- Agendar sessões futuras com duração entre 15 e 480 minutos.
- Impedir conflito de horário para o mesmo observador ou telescópio.
- Cancelar somente sessões ainda agendadas.
- Concluir uma sessão com avaliação do céu e anotações de campo.
- Impedir a conclusão de sessões futuras e o cancelamento de sessões já iniciadas.
- Listar observadores, telescópios e sessões em ordem útil.
- Persistir todos os registros em arquivos `.ser` por serialização de objetos.
- Informar erros específicos por meio de exceções de negócio e persistência.

## Como os requisitos da disciplina foram atendidos

| Requisito | Implementação |
|---|---|
| Persistência em arquivo | `RepositorioArquivo<T>` serializa listas em `dados/*.ser` e grava primeiro em arquivo temporário. |
| Ocultação da informação | Os atributos são privados e possuem métodos de acesso. |
| Arquitetura em camadas | Pacotes `ui`, `fachada`, `negocio`, `dados`, `modelo` e `excecao`. |
| Padrão Fachada | `FachadaObservatorio` é o único ponto de entrada da interface para as regras do sistema. |
| Exceções | `RegraNegocioException` e `PersistenciaException` tratam desvios do fluxo normal. |
| Herança | `AstronomoAmador` e `Pesquisador` herdam de `PessoaObservadora`. |
| Polimorfismo | A listagem trabalha com `PessoaObservadora` e chama `getTipo()` e `getVinculo()` conforme o subtipo real. |
| Interfaces entre camadas | A UI depende de `OperacoesObservatorio`; o negócio depende de `Repositorio<T>`. |

## Estrutura

```text
observatorio-aurora/
├── docs/
│   └── diagrama-classes.md
├── src/main/java/br/edu/ufape/aurora/
│   ├── dados/       interfaces e persistência serializada
│   ├── excecao/     exceções específicas
│   ├── fachada/     contrato e implementação da fachada
│   ├── modelo/      entidades, herança e enums
│   ├── negocio/     validações e regras de negócio
│   └── ui/          menu de console
├── src/test/java/br/edu/ufape/aurora/
│   └── testes automáticos de integração e encapsulamento
└── pom.xml
```

O diretório `dados/` é criado automaticamente no primeiro cadastro. Cada tipo de agregado é armazenado em seu próprio arquivo.

## Executar

Pré-requisitos: JDK 17 ou mais recente e Maven 3.9 ou mais recente.

```bash
mvn package
java -jar target/observatorio-aurora-1.0.0.jar
```

Para executar somente os testes automáticos:

```bash
mvn test
```

Também é possível compilar sem Maven:

```bash
javac -encoding UTF-8 -d bin src/main/java/br/edu/ufape/aurora/*/*.java
java -cp bin br.edu.ufape.aurora.ui.TelaPrincipal
```

## Decisões de projeto

- As entidades relacionadas são armazenadas dentro da sessão, preservando um retrato dos nomes e dados usados no momento do agendamento.
- O repositório genérico evita duplicação entre persistências de observadores, telescópios e sessões.
- A troca do arquivo é atômica quando suportada pelo sistema operacional, reduzindo o risco de corromper o arquivo principal durante uma gravação.
- Regras de negócio não leem o console e a UI não acessa arquivos diretamente, o que mantém as responsabilidades separadas.

O diagrama UML completo está em [docs/diagrama-classes.md](docs/diagrama-classes.md).
