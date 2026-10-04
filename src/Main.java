import java.util.concurrent.Semaphore;
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
        int thread_disponiveis = (Runtime.getRuntime().availableProcessors() - 1 );
        byte[] vetor;
        byte[] vetor_ordenado = null;
        int tamanho_do_vetor = 0;
        char resposta = 'N' ;
        Random gerador = new Random();
        boolean trocar_vetor = true;
        Ordenadora[] ordenadora;
        byte[] metade_esquerda;
        byte[] metade_direita;

        while (trocar_vetor) {
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

            metade_esquerda = new byte[vetor.length/2];
            metade_direita = new byte[vetor.length/2];

            for (int a = 0 ; a < vetor.length/2 ; a++){
                metade_esquerda[a] = vetor[a];
                metade_direita[a] = vetor[(vetor.length/2)+a];
            }

            boolean manter_vetor = true;
            while (manter_vetor) {
                System.out.println("O que deseja fazer agora? (1-BOrganizar sem paralelismo/2-Organizar com paralelismo/3-Mostar um pedaço do vetor/4-Montar outro vetor/5-Sair ");

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
                            ordenadora = new Ordenadora[1];
                            ordenadora[0] = new Ordenadora(vetor);

                            ordenadora[0].start();
                            ordenadora[0].join();

                            vetor = ordenadora[0].getResultado();

                        } catch (Exception e) {
                            System.err.println(e);
                        }
                        break;
                    case ((byte) (2)):
                        try {
                            ordenadora = new Ordenadora[thread_disponiveis];

                            byte[][] paretes_vetor = new byte[thread_disponiveis][];
                            int tamanhoBase = vetor.length / thread_disponiveis;
                            int resto = vetor.length % thread_disponiveis;

                            int posicaoAtual = 0;

                            for (int i = 0; i < thread_disponiveis; i++) {

                                int tamanhoDestePedaco = tamanhoBase;
                                if (i == thread_disponiveis - 1) {
                                    tamanhoDestePedaco += resto;
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

                            vetor_ordenado = new byte[tamanho_do_vetor];
                            int posicaoMerge = 0;

                            for (int i = 0; i < thread_disponiveis; i++) {
                                byte[] pedacoPronto = ordenadora[i].getResultado();

                                System.arraycopy(pedacoPronto, 0, vetor_ordenado, posicaoMerge, pedacoPronto.length);

                                posicaoMerge += pedacoPronto.length;
                            }
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

                    default:
                        System.err.println("POHA ANTA OS NUMEROS ESTÃO ESCRITOS E VOCÊ ERRA, BURRO");
                        break;
                }
            }
        }
    }
}
