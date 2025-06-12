import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;


public class GerenciadorPokerApp {

    public static void main(String[] args) {
        exibirBoasVindas();

        try (Scanner scanner = new Scanner(System.in)) {
            // 1. Coletar configurações do jogo.
            ConfiguracoesJogo configs = coletarConfiguracoes(scanner);

            // 2. Criar a Mesa.
            System.out.print("Digite um nome para a mesa de jogo: ");
            String nomeDaMesa = scanner.nextLine();
            Mesa mesa = new Mesa(nomeDaMesa, configs, scanner);

            // 3. Adicionar jogadores à Mesa.
            adicionarJogadoresNaMesa(mesa, scanner, configs.fichasIniciaisPadrao());

            // 4. Iniciar o jogo.
            // A partir deste ponto, o controle é passado para o objeto 'mesa'.
            // O código ficará "preso" dentro do metodo iniciarJogo() até o fim da partida.
            mesa.iniciarJogo();

        } catch (Exception e) {
            // Captura qualquer erro inesperado que possa ocorrer durante a configuração.
            System.err.println("\nOcorreu um erro crítico e a aplicação será encerrada: " + e.getMessage());
        }

        System.out.println("\nObrigado por jogar! Até a próxima.");
    }

    private static void exibirBoasVindas() {
        System.out.println("==============================================");
        System.out.println(" Bem-vindo ao Gerenciador de Fichas de Poker!");
        System.out.println("==============================================\n");
    }


    private static ConfiguracoesJogo coletarConfiguracoes(Scanner scanner) {
        while (true) {
            try {
                System.out.println("--- Configuração Inicial do Jogo ---");
                System.out.print("Digite a quantidade de fichas iniciais (buy-in): ");
                int fichas = scanner.nextInt();

                System.out.print("Digite o número de mãos por nível de blinds: ");
                int maosPorNivel = scanner.nextInt();
                scanner.nextLine(); // Limpa o buffer

                List<NivelDeBlind> estruturaDeBlinds = new ArrayList<>();
                System.out.println("--- Defina a Estrutura de Blinds ---");
                System.out.println("(Digite 'fim' quando tiver adicionado todos os níveis)");

                int nivelNum = 1;
                while (true) {
                    System.out.print("Nível " + nivelNum + " - Small Blind (ou 'fim'): ");
                    String inputSb = scanner.nextLine();
                    if (inputSb.equalsIgnoreCase("fim")) {
                        if (estruturaDeBlinds.isEmpty()) {
                            System.err.println("Você deve definir pelo menos um nível de blinds.");
                            continue;
                        }
                        break;
                    }

                    System.out.print("Nível " + nivelNum + " - Big Blind: ");
                    String inputBb = scanner.nextLine();

                    int sb = Integer.parseInt(inputSb);
                    int bb = Integer.parseInt(inputBb);

                    if (bb <= sb) {
                        System.err.println("Erro: O Big Blind deve ser maior que o Small Blind. Tente este nível novamente.");
                        continue; // Pede o mesmo nível de novo
                    }

                    estruturaDeBlinds.add(new NivelDeBlind(sb, bb));
                    nivelNum++;
                }

                return new ConfiguracoesJogo(fichas, maosPorNivel, estruturaDeBlinds);

            } catch (InputMismatchException | NumberFormatException e) {
                System.err.println("Erro: Por favor, digite apenas números inteiros.");
                if(scanner.hasNextLine()) scanner.nextLine(); // Limpa o buffer
            } catch (IllegalArgumentException e) {
                System.err.println("Erro de configuração: " + e.getMessage());
                System.err.println("Por favor, tente novamente.\n");
            }
        }
    }

    private static void adicionarJogadoresNaMesa(Mesa mesa, Scanner scanner, int fichasIniciais) {
        System.out.println("\n--- Adicionar Jogadores ---");
        System.out.println("(Digite 'pronto' quando tiver adicionado todos)");
        while (true) {
            System.out.print("Nome do jogador: ");
            String nome = scanner.nextLine();

            if (nome.trim().equalsIgnoreCase("pronto")) {
                if (mesa.getJogadores().size() < 2) {
                    System.err.println("São necessários pelo menos 2 jogadores. Por favor, adicione mais.");
                    continue; // Pede para adicionar mais jogadores em vez de sair do loop
                }
                break; // Sai do loop se 'pronto' for digitado e houver jogadores suficientes
            }

            try {
                Jogador novoJogador = new Jogador(nome, fichasIniciais);
                mesa.adicionarJogador(novoJogador);
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao adicionar jogador: " + e.getMessage());
            }
        }
    }
}