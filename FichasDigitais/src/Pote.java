import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Pote {

    //pote lateral em caso de all-in
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

    public Pote() {
        this.subPotes = new ArrayList<>();
    }

    public List<SubPote> getSubPotes() {
        return subPotes;
    }

    public int getTotalFichas() {
        return subPotes.stream().mapToInt(SubPote::getValor).sum();
    }

    public void limpar() {
        subPotes.clear();
    }
}