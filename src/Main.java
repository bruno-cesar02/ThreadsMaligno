import java.util.Random;
import java.io.EOFException;

public class Main {
    public static void printarParteDoVetor(int inicio, int fim, byte[] vetor) throws Exception {
        if (inicio < 1 || fim < inicio || fim > vetor.length) {
            throw new Exception("Anta, o segundo tem que ser maior que o primeiro");
        }
        System.out.print("[");
        for (int i = inicio - 1; i < fim; i++) {
            System.out.print(vetor[i]);
            if (i < fim - 1)
                System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        try {
            // Repetir tamanho e semente permite gerar os mesmos dados no sequencial.
            long semente = 42;
            if (args.length > 0)
                semente = Long.parseLong(args[0]);
            executar(semente);
        } catch (EOFException e) {
            System.out.println("Entrada encerrada. Programa finalizado.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Ordenação interrompida.");
        } catch (OutOfMemoryError e) {
            System.err.println("Memoria insuficiente. Execute novamente com um vetor menor.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private static void executar(long semente) throws Exception {
        boolean trocar_vetor = true;
        while (trocar_vetor) {
            int tamanho = lerTamanho();
            byte[] vetor = new byte[tamanho];
            char resposta = lerResposta();
            Random gerador = new Random(semente);

            if (resposta == 's') {
                for (int i = 0; i < tamanho; i++)
                    vetor[i] = (byte) (gerador.nextInt(256) - 128);
                System.out.println("Vetor de bytes aleatórios montado");
                System.out.println("[LOG] Semente: " + semente + "; elementos: " + tamanho);
            } else {
                for (int i = 0; i < tamanho; i++)
                    vetor[i] = lerByte("Insira o " + (i + 1) + "º numero:",
                                     "erro deve colocar uma numero que caiba em um byte");
            }

            int processadores = Runtime.getRuntime().availableProcessors();
            System.out.println("\n--- ORDENACAO PARALELA ---");
            System.out.println("[LOG] Processadores disponiveis: " + processadores);
            if (processadores < 2)
                System.out.println("[LOG] Apenas um processador: sera usada uma tarefa, sem paralelismo real garantido.");
            long inicio = System.nanoTime();
            vetor = ordenarParalelo(vetor, processadores);
            long tempo = System.nanoTime() - inicio;
            System.out.println("[LOG] Concluido: " + vetor.length + " elementos ordenados.");
            mostrarTempo(tempo);

            boolean manter_vetor = true;
            while (manter_vetor) {
                System.out.println("\n--- CONSULTAR RESULTADO ---");
                System.out.println("1 - Mostrar todo o vetor ordenado");
                System.out.println("2 - Mostrar um intervalo do vetor");
                System.out.println("3 - Mostrar tempo de ordenacao");
                System.out.println("4 - Criar outro vetor");
                System.out.println("0 - Sair");
                byte opcao = lerByte(
                    "Escolha uma opcao:",
                    "A entrada PRECISA SER UM BYTE O ANTA DO CARALHO");

                switch (opcao) {
                    case 1:
                        printarParteDoVetor(1, vetor.length, vetor);
                        break;
                    case 2:
                        System.out.println("Posicoes de 1 a " + vetor.length + ", incluindo os dois limites.");
                        try {
                            System.out.println("Posicao inicial:");
                            int de = Integer.parseInt(lerLinha().trim());
                            System.out.println("Posicao final:");
                            int ate = Integer.parseInt(lerLinha().trim());
                            printarParteDoVetor(de, ate, vetor);
                        } catch (EOFException e) {
                            throw e;
                        } catch (Exception e) {
                            System.out.println("Tem que ser dois INT POSITIVOS SEU ANIMAL:" + e.getMessage());
                        }
                        break;
                    case 3:
                        mostrarTempo(tempo);
                        break;
                    case 4:
                        manter_vetor = false;
                        break;
                    case 0:
                        manter_vetor = false;
                        trocar_vetor = false;
                        System.out.println("Programa encerrado.");
                        break;
                    default:
                        System.out.println("POHA ANTA OS NUMEROS ESTÃO ESCRITOS E VOCÊ ERRA, BURRO");
                }
            }
        }
    }

    public static byte[] ordenarParalelo(byte[] vetor, int processadores) throws Exception {
        if (vetor == null || vetor.length == 0 || processadores < 1)
            throw new IllegalArgumentException("Vetor vazio ou processadores invalidos");

        int quantidade = processadores - 1;
        if (quantidade < 1)
            quantidade = 1;
        System.out.println("[LOG] Criando " + quantidade + " ordenadoras.");

        int base = vetor.length / quantidade;
        int sobra = vetor.length % quantidade;
        int posicao = 0;
        Ordenadora[] ordenadoras = new Ordenadora[quantidade];

        for (int i = 0; i < quantidade; i++) {
            int tamanho = base;
            if (i < sobra)
                tamanho++;
            byte[] pedaco = new byte[tamanho];
            for (int j = 0; j < tamanho; j++)
                pedaco[j] = vetor[posicao++];
            ordenadoras[i] = new Ordenadora(pedaco);
            ordenadoras[i].setName("Ordenadora-" + (i + 1));
        }

        // Inicia todas ANTES de esperar qualquer uma.
        for (int i = 0; i < quantidade; i++)
            ordenadoras[i].start();
        for (int i = 0; i < quantidade; i++)
            ordenadoras[i].join();

        byte[][] pedacos = new byte[quantidade][];
        for (int i = 0; i < quantidade; i++)
            pedacos[i] = ordenadoras[i].getResultado();
        // As tarefas encerradas nao precisam manter os pedacos antigos vivos.
        ordenadoras = null;

        int rodada = 1;
        while (pedacos.length > 1) {
            int pares = pedacos.length / 2;
            int semPar = pedacos.length % 2;
            System.out.println("[LOG] Rodada " + rodada + ": " + pares
                               + " misturadoras; " + semPar + " pedaco sem par.");
            Misturadora[] misturadoras = new Misturadora[pares];
            byte[][] proximos = new byte[pares + semPar][];

            for (int i = 0; i < pares; i++) {
                misturadoras[i] = new Misturadora(pedacos[2 * i], pedacos[2 * i + 1]);
                misturadoras[i].setName("Misturadora-R" + rodada + "-" + (i + 1));
            }
            if (semPar == 1)
                proximos[pares] = pedacos[pedacos.length - 1];

            for (int i = 0; i < pares; i++)
                misturadoras[i].start();
            for (int i = 0; i < pares; i++)
                misturadoras[i].join();
            for (int i = 0; i < pares; i++)
                proximos[i] = misturadoras[i].getResultado();

            pedacos = proximos;
            rodada++;
        }
        return pedacos[0];
    }

    private static String lerLinha() throws EOFException {
        String linha = Teclado.getUmString();
        if (linha == null)
            throw new EOFException("Entrada encerrada");
        return linha;
    }

    private static int lerTamanho() throws EOFException {
        while (true) {
            System.out.println("Quantos numeros deseja adicionar ao vetor");
            String linha = lerLinha();
            try {
                int tamanho = Integer.parseInt(linha.trim());
                if (tamanho > 0)
                    return tamanho;
                System.out.println("O tamanho deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Erro ao ler buffer: " + e.getMessage());
            }
        }
    }

    private static char lerResposta() throws EOFException {
        while (true) {
            System.out.println("Gerar valores aleatorios? [s = sim / n ou ENTER = digitar]");
            String linha = lerLinha().trim();
            if (linha.isEmpty() || linha.equalsIgnoreCase("n"))
                return 'n';
            if (linha.equalsIgnoreCase("s"))
                return 's';
            System.out.println("erro ao pegar a resposta");
        }
    }

    private static byte lerByte(String pergunta, String erro) throws EOFException {
        while (true) {
            System.out.println(pergunta);
            String linha = lerLinha();
            try {
                return Byte.parseByte(linha.trim());
            } catch (NumberFormatException e) {
                System.out.println(erro);
            }
        }
    }

    private static void mostrarTempo(long tempo) {
        System.out.printf("Tempo total de ordenacao paralela: %.3f ms (%.6f s)%n",
                          tempo / 1000000.0, tempo / 1000000000.0);
    }
}
