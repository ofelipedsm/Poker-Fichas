import java.util.*;
import java.util.stream.Collectors;

public class Mao {

    // --- Atributos da Mão ---
    private final List<Jogador> jogadoresNaMao;
    private final Pote pote;
    // ALTERAÇÃO: Recebe o nível de blind específico para esta mão
    private final NivelDeBlind nivelDeBlindAtual;
    private final int indiceDealer;
    private final Scanner scanner;

    // --- Estado Temporário (resetado a cada mão) ---
    private final Set<Jogador> jogadoresDesistiram;
    private final Map<Jogador, Integer> apostasNaMaoTotal;

    // ALTERAÇÃO: O construtor agora espera um NivelDeBlind, não mais ConfiguracoesJogo
    public Mao(List<Jogador> jogadoresAtivos, Jogador dealer, NivelDeBlind nivelDeBlindAtual, Scanner scanner) {
        this.jogadoresNaMao = new ArrayList<>(jogadoresAtivos);
        this.nivelDeBlindAtual = nivelDeBlindAtual; // Armazena o nível atual
        this.scanner = scanner;
        this.pote = new Pote();
        this.jogadoresDesistiram = new HashSet<>();
        this.apostasNaMaoTotal = new HashMap<>();
        this.jogadoresNaMao.forEach(j -> this.apostasNaMaoTotal.put(j, 0));
        this.indiceDealer = encontrarIndiceLocal(dealer);
    }

    public void jogar() {
        if(this.indiceDealer == -1){
            System.err.println("ERRO: Dealer não foi encontrado na mão atual. Terminando a mão.");
            return;
        }

        exibirOverviewDaMao();

        System.out.println("\n===== INICIANDO NOVA MÃO =====");
        System.out.println("Blinds: " + nivelDeBlindAtual.sb() + "/" + nivelDeBlindAtual.bb());

        boolean blindsColetados = false;

        for (EstagioDaMao estagio : EstagioDaMao.values()) {
            if (getJogadoresAindaAptos().size() > 1) {
                System.out.println("\n--- ESTÁGIO: " + estagio + " ---");

                if (estagio == EstagioDaMao.PREFLOP && !blindsColetados) {
                    if (!coletarBlinds()) {
                        break;
                    }
                    blindsColetados = true;
                }
                realizarRodadaDeApostas(estagio);
            } else {
                break;
            }
        }
        distribuirPote();
        System.out.println("===== FIM DA MÃO =====");
    }

    private void exibirOverviewDaMao() {
        System.out.println("\n--- Visão Geral da Mão ---");

        Jogador dealer = jogadoresNaMao.get(this.indiceDealer);
        int indiceSB = (jogadoresNaMao.size() == 2) ? this.indiceDealer : getIndiceProximoJogadorAtivo(this.indiceDealer);
        Jogador smallBlind = jogadoresNaMao.get(indiceSB);
        int indiceBB = getIndiceProximoJogadorAtivo(indiceSB);
        Jogador bigBlind = jogadoresNaMao.get(indiceBB);

        System.out.println("Jogadores e Fichas:");
        for (Jogador jogador : this.jogadoresNaMao) {
            StringBuilder infoExtra = new StringBuilder();
            if (jogador.equals(dealer)) infoExtra.append(" (Dealer)");
            if (jogador.equals(smallBlind)) infoExtra.append(" (Small Blind)");
            if (jogador.equals(bigBlind)) infoExtra.append(" (Big Blind)");

            System.out.printf(" > %-15s: %-7d fichas %s%n", jogador.getNome(), jogador.getFichas(), infoExtra.toString());
        }
        System.out.println("----------------------------");
    }

