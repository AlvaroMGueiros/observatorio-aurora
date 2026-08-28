# Diagrama de classes UML

O diagrama abaixo usa Mermaid. Ele pode ser visualizado no GitHub, no GitLab ou em qualquer editor compatível com Mermaid.

```mermaid
classDiagram
    direction LR

    class Identificavel {
        <<interface>>
        +getId() String
    }

    class PessoaObservadora {
        <<abstract>>
        -String id
        -String nome
        -String email
        -LocalDate dataCadastro
        +getTipo()* String
        +getVinculo()* String
        +getId() String
        +setId(String)
        +getNome() String
        +setNome(String)
        +getEmail() String
        +setEmail(String)
        +getDataCadastro() LocalDate
        +setDataCadastro(LocalDate)
    }

    class AstronomoAmador {
        -NivelExperiencia nivelExperiencia
        -String clubeAstronomia
        +getNivelExperiencia() NivelExperiencia
        +setNivelExperiencia(NivelExperiencia)
        +getClubeAstronomia() String
        +setClubeAstronomia(String)
        +getTipo() String
        +getVinculo() String
    }

    class Pesquisador {
        -String instituicao
        -String areaPesquisa
        +getInstituicao() String
        +setInstituicao(String)
        +getAreaPesquisa() String
        +setAreaPesquisa(String)
        +getTipo() String
        +getVinculo() String
    }

    class Telescopio {
        -String id
        -String nome
        -TipoTelescopio tipo
        -double aberturaMilimetros
        -boolean ativo
        +getId() String
        +setId(String)
        +getNome() String
        +setNome(String)
        +getTipo() TipoTelescopio
        +setTipo(TipoTelescopio)
        +getAberturaMilimetros() double
        +setAberturaMilimetros(double)
        +isAtivo() boolean
        +setAtivo(boolean)
    }

    class SessaoObservacao {
        -String id
        -PessoaObservadora observador
        -Telescopio telescopio
        -String alvoCeleste
        -LocalDateTime inicio
        -int duracaoMinutos
        -StatusSessao status
        -Integer qualidadeCeu
        -String anotacoes
        +getId() String
        +setId(String)
        +getObservador() PessoaObservadora
        +setObservador(PessoaObservadora)
        +getTelescopio() Telescopio
        +setTelescopio(Telescopio)
        +getAlvoCeleste() String
        +setAlvoCeleste(String)
        +getInicio() LocalDateTime
        +setInicio(LocalDateTime)
        +getDuracaoMinutos() int
        +setDuracaoMinutos(int)
        +getStatus() StatusSessao
        +setStatus(StatusSessao)
        +getQualidadeCeu() Integer
        +setQualidadeCeu(Integer)
        +getAnotacoes() String
        +setAnotacoes(String)
        +getFim() LocalDateTime
    }

    class NivelExperiencia {
        <<enumeration>>
        INICIANTE
        INTERMEDIARIO
        AVANCADO
        -String descricao
        +getDescricao() String
    }

    class TipoTelescopio {
        <<enumeration>>
        REFLETOR
        REFRATOR
        CATADIOPTRICO
        RADIO
        -String descricao
        +getDescricao() String
    }

    class StatusSessao {
        <<enumeration>>
        AGENDADA
        CONCLUIDA
        CANCELADA
        -String descricao
        +getDescricao() String
    }

    class Repositorio~T~ {
        <<interface>>
        +inserir(T)
        +atualizar(T)
        +buscarPorId(String) T
        +listar() List~T~
    }

    class RepositorioArquivo~T~ {
        -Path arquivo
        -Class~T~ tipoEntidade
        +inserir(T)
        +atualizar(T)
        +buscarPorId(String) T
        +listar() List~T~
    }

    class CadastroObservador {
        -Repositorio repositorio
        +cadastrar(PessoaObservadora) PessoaObservadora
        +buscarPorId(String) PessoaObservadora
        +listar() List~PessoaObservadora~
    }

    class CadastroTelescopio {
        -Repositorio repositorio
        +cadastrar(Telescopio) Telescopio
        +buscarPorId(String) Telescopio
        +listar() List~Telescopio~
    }

    class GestaoSessao {
        -Repositorio repositorio
        -CadastroObservador cadastroObservador
        -CadastroTelescopio cadastroTelescopio
        +agendar(...) SessaoObservacao
        +concluir(...) SessaoObservacao
        +cancelar(String) SessaoObservacao
        +listar() List~SessaoObservacao~
    }

    class OperacoesObservatorio {
        <<interface>>
        +cadastrarObservador(PessoaObservadora) PessoaObservadora
        +cadastrarTelescopio(Telescopio) Telescopio
        +agendarSessao(...) SessaoObservacao
        +concluirSessao(...) SessaoObservacao
        +cancelarSessao(String) SessaoObservacao
        +listarObservadores() List
        +listarTelescopios() List
        +listarSessoes() List
    }

    class FachadaObservatorio
    class TelaPrincipal
    class Validador {
        -Pattern EMAIL
        +textoObrigatorio(String, String)$ String
        +email(String)$ String
    }
    class RegraNegocioException
    class PersistenciaException

    Identificavel <|.. PessoaObservadora
    Identificavel <|.. Telescopio
    Identificavel <|.. SessaoObservacao
    PessoaObservadora <|-- AstronomoAmador
    PessoaObservadora <|-- Pesquisador
    AstronomoAmador --> NivelExperiencia
    Telescopio --> TipoTelescopio
    SessaoObservacao --> StatusSessao
    SessaoObservacao --> PessoaObservadora
    SessaoObservacao --> Telescopio
    Repositorio <|.. RepositorioArquivo
    CadastroObservador --> Repositorio
    CadastroObservador ..> Validador
    CadastroTelescopio --> Repositorio
    CadastroTelescopio ..> Validador
    GestaoSessao --> Repositorio
    GestaoSessao ..> Validador
    GestaoSessao --> CadastroObservador
    GestaoSessao --> CadastroTelescopio
    OperacoesObservatorio <|.. FachadaObservatorio
    FachadaObservatorio --> CadastroObservador
    FachadaObservatorio --> CadastroTelescopio
    FachadaObservatorio --> GestaoSessao
    TelaPrincipal --> OperacoesObservatorio
    CadastroObservador ..> RegraNegocioException
    CadastroTelescopio ..> RegraNegocioException
    GestaoSessao ..> RegraNegocioException
    RepositorioArquivo ..> PersistenciaException
```

## Fluxo entre camadas

```text
TelaPrincipal
      │ usa o contrato
      ▼
OperacoesObservatorio ← FachadaObservatorio
                             │ coordena
                             ▼
                 Cadastros / GestaoSessao
                             │ usa o contrato
                             ▼
                  Repositorio<T> ← RepositorioArquivo<T>
                                           │
                                           ▼
                                    arquivos .ser
```
