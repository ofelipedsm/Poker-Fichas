import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Random;

public class Mesa {

    private final String nomeDaSala;
    private final List<Jogador> jogadores;
    private final ConfiguracoesJogo configuracoes;
    private final Scanner scanner;
    private int indiceDealer;
    private boolean jogoEmAndamento;

    // --- NOVOS ATRIBUTOS ---
    private int numeroDaMaoAtual;
    private NivelDeBlind nivelDeBlindAtual;
    // -----------------------


    public Mesa(String nomeDaSala, ConfiguracoesJogo configuracoes, Scanner scanner) {
        if (nomeDaSala == null || nomeDaSala.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da sala não pode ser nulo ou vazio.");
        }
        if (configuracoes == null) {
            throw new IllegalArgumentException("As configurações do jogo não podem ser nulas.");
        }
        if (scanner == null) {
            throw new IllegalArgumentException("O Scanner não pode ser nulo.");
        }
        this.nomeDaSala = nomeDaSala;
        this.configuracoes = configuracoes;
        this.scanner = scanner;
        this.jogadores = new ArrayList<>();
        this.indiceDealer = -1;
        this.jogoEmAndamento = false;

        // --- INICIALIZAÇÃO DOS NOVOS ATRIBUTOS ---
        this.numeroDaMaoAtual = 0;
        // Começamos com o primeiro nível de blind definido nas configurações
        this.nivelDeBlindAtual = configuracoes.estruturaDeBlinds().get(0);
        // ----------------------------------------
    }

    public void adicionarJogador(Jogador jogador) {
        if (jogoEmAndamento) {
            throw new IllegalStateException("Não é possível adicionar jogadores enquanto o jogo está em andamento.");
        }
        if (this.jogadores.contains(jogador)) {
            throw new IllegalArgumentException("O jogador '" + jogador.getNome() + "' já está na mesa.");
        }
        this.jogadores.add(jogador);
        System.out.println("Jogador '" + jogador.getNome() + "' adicionado à mesa.");
    }

    public void iniciarJogo() {
        if (jogadores.size() < 2) {
            throw new IllegalStateException("São necessários pelo menos 2 jogadores para iniciar o jogo.");
        }
        this.jogoEmAndamento = true;
        this.indiceDealer = -1;

        System.out.println("\n=================================================");
        System.out.println("O Jogo na mesa '" + nomeDaSala + "' começou!");
        System.out.println("=================================================");

        while (jogoEmAndamento) {
            jogarProximaMao();

            List<Jogador> jogadoresComFichas = getJogadoresComFichas();
            if (jogadoresComFichas.size() < 2) {
                jogoEmAndamento = false;
                System.out.println("\n--- FIM DE JOGO ---");
                if (!jogadoresComFichas.isEmpty()) {
                    System.out.println("O grande vencedor é: " + jogadoresComFichas.get(0).getNome() + "!");
                } else {
                    System.out.println("Não há mais jogadores com fichas.");
                }
            } else if (jogoEmAndamento) {
                System.out.print("\nDeseja iniciar a próxima mão? (s/n): ");
                String comando = scanner.nextLine().trim().toLowerCase();
                if (!comando.equals("s")) {
                    jogoEmAndamento = false;
                }
            }
        }
        System.out.println("\n--- Jogo na mesa '" + nomeDaSala + "' terminado. ---");
    }

    public List<Jogador> getJogadores() {
        return Collections.unmodifiableList(jogadores);
    }


    // --- MÉTODO ATUALIZADO ---
    private void jogarProximaMao() {
        this.numeroDaMaoAtual++; // 1. Incrementa o contador de mãos
        atualizarNivelDeBlind();   // 2. Verifica se o nível de blind deve subir

        moverBotaoDealer();
        List<Jogador> jogadoresNaMao = getJogadoresComFichas();

        Jogador dealer = this.jogadores.get(this.indiceDealer);

        // 3. Passa o nível de blind ATUAL para a Mao, em vez das configurações inteiras
        Mao maoAtual = new Mao(jogadoresNaMao, dealer, this.nivelDeBlindAtual, this.scanner);

        maoAtual.jogar();
        exibirFichasDeTodos();
    }
    // -------------------------

    // --- NOVO MÉTODO ---
    /**
     * Verifica se o nível de blind precisa ser atualizado com base no número
     * de mãos jogadas e, se necessário, atualiza e anuncia a mudança.
     */
    private void atualizarNivelDeBlind() {
        int maosPorNivel = this.configuracoes.maosPorNivel();
        List<NivelDeBlind> estrutura = this.configuracoes.estruturaDeBlinds();

        // Evita divisão por zero e não faz nada se maosPorNivel for inválido
        if (maosPorNivel <= 0) return;

        // Calcula qual deveria ser o índice do nível atual
        // Usa Math.min para garantir que não ultrapasse o último nível definido
        int indiceDoNivel = Math.min((this.numeroDaMaoAtual - 1) / maosPorNivel, estrutura.size() - 1);

        NivelDeBlind novoNivel = estrutura.get(indiceDoNivel);

        // Se o nível calculado for diferente do nível atual, faz a atualização
        if (!this.nivelDeBlindAtual.equals(novoNivel)) {
            this.nivelDeBlindAtual = novoNivel;
            System.out.println("\n******************************");
            System.out.println("*** OS BLINDS SUBIRAM! ***");
            System.out.println("Novo Nível: Small Blind " + novoNivel.sb() + " / Big Blind " + novoNivel.bb());
            System.out.println("******************************");
        }
    }
    // -------------------

    private void moverBotaoDealer() {
        if (jogadores.isEmpty()) return;

        // Se for a primeira mão, o dealer é escolhido aleatoriamente
        if (this.indiceDealer == -1) {
            this.indiceDealer = new Random().nextInt(jogadores.size());
            // Garante que o primeiro dealer escolhido tenha fichas
            while (jogadores.get(this.indiceDealer).getFichas() == 0) {
                this.indiceDealer = (this.indiceDealer + 1) % jogadores.size();
            }
            return;
        }

        int proximoIndice = this.indiceDealer;
        do {
            proximoIndice = (proximoIndice + 1) % jogadores.size();
        } while (jogadores.get(proximoIndice).getFichas() == 0);

        this.indiceDealer = proximoIndice;
    }

    private List<Jogador> getJogadoresComFichas() {
        return jogadores.stream()
                .filter(jogador -> jogador.getFichas() > 0)
                .collect(Collectors.toList());
    }

    private void exibirFichasDeTodos() {
        System.out.println("\n--- Saldo de Fichas Atual ---");
        for (Jogador jogador : this.jogadores) {
            System.out.println(jogador.getNome() + ": " + jogador.getFichas() + " fichas");
        }
        System.out.println("-----------------------------");
    }
}