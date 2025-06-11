import java.util.Objects;

public class Jogador {

    // --- ATRIBUTOS ---

    private final String nome;
    private int fichas;


    // --- CONSTRUTOR ---

    public Jogador(String nome, int fichasIniciais) {
        // Validação dos parâmetros de entrada
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador não pode ser nulo ou vazio.");
        }
        if (fichasIniciais < 0) {
            throw new IllegalArgumentException("A quantidade de fichas iniciais não pode ser negativa.");
        }

        this.nome = nome.trim();
        this.fichas = fichasIniciais;
    }


    // --- MÉTODOS DE ACESSO (GETTERS) ---

    public String getNome() {
        return nome;
    }

    public int getFichas() {
        return fichas;
    }


    // --- MÉTODOS DE COMPORTAMENTO ---

    public void adicionarFichas(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de fichas para adicionar deve ser positiva.");
        }
        this.fichas += quantidade;
    }

    public void removerFichas(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade de fichas para remover deve ser positiva.");
        }
        if (this.fichas < quantidade) {
            throw new IllegalArgumentException("O jogador '" + this.nome + "' não tem fichas suficientes para remover " + quantidade + ". Fichas atuais: " + this.fichas);
        }
        this.fichas -= quantidade;
    }


    // --- MÉTODOS UTILITÁRIOS (SOBRESCRITAS DE OBJECT) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Otimização: se for a mesma instância, é igual.
        if (o == null || getClass() != o.getClass()) return false; // Se for nulo ou de outra classe, é diferente.
        Jogador jogador = (Jogador) o;
        return nome.equals(jogador.nome); // A igualdade é definida pelo nome.
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome); // Usa a classe utilitária Objects para gerar um hash a partir do nome.
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "nome='" + nome + '\'' +
                ", fichas=" + fichas +
                '}';
    }
}