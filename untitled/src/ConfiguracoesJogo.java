import java.util.List;

public record ConfiguracoesJogo(
        int fichasIniciaisPadrao,
        int maosPorNivel, // De quantas em quantas mãos os blinds sobem
        List<NivelDeBlind> estruturaDeBlinds // A lista de níveis
) {
    public ConfiguracoesJogo {
        if (fichasIniciaisPadrao <= 0) {
            throw new IllegalArgumentException("A quantidade de fichas iniciais deve ser positiva.");
        }
        if (maosPorNivel <= 0) {
            throw new IllegalArgumentException("O número de mãos por nível deve ser positivo.");
        }
        if (estruturaDeBlinds == null || estruturaDeBlinds.isEmpty()) {
            throw new IllegalArgumentException("A estrutura de blinds não pode ser nula ou vazia.");
        }
    }
}