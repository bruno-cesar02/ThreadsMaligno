

public class MaiorVetorAproximado {
    public static void main(String[] args) {
        System.out.println("Estimando o maior tamanho possivel de vetor em Java...");
        System.out.println("Este teste aloca um byte[]; a ordenacao precisa de memoria adicional.");
        long inicio = System.currentTimeMillis();
        int tamanho = 1_000_000;
        int ultimoBemSucedido = 0;

        while (true) {
            try {
                byte[] vetor = new byte[tamanho];
                ultimoBemSucedido = vetor.length;
                System.out.printf("Alocado com sucesso: %,d elementos%n", ultimoBemSucedido);
                vetor = null;
                System.gc(); // Solicita coleta; nao garante liberacao imediata.

                // Aumenta aproximadamente 50%, sem ultrapassar o limite do int.
                if (tamanho > Integer.MAX_VALUE / 3 * 2)
                    break;
                tamanho /= 2;
                tamanho *= 3;
            } catch (OutOfMemoryError erro) {
                System.out.printf("Falhou em %,d elementos%n", tamanho);
                break;
            }
        }

        long fim = System.currentTimeMillis();
        System.out.printf("%nMaior vetor testado que coube: %,d elementos%n", ultimoBemSucedido);
        System.out.printf("Memoria dos elementos: %.2f MB%n", ultimoBemSucedido * 1.0 / (1024 * 1024));
        System.out.printf("Tempo do experimento: %.2f segundos%n", (fim - inicio) / 1000.0);
        System.out.println("Nao use este valor como garantia de tamanho seguro para o Merge Sort.");
    }
}
