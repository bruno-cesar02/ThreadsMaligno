import java.util.concurrent.Semaphore;
import java.util.Random;

public class Main {
    public static void printarParteDoVetor (int inicio , int fim, byte[] vetor) throws Exception{
        if (inicio < 0 || fim < inicio){
            throw new Exception("Anta, o segundo tem que ser maior que o primeiro");
        }
        System.out.print("[");
        for(int i = inicio - 1; i < fim; i++ ){
            System.out.print(", " + vetor[i]);
        }
        System.out.println("]");
    }


    public static void main(String[] args) {
        Semaphore thread_disponiveis = new Semaphore(Runtime.getRuntime().availableProcessors() - 1, true);
        byte[] vetor_desorganizado;
        int tamanho_do_vetor = 0;
        char resposta = 'N' ;
        Random gerador = new Random();
        boolean trocar_vetor = true;

        while (trocar_vetor) {
            System.out.println("Quantos numeros deseja adicionar ao vetor");
            try {
                tamanho_do_vetor = Teclado.getUmInt();
            } catch (Exception e) {
                System.err.println("Erro ao ler buffer: " + e);
            }

            vetor_desorganizado = new byte[tamanho_do_vetor];

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
                        vetor_desorganizado[i] = Teclado.getUmByte();

                    }
                } catch (Exception e) {
                    System.err.println("erro deve colocar uma numero que caiba em um byte");
                }

            } else {
                for (int i = 0; i < tamanho_do_vetor; i++) {
                    vetor_desorganizado[i] = (byte) (gerador.nextInt(256) - 128);
                }
                System.out.println("Vetor de bytes aleatórios montado");
            }
            boolean manter_vetor = true;
            while (manter_vetor) {
                System.out.println("O que deseja fazer agora? (1-Bagunçar/2-Organizar/3-Mostar um pedaço do vetor/4-Montar outro vetor/5-Sair ");

                byte opcao;
                try {
                    opcao = Teclado.getUmByte();
                } catch (Exception e) {
                    System.err.println("A entrada PRECISA SER UM BYTE O ANTA DO CARALHO");
                    return;
                }
                switch (opcao) {
                    case ((byte) (1)):
                        // aqui coloca a chamada do bagunçador
                    case ((byte) (2)):
                        // aqui coloca o algoritmo de merge sort
                    case ((byte) (3)):
                        System.out.println("Entre dois valores, de onde começa a onde termina de buscar");
                        try {
                            int inicio = Teclado.getUmInt();
                            int fim = Teclado.getUmInt();
                            printarParteDoVetor(inicio,fim, vetor_desorganizado);
                        } catch (Exception e){
                            System.err.println("Tem que ser dois INT POSITIVOS SEU ANIMAL:" + e);
                        }

                    case ((byte) (4)):
                        manter_vetor = false;

                    case ((byte) (5)):
                        manter_vetor = false;
                        trocar_vetor = false;

                    default:
                        System.err.println("POHA ANTA OS NUMEROS ESTÃO ESCRITOS E VOCÊ ERRA, BURRO");
                }
            }
        }
    }
}
