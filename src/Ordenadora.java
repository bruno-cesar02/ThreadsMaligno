import java.util.Arrays;

public class Ordenadora extends Thread
{
    private final byte[] pedaco;
    private byte[] resultado;

    public Ordenadora(byte[] pedaco)
    {
        // Verifica o parâmetro recebido, antes de preencher o atributo.
        if (pedaco == null)
            throw new IllegalArgumentException("Pedaço não pode ser nulo");

        this.pedaco = Arrays.copyOf(pedaco, pedaco.length);
    }

    @Override
    public void run()
    {
        this.resultado = mergeSort(this.pedaco);
    }

    // A main deve chamar depois de join().
    public byte[] getResultado()
    {
        if (getState() != Thread.State.TERMINATED || this.resultado == null)
            throw new IllegalStateException("Resultado ainda não disponível");

        return Arrays.copyOf(this.resultado, this.resultado.length);
    }

    private byte[] mergeSort(byte[] vetor)
    {
        // Caso-base: zero ou um elemento já está ordenado.
        if (vetor.length <= 1)
            return Arrays.copyOf(vetor, vetor.length);

        int meio = vetor.length / 2;

        // O início é incluído; o fim é excluído.
        byte[] esquerda = Arrays.copyOfRange(vetor, 0, meio);
        byte[] direita = Arrays.copyOfRange(vetor, meio, vetor.length);

        // Cada chamada ordena sua metade.
        esquerda = mergeSort(esquerda);
        direita = mergeSort(direita);

        return intercalar(esquerda, direita);
    }

    private byte[] intercalar(byte[] esquerda, byte[] direita)
    {
        byte[] unido = new byte[esquerda.length + direita.length];

        int i = 0; // Posição na esquerda.
        int j = 0; // Posição na direita.
        int k = 0; // Posição no resultado.

        while (i < esquerda.length && j < direita.length)
        {
            if (esquerda[i] <= direita[j])
            {
                unido[k] = esquerda[i];
                i++;
            }
            else
            {
                unido[k] = direita[j];
                j++;
            }

            k++;
        }

        // Copia o que restou da esquerda.
        while (i < esquerda.length)
        {
            unido[k] = esquerda[i];
            i++;
            k++;
        }

        // Copia o que restou da direita.
        while (j < direita.length)
        {
            unido[k] = direita[j];
            j++;
            k++;
        }

        return unido;
    }
}