    private boolean coletarBlinds() {
        System.out.println("--- Coletando Blinds ---");
        List<Jogador> jogadoresAtivos = getJogadoresAindaAptos();
        if (jogadoresAtivos.size() < 2) return false;

        int indiceSB = (jogadoresAtivos.size() == 2) ? this.indiceDealer : getIndiceProximoJogadorAtivo(this.indiceDealer);
        Jogador smallBlind = jogadoresNaMao.get(indiceSB);

        int indiceBB = getIndiceProximoJogadorAtivo(indiceSB);
        Jogador bigBlind = jogadoresNaMao.get(indiceBB);

        // ALTERAÇÃO: Usa os valores do nível de blind atual
        System.out.print(smallBlind.getNome() + " posta o Small Blind de " + nivelDeBlindAtual.sb() + "... ");
        fazerAposta(smallBlind, nivelDeBlindAtual.sb(), null);

        System.out.print(bigBlind.getNome() + " posta o Big Blind de " + nivelDeBlindAtual.bb() + "... ");
        fazerAposta(bigBlind, nivelDeBlindAtual.bb(), null);

        return true;
    }

    private void realizarRodadaDeApostas(EstagioDaMao estagio) {
        if (getJogadoresAindaAptos().size() <= 1) return;

        if (jogadoresNaMao.stream().filter(j -> !devePularJogador(j)).count() < 2) {
            System.out.println("Não há jogadores suficientes para uma rodada de apostas. Indo para o próximo estágio.");
            return;
        }

        Map<Jogador, Boolean> jaAgiuNestaRodada = new HashMap<>();
        jogadoresNaMao.forEach(j -> jaAgiuNestaRodada.put(j, false));

        Map<Jogador, Integer> apostasNestaRodada = new HashMap<>();
        jogadoresNaMao.forEach(j -> apostasNestaRodada.put(j, 0));

        int apostaParaPagar = 0;
        // ALTERAÇÃO: Usa o Big Blind do nível atual como referência de aumento
        int tamanhoMinimoAumento = nivelDeBlindAtual.bb();

        if (estagio == EstagioDaMao.PREFLOP) {
            // ALTERAÇÃO: A aposta a pagar no pré-flop é o Big Blind do nível atual
            apostaParaPagar = nivelDeBlindAtual.bb();
            for (Jogador j : jogadoresNaMao) {
                int apostaDoBlind = apostasNaMaoTotal.get(j);
                if (apostaDoBlind > 0) {
                    apostasNestaRodada.put(j, apostaDoBlind);
                }
            }
        }

        int indiceJogadorAtual = getIndicePrimeiroAJogar(estagio);
        boolean rodadaAtiva = true;

        while (rodadaAtiva) {
            Jogador jogador = jogadoresNaMao.get(indiceJogadorAtual);

            if (devePularJogador(jogador)) {
                long jogadoresQuePrecisamAgir = jogadoresNaMao.stream()
                        .filter(j -> !devePularJogador(j) && !jaAgiuNestaRodada.get(j))
                        .count();
                if (jogadoresQuePrecisamAgir <= 1) {
                    rodadaAtiva = false;
                }
                indiceJogadorAtual = getIndiceProximoJogadorAtivo(indiceJogadorAtual);
                continue;
            }

            AcaoJogador acao = processarAcaoDoJogador(jogador, apostaParaPagar, tamanhoMinimoAumento, apostasNestaRodada);
            jaAgiuNestaRodada.put(jogador, true);

            int apostaAtualDoJogador = apostasNestaRodada.get(jogador);
            if (acao == AcaoJogador.AUMENTAR || acao == AcaoJogador.APOSTAR) {
                tamanhoMinimoAumento = apostaAtualDoJogador - apostaParaPagar;
                apostaParaPagar = apostaAtualDoJogador;
                jaAgiuNestaRodada.replaceAll((j, v) -> devePularJogador(j));
                jaAgiuNestaRodada.put(jogador, true);
            }

            if (getJogadoresAindaAptos().size() <= 1) {
                rodadaAtiva = false;
                break;
            }

            indiceJogadorAtual = getIndiceProximoJogadorAtivo(indiceJogadorAtual);

            long jogadoresQuePrecisamAgir = jogadoresNaMao.stream()
                    .filter(j -> !devePularJogador(j) && !jaAgiuNestaRodada.get(j))
                    .count();

            final int apostaFinalDaRodada = apostaParaPagar;
            boolean todosIguais = jogadoresNaMao.stream()
                    .filter(j -> !devePularJogador(j))
                    .allMatch(j -> apostasNestaRodada.get(j) == apostaFinalDaRodada || j.getFichas() == 0);

            if (jogadoresQuePrecisamAgir == 0 && todosIguais) {
                rodadaAtiva = false;
            }
        }
    }

