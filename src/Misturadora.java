public class Misturadora extends Thread {
    private final byte[] a;
    private final byte[] b;
    private byte[] resultado;

    public Misturadora(byte[] a, byte[] b) throws Exception {
        if (a == null || b == null)
            throw new Exception("Vetores de entrada ausentes");
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        this.resultado = merge(this.a, this.b);
    }

    // Chamar este metodo depois do join.
    public byte[] getResultado() {
        if (getState() != Thread.State.TERMINATED || this.resultado == null)
            throw new IllegalStateException("Resultado ainda não disponível; a tarefa pode ter falhado");
        return this.resultado;
    }

    public static byte[] merge(byte[] a, byte[] b) {
        byte[] resultado = new byte[a.length + b.length];
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                resultado[k] = a[i];
                i++;
            } else {
                resultado[k] = b[j];
                j++;
            }
            k++;
        }
        while (i < a.length) {
            resultado[k] = a[i];
            i++;
            k++;
        }
        while (j < b.length) {
            resultado[k] = b[j];
            j++;
            k++;
        }
        return resultado;
    }
}
