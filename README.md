# Gerenciador de Mesa de Poker

*Um projeto Java para gerenciar fichas, apostas e potes em jogos de poker presenciais. Esta aplicação de console representa a fundação e a prova de conceito para uma futura aplicação web completa.*

---

## O Projeto (Metodologia STAR)


### Situação (O Problema)

A inspiração para este projeto nasceu de um cenário muito comum e familiar: aqueles momentos em uma viagem ou num simples encontro, quando a vontade espontânea de jogar uma partida de poker com amigos surge, mas a falta de planeamento fala mais alto.

Invariavelmente, o grupo se deparava com o mesmo obstáculo: **"E as fichas?"**.

A ausência de um set de poker físico transformava o que deveria ser um momento de lazer e confraternização numa confusão de anotações em guardanapos e cálculos manuais, quebrando o ritmo, a imersão e a diversão do jogo. O desafio era claro: como manter a essência social e presencial do poker, com cartas reais e interações verdadeiras, mas com uma solução digital e portátil para a gestão financeira do jogo?

### Tarefa (A Missão)

A missão que guiou o desenvolvimento deste projeto foi clara e direta:

> **Desenvolver uma aplicação que mantivesse a essência social e divertida do poker presencial, mas que eliminasse a necessidade de fichas físicas e planejamento prévio, tornando uma partida de poker instantaneamente acessível em qualquer lugar.**

O aplicativo precisava ser um "dealer digital" para a parte financeira do jogo, automatizando todas as regras de apostas para que os jogadores pudessem usar um baralho comum sem qualquer outra preocupação.

### Ação (A Solução Técnica)

Para cumprir esta missão, esta primeira versão da aplicação foi desenvolvida do zero em **Java**, seguindo princípios de código limpo e responsabilidade única para garantir uma base sólida para futuras evoluções. As principais ações e tecnologias envolvidas foram:

* **Linguagem e Plataforma:** Java 24, utilizando uma estrutura de Orientação a Objetos robusta.
* **Lógica de Jogo Implementada:**
    * **Gestão de Jogadores e Fichas:** Controlo preciso do stack de cada jogador.
    * **Ciclo de Apostas Completo:** Lógica para as rodadas Pré-Flop, Flop, Turn e River, respeitando a ordem de ação.
    * **Cálculo de Potes Laterais (Side Pots):** Algoritmo implementado para calcular e dividir os potes corretamente em cenários de all-in com múltiplos jogadores.
    * **Gestão de Empates (Split Pots):** Funcionalidade para dividir qualquer pote (principal ou lateral) entre múltiplos vencedores.
    * **Blinds Automáticos:** Sistema de níveis que aumenta os blinds automaticamente com base no número de mãos jogadas.
* **Interface:** Aplicação de console interativa, com comandos claros e feedback em tempo real para o utilizador.
* **Desenvolvimento Guiado:** O projeto foi construído de forma incremental, com uma base sólida e funcionalidades complexas sendo adicionadas em fases, com extensa depuração e testes de cenário.

### Resultado (A Aplicação Final)

O resultado desta fase inicial é uma aplicação de console 100% funcional e robusta que atua como um verdadeiro "Croupier Digital", permitindo que uma partida de poker comece a qualquer hora, em qualquer lugar.

* **Funcionalidades Chave:**
    1.  **Setup de Jogo Configurável:** O utilizador define o buy-in inicial, o número de mãos por nível e a estrutura completa de blinds.
    2.  **Gestão Automática de Potes:** O programa calcula o pote principal e todos os potes laterais necessários de forma automática e transparente.
    3.  **Distribuição Flexível de Prémios:** O utilizador declara o(s) vencedor(es) de cada pote, e o sistema divide e entrega as fichas corretamente, mesmo em caso de empate.
    4.  **Ritmo de Jogo Profissional:** Com os blinds a aumentar automaticamente, o jogo ganha uma dinâmica de torneio.
* **Impacto Real:**
    O impacto real desta ferramenta é a **liberdade**. A aplicação torna o poker mais acessível e funcional. Qualquer encontro casual — seja um churrasco, um dia na praia ou uma festa — pode instantaneamente se tornar uma mesa de poker, precisando apenas de um baralho e da vontade de jogar.

---

## Como Executar o Projeto

1.  Clone o repositório:
    ```bash
    git clone https://github.com/ofelipedsm/Poker-Fichas.git
    ```
2.  Navegue até a pasta do projeto.
3.  Compile o código. (Se estiver a usar um IDE como IntelliJ ou Eclipse, basta carregar o projeto e executar a classe `GerenciadorPokerApp.java`).
4.  Execute a aplicação e siga as instruções no console.

## Evolução e Futuro do Projeto

Esta aplicação de console é a primeira abordagem para a solução do problema e serve como uma robusta prova de conceito para a lógica do jogo. O código foi estruturado com princípios de Orientação a Objetos para facilitar a sua migração e evolução.

O roteiro futuro planeado é a transformação deste projeto numa **aplicação web completa, mais eficiente e escalável** e porteriormente em um aplicativo.