    private enum AcaoJogador { DESISTIR, PASSAR, PAGAR, APOSTAR, AUMENTAR }

    private AcaoJogador processarAcaoDoJogador(Jogador jogador, int apostaParaPagar, int tamanhoMinimoAumento, Map<Jogador, Integer> apostasNestaRodada) {
        System.out.println("\n---------------------------------");
        System.out.println("Ação para: " + jogador.getNome() + " (" + jogador.getFichas() + " fichas)");
        System.out.println("Pote atual: " + pote.getTotalFichas() + ". Apostas na rodada: " + apostasNestaRodada.values().stream().mapToInt(Integer::intValue).sum());

        int jaApostadoNestaRodada = apostasNestaRodada.getOrDefault(jogador, 0);
        int valorParaPagar = Math.min(jogador.getFichas() + jaApostadoNestaRodada, apostaParaPagar) - jaApostadoNestaRodada;

        System.out.println("Valor para pagar: " + valorParaPagar);
        System.out.print("Comando (desistir, passar, pagar, apostar <valor_do_aumento>): ");

        while(true) {
            try {
                String[] partes = scanner.nextLine().trim().toLowerCase().split(" ");
                String comando = partes[0];

                switch (comando) {
                    case "desistir":
                        jogadoresDesistiram.add(jogador);
                        System.out.println(jogador.getNome() + " desiste da mão.");
                        return AcaoJogador.DESISTIR;

                    case "passar":
                        if (apostaParaPagar > jaApostadoNestaRodada) throw new IllegalStateException("Não pode passar, existe uma aposta de " + apostaParaPagar + " para pagar.");
                        System.out.println(jogador.getNome() + " passa a vez.");
                        return AcaoJogador.PASSAR;

                    case "pagar":
                        if (valorParaPagar <= 0) {
                            System.out.println(jogador.getNome() + " já pagou o suficiente (passa).");
                            return AcaoJogador.PASSAR;
                        }
                        fazerAposta(jogador, valorParaPagar, apostasNestaRodada);
                        System.out.println(jogador.getNome() + " paga " + valorParaPagar + " fichas.");
                        return AcaoJogador.PAGAR;

                    case "apostar":
                        if (partes.length < 2) throw new IllegalArgumentException("Comando incompleto. Use: apostar <valor>");
                        int valorDoAumento = Integer.parseInt(partes[1]);

                        if (valorDoAumento < tamanhoMinimoAumento && jogador.getFichas() > (valorParaPagar + valorDoAumento)) {
                            throw new IllegalArgumentException("Aumento inválido. O aumento mínimo é de " + tamanhoMinimoAumento);
                        }

                        int apostaTotalNestaAcao = valorParaPagar + valorDoAumento;

                        AcaoJogador tipoDeAposta = (apostaParaPagar > 0) ? AcaoJogador.AUMENTAR : AcaoJogador.APOSTAR;
                        fazerAposta(jogador, apostaTotalNestaAcao, apostasNestaRodada);
                        System.out.println(jogador.getNome() + " " + (tipoDeAposta == AcaoJogador.AUMENTAR ? "aumenta em" : "aposta") + " " + valorDoAumento + " fichas.");
                        return tipoDeAposta;

                    default:
                        System.err.println("Comando inválido. Tente novamente.");
                        break;
                }
            } catch(Exception e) {
                System.err.println("Erro na jogada: " + e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private void fazerAposta(Jogador jogador, int valor, Map<Jogador, Integer> apostasNestaRodada) {
        if (valor <= 0) return;
        int valorReal = Math.min(valor, jogador.getFichas());

        jogador.removerFichas(valorReal);

        if (apostasNestaRodada != null) {
            apostasNestaRodada.put(jogador, apostasNestaRodada.getOrDefault(jogador, 0) + valorReal);
        }
        apostasNaMaoTotal.put(jogador, apostasNaMaoTotal.getOrDefault(jogador, 0) + valorReal);

        // Lógica de all-in refinada
        if (jogador.getFichas() == 0) {
            System.out.print(" (ALL-IN) ");
        }
    }

    private void calcularEConstruirPotes() {
        pote.limpar();

        Map<Jogador, Integer> apostasDosJogadoresAtivos = new HashMap<>();
        for (Jogador jogador : this.jogadoresNaMao) {
            if (!jogadoresDesistiram.contains(jogador)) {
                apostasDosJogadoresAtivos.put(jogador, this.apostasNaMaoTotal.get(jogador));
            }
        }

        if (apostasDosJogadoresAtivos.isEmpty()) {
            return;
        }

        List<Integer> niveisDeAposta = apostasDosJogadoresAtivos.values().stream()
                .distinct()
                .filter(aposta -> aposta > 0)
                .sorted()
                .toList();

        int nivelDeApostaAnterior = 0;

        for (int nivelAtual : niveisDeAposta) {
            int contribuicaoPorJogador = nivelAtual - nivelDeApostaAnterior;

            // Correção na lógica de cálculo do valor do subpote
            long totalDeContribuicoesNesteNivel = 0;
            Set<Jogador> jogadoresContribuintes = new HashSet<>();

            for(Map.Entry<Jogador, Integer> apostaEntry : apostasDosJogadoresAtivos.entrySet()){
                if(apostaEntry.getValue() >= nivelDeApostaAnterior){
                    jogadoresContribuintes.add(apostaEntry.getKey());
                    totalDeContribuicoesNesteNivel += Math.min(apostaEntry.getValue() - nivelDeApostaAnterior, contribuicaoPorJogador);
                }
            }

            Set<Jogador> jogadoresElegiveis = apostasDosJogadoresAtivos.keySet().stream()
                    .filter(jogador -> apostasDosJogadoresAtivos.get(jogador) >= nivelAtual)
                    .collect(Collectors.toSet());

            if (totalDeContribuicoesNesteNivel > 0) {
                Pote.SubPote subPote = new Pote.SubPote(jogadoresElegiveis);
                subPote.adicionarFichas((int)totalDeContribuicoesNesteNivel);
                this.pote.getSubPotes().add(subPote);
            }

            nivelDeApostaAnterior = nivelAtual;
        }
    }

    private void distribuirPote() {
        System.out.println("\n--- Distribuição do Pote ---");

        List<Jogador> jogadoresRestantes = jogadoresNaMao.stream()
                .filter(j -> !jogadoresDesistiram.contains(j))
                .collect(Collectors.toList());

        if (jogadoresRestantes.size() == 1) {
            Jogador vencedor = jogadoresRestantes.get(0);
            int premioTotal = apostasNaMaoTotal.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println(vencedor.getNome() + " vence, pois foi o único que sobrou!");
            System.out.println("Parabéns, " + vencedor.getNome() + "! Você ganhou o pote de " + premioTotal + " fichas.");
            vencedor.adicionarFichas(premioTotal);
            return;
        }

        if (jogadoresRestantes.size() > 1) {
            System.out.println("Showdown! Os jogadores restantes vão revelar as suas cartas.");

            calcularEConstruirPotes();

            List<Pote.SubPote> subPotes = pote.getSubPotes();
            int numeroDoPote = 1;

            for (Pote.SubPote subPote : subPotes) {
                if (subPote.getValor() == 0) continue;
                System.out.println("\n--- Resolvendo " + (subPotes.size() > 1 ? "Pote " + numeroDoPote : "Pote Principal") + " (Valor: " + subPote.getValor() + ") ---");

                List<Jogador> jogadoresElegiveisParaEstePote = jogadoresRestantes.stream()
                        .filter(subPote.getJogadoresElegiveis()::contains)
                        .toList();

                System.out.println("Jogadores elegíveis para este pote: " + jogadoresElegiveisParaEstePote.stream().map(Jogador::getNome).toList());

                List<Jogador> vencedoresDoPote = new ArrayList<>();
                while (vencedoresDoPote.isEmpty()) {
                    System.out.print("Digite o(s) nome(s) do(s) vencedor(es) (separados por vírgula, se houver empate): ");
                    String[] nomesDosVencedores = scanner.nextLine().trim().split(",");

                    List<Jogador> vencedoresEncontrados = new ArrayList<>();
                    boolean todosNomesValidos = true;
                    for (String nome : nomesDosVencedores) {
                        String nomeLimpo = nome.trim();
                        if(nomeLimpo.isEmpty()) continue;

                        Optional<Jogador> possivelVencedor = jogadoresElegiveisParaEstePote.stream()
                                .filter(j -> j.getNome().equalsIgnoreCase(nomeLimpo))
                                .findFirst();

                        if (possivelVencedor.isPresent()) {
                            if (!vencedoresEncontrados.contains(possivelVencedor.get())) {
                                vencedoresEncontrados.add(possivelVencedor.get());
                            }
                        } else {
                            System.err.println("Erro: Jogador '" + nomeLimpo + "' é inválido ou não é elegível para este pote.");
                            todosNomesValidos = false;
                            break;
                        }
                    }

                    if (todosNomesValidos && !vencedoresEncontrados.isEmpty()) {
                        vencedoresDoPote = vencedoresEncontrados;
                    } else {
                        if(todosNomesValidos){
                            System.err.println("Nenhum nome inserido.");
                        }
                        System.err.println("Por favor, tente novamente.");
                    }
                }

                int numeroDeVencedores = vencedoresDoPote.size();
                int premioBase = subPote.getValor() / numeroDeVencedores;
                int fichasDeResto = subPote.getValor() % numeroDeVencedores;

                System.out.println("O pote de " + subPote.getValor() + " será dividido entre " + numeroDeVencedores + " jogador(es).");

                for (int i = 0; i < vencedoresDoPote.size(); i++) {
                    Jogador vencedor = vencedoresDoPote.get(i);
                    int premioIndividual = premioBase;
                    if (i == 0 && fichasDeResto > 0) {
                        premioIndividual += fichasDeResto;
                    }
                    vencedor.adicionarFichas(premioIndividual);
                    System.out.println(" > Parabéns, " + vencedor.getNome() + "! Você ganhou " + premioIndividual + " fichas.");
                }
                numeroDoPote++;
            }
        }
    }

    private int getIndicePrimeiroAJogar(EstagioDaMao estagio) {
        if (estagio == EstagioDaMao.PREFLOP) {
            int indiceSB = (jogadoresNaMao.size() == 2) ? this.indiceDealer : getIndiceProximoJogadorAtivo(this.indiceDealer);
            int indiceBB = getIndiceProximoJogadorAtivo(indiceSB);
            return getIndiceProximoJogadorAtivo(indiceBB);
        } else {
            return getIndiceProximoJogadorAtivo(this.indiceDealer);
        }
    }

    private int getIndiceProximoJogadorAtivo(int indiceAtual) {
        int proximoIndice = (indiceAtual + 1) % jogadoresNaMao.size();
        while (proximoIndice != indiceAtual) {
            if (!devePularJogador(jogadoresNaMao.get(proximoIndice))) {
                return proximoIndice;
            }
            proximoIndice = (proximoIndice + 1) % jogadoresNaMao.size();
        }
        return indiceAtual;
    }

    private boolean devePularJogador(Jogador jogador) {
        return jogadoresDesistiram.contains(jogador) || jogador.getFichas() == 0;
    }

    private List<Jogador> getJogadoresAindaAptos() {
        return jogadoresNaMao.stream()
                .filter(j -> !jogadoresDesistiram.contains(j) && j.getFichas() > 0)
                .collect(Collectors.toList());
    }

    private int encontrarIndiceLocal(Jogador jogadorAlvo) {
        for (int i = 0; i < this.jogadoresNaMao.size(); i++) {
            if (this.jogadoresNaMao.get(i).equals(jogadorAlvo)) {
                return i;
            }
        }
        return -1;
    }
}