import java.util.Arrays;

public class Ordenadora extends Thread {
    private final byte[] pedaco;
    private byte[] resultado;

    public Ordenadora(byte[] pedaco) {
        if (pedaco == null)
            throw new IllegalArgumentException("Pedaço não pode ser nulo");
        // A main entrega um pedaco exclusivo e nao o modifica apos o start.
        this.pedaco = pedaco;
    }

    @Override
    public void run() {
        this.resultado = mergeSort(this.pedaco);
    }

    // Chamar apos join(). O resultado sera lido, nao alterado, pelas misturadoras.
    public byte[] getResultado() {
        if (getState() != Thread.State.TERMINATED || this.resultado == null)
            throw new IllegalStateException("Resultado ainda não disponível; a tarefa pode ter falhado");
        return this.resultado;
    }

    private byte[] mergeSort(byte[] vetor) {
        if (vetor.length <= 1)
            return vetor;

        int meio = vetor.length / 2;
        byte[] esquerda = Arrays.copyOfRange(vetor, 0, meio);
        byte[] direita = Arrays.copyOfRange(vetor, meio, vetor.length);

        esquerda = mergeSort(esquerda);
        direita = mergeSort(direita);
        // Reutiliza o algoritmo do grupo; esta chamada nao cria outra thread.
        return Misturadora.merge(esquerda, direita);
    }
}
