import java.util.Random;

public class Main {
    public static void printarParteDoVetor (int inicio , int fim, byte[] vetor) throws Exception{
        if (inicio < 1 || fim < inicio || fim > vetor.length){
            throw new Exception("Anta, o segundo tem que ser maior que o primeiro");
        }
        System.out.print("[");
        for(int i = inicio - 1; i < fim; i++ ){
            System.out.print(vetor[i]);

            if (i < fim - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        // Com apenas um processador, usa uma tarefa para evitar divisao por zero.
        int thread_disponiveis = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        byte[] vetor;
        byte[] vetor_ordenado = null;
        int tamanho_do_vetor = 0;
        char resposta = 'N' ;
        Random gerador = new Random();
        boolean trocar_vetor = true;
        Ordenadora[] ordenadora;

        while (trocar_vetor) {
            vetor_ordenado = null;
            double tempoSemParalelismo = -1;
            double tempoComParalelismo = -1;
            System.out.println("Quantos numeros deseja adicionar ao vetor");
            try {
                tamanho_do_vetor = Teclado.getUmInt();
            } catch (Exception e) {
                System.err.println("Erro ao ler buffer: " + e);
            }

            vetor = new byte[tamanho_do_vetor];

            System.out.println("Deseja adicionar os valores automaticamente?(s/N)");
            try {
                resposta = Teclado.getUmChar();
                resposta = Character.toLowerCase(resposta);
            } catch (Exception e) {
                System.err.println("erro ao pegar a resposta");
            }
            if (resposta != 's') {
                try {
                    for (int i = 0; i < tamanho_do_vetor; i++) {
                        System.out.println("Insira o " + (i + 1) + "º numero:");
                        vetor[i] = Teclado.getUmByte();

                    }
                } catch (Exception e) {
                    System.err.println("erro deve colocar uma numero que caiba em um byte");
                }

            } else {
                for (int i = 0; i < tamanho_do_vetor; i++) {
                    vetor[i] = (byte) (gerador.nextInt(256) - 128);
                }
                System.out.println("Vetor de bytes aleatórios montado");
            }

            boolean manter_vetor = true;
            while (manter_vetor) {
                System.out.println("\nO que deseja fazer agora?");
                System.out.println("1 - Organizar sem paralelismo");
                System.out.println("2 - Organizar com paralelismo");
                System.out.println("3 - Mostrar um pedaço do vetor");
                System.out.println("4 - Montar outro vetor");
                System.out.println("5 - Sair");
                System.out.println("6 - Mostrar tempos");
                System.out.print("Escolha uma opção: ");

                byte opcao;
                try {
                    opcao = Teclado.getUmByte();
                } catch (Exception e) {
                    System.err.println("A entrada PRECISA SER UM BYTE O ANTA DO CARALHO");
                    return;
                }
                switch (opcao) {
                    case ((byte) (1)):
                        try {
                            long inicioTempo = System.nanoTime();
                            ordenadora = new Ordenadora[1];
                            ordenadora[0] = new Ordenadora(vetor);

                            ordenadora[0].start();
                            ordenadora[0].join();

                            // Preserva a entrada original para comparar as duas opcoes.
                            vetor_ordenado = ordenadora[0].getResultado();
                            tempoSemParalelismo = (System.nanoTime() - inicioTempo) / 1_000_000.0;

                        } catch (Exception e) {
                            System.err.println(e);
                        }
                        break;
                    case ((byte) (2)):
                        try {
                            long inicioTempo = System.nanoTime();
                            ordenadora = new Ordenadora[thread_disponiveis];

                            byte[][] paretes_vetor = new byte[thread_disponiveis][];
                            int tamanhoBase = vetor.length / thread_disponiveis;
                            int resto = vetor.length % thread_disponiveis;

                            int posicaoAtual = 0;

                            for (int i = 0; i < thread_disponiveis; i++) {

                                int tamanhoDestePedaco = tamanhoBase;
                                if (i < resto) {
                                    tamanhoDestePedaco++;
                                }

                                byte[] pedacoRecortado = new byte[tamanhoDestePedaco];

                                System.arraycopy(vetor, posicaoAtual, pedacoRecortado, 0, tamanhoDestePedaco);

                                paretes_vetor[i] = pedacoRecortado;

                                posicaoAtual += tamanhoDestePedaco;
                            }
                            for (int i = 0 ; i < thread_disponiveis ; i++){
                                ordenadora[i] = new Ordenadora(paretes_vetor[i]);

                                ordenadora[i].start();
                            }
                            for (int i = 0 ; i < thread_disponiveis ; i++){ ordenadora[i].join(); }

                            byte[][] pedacosOrdenados = new byte[thread_disponiveis][];
                            for (int i = 0; i < thread_disponiveis; i++) {
                                pedacosOrdenados[i] = ordenadora[i].getResultado();
                            }

                            // Intercala os pedacos aos pares ate restar o vetor completo.
                            while (pedacosOrdenados.length > 1) {
                                int quantidadePares = pedacosOrdenados.length / 2;
                                int sobra = pedacosOrdenados.length % 2;
                                Misturadora[] misturadoras = new Misturadora[quantidadePares];

                                for (int i = 0; i < quantidadePares; i++) {
                                    misturadoras[i] = new Misturadora(
                                            pedacosOrdenados[2 * i],
                                            pedacosOrdenados[2 * i + 1]);
                                }
                                for (int i = 0; i < quantidadePares; i++) {
                                    misturadoras[i].start();
                                }
                                for (int i = 0; i < quantidadePares; i++) {
                                    misturadoras[i].join();
                                }

                                byte[][] proximaRodada = new byte[quantidadePares + sobra][];
                                for (int i = 0; i < quantidadePares; i++) {
                                    proximaRodada[i] = misturadoras[i].getResultado();
                                }
                                if (sobra == 1) {
                                    // O ultimo pedaco sem par segue intacto.
                                    proximaRodada[quantidadePares] =
                                            pedacosOrdenados[pedacosOrdenados.length - 1];
                                }
                                pedacosOrdenados = proximaRodada;
                            }
                            vetor_ordenado = pedacosOrdenados[0];
                            tempoComParalelismo = (System.nanoTime() - inicioTempo) / 1_000_000.0;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Ordenação interrompida.");
                            return;
                        } catch (Exception e) {
                            System.err.println("Erro ao ordenar: " + e.getMessage());
                        }
                        break;
                        
                    case ((byte) (3)):
                        System.out.println("Entre dois valores, de onde começa a onde termina de buscar");
                        try {
                            int inicio = Teclado.getUmInt();
                            int fim = Teclado.getUmInt();
                            if (vetor_ordenado == null) {
                                printarParteDoVetor(inicio, fim, vetor);
                            }
                            else {
                                printarParteDoVetor(inicio,fim,vetor_ordenado);
                            }
                        } catch (Exception e){
                            System.err.println("Tem que ser dois INT POSITIVOS SEU ANIMAL:" + e);
                        }
                        break;

                    case ((byte) (4)):
                        manter_vetor = false;
                        break;

                    case ((byte) (5)):
                        manter_vetor = false;
                        trocar_vetor = false;
                        break;

                    case ((byte) (6)):
                        if (tempoSemParalelismo < 0)
                            System.out.println("A opcao 1 ainda nao foi executada neste vetor.");
                        else
                            System.out.printf("Tempo da opcao 1: %.3f ms%n", tempoSemParalelismo);

                        if (tempoComParalelismo < 0)
                            System.out.println("A opcao 2 ainda nao foi executada neste vetor.");
                        else
                            System.out.printf("Tempo da opcao 2: %.3f ms%n", tempoComParalelismo);
                        break;
                    default:
                        System.err.println("POHA ANTA OS NUMEROS ESTÃO ESCRITOS E VOCÊ ERRA, BURRO");
                        break;
                }
            }
        }
    }
}
