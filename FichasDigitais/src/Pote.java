import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa o pote de fichas, agora com capacidade para gerenciar
 * múltiplos potes laterais (side pots).
 *
 * <p><b>Design Alterado:</b> Em vez de um único total, a classe agora gerencia
 * uma lista de 'SubPote', onde cada um tem um valor e um conjunto de jogadores
 * elegíveis para ganhá-lo.</p>
 */
public class Pote {

    /**
     * Uma classe interna para representar um único pote (principal ou lateral).
     * Usamos 'protected' para que a classe Mao possa aceder-lhe se necessário.
     */
    protected static class SubPote {
        private int valor;
        private final Set<Jogador> jogadoresElegiveis;

        public SubPote(Set<Jogador> jogadoresElegiveis) {
            this.valor = 0;
            this.jogadoresElegiveis = jogadoresElegiveis;
        }

        public int getValor() {
            return valor;
        }

        public Set<Jogador> getJogadoresElegiveis() {
            return jogadoresElegiveis;
        }

        public void adicionarFichas(int quantidade) {
            if (quantidade > 0) {
                this.valor += quantidade;
            }
        }

        // Útil para depuração no futuro
        @Override
        public String toString() {
            // Converte a lista de jogadores elegíveis para uma lista de nomes para facilitar a leitura
            String nomes = jogadoresElegiveis.stream()
                    .map(Jogador::getNome)
                    .collect(Collectors.joining(", "));

            return "SubPote{" + "valor=" + valor + ", elegíveis=[" + nomes + "]}";
        }
    }

    // O Pote principal agora é uma lista de SubPotes.
    private final List<SubPote> subPotes;

    /**
     * Constrói um novo Pote, iniciando com uma lista vazia de potes.
     */
    public Pote() {
        this.subPotes = new ArrayList<>();
    }

    /**
     * Retorna a lista de todos os sub-potes (principal e laterais).
     * @return A lista de SubPote.
     */
    public List<SubPote> getSubPotes() {
        return subPotes;
    }

    /**
     * Calcula e retorna o valor total de fichas em todos os sub-potes.
     * @return O total de fichas na mesa.
     */
    public int getTotalFichas() {
        return subPotes.stream().mapToInt(SubPote::getValor).sum();
    }

    /**
     * Limpa todos os sub-potes, preparando o Pote para uma nova mão.
     */
    public void limpar() {
        subPotes.clear();
    }
}