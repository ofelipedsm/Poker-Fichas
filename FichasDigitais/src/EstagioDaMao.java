public enum EstagioDaMao {

    //A primeira rodada de apostas, antes de qualquer carta comunitária ser revelada.
    PREFLOP("Pré-Flop"),

    //O estágio em que as três primeiras cartas comunitárias são reveladas.
    FLOP("Flop"),

    //O estágio em que a quarta carta comunitária (o "Turn") é revelada.
    TURN("Turn"),

    //O estágio em que a quinta e última carta comunitária (o "River") é revelada.
    RIVER("River");



    //Um nome mais amigável para exibição na interface do usuário.
    private final String nomeAmigavel;

    /**
     * Construtor privado para o enum.
     * É chamado uma vez para cada constante definida acima.
     * @param nomeAmigavel O nome a ser exibido para o estágio.
     */
    EstagioDaMao(String nomeAmigavel) {
        this.nomeAmigavel = nomeAmigavel;
    }

    public String getNomeAmigavel() {
        return nomeAmigavel;
    }

    @Override
    public String toString() {
        return this.nomeAmigavel;
    }
}