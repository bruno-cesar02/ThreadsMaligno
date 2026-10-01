public class Misturadora extends Thread
{
    private byte[] a;
    private byte[] b;
    private byte[] resultado;

    public Misturadora(byte[] a, byte[] b) throws Exception
    {
        if (a == null || b == null)
            throw new Exception("Vetores de entrada ausentes");

        this.a = a;
        this.b = b;
    }

    @Override
    public void run()
    {
        this.resultado = merge(this.a, this.b);
    }

    // chamar esse metodo depois do join
    public byte[] getResultado()
    {
        return this.resultado;
    }

   
    public static byte[] merge(byte[] a, byte[] b)
    {
        byte[] resultado = new byte[a.length + b.length];

        int i = 0;
        int j = 0;
        int k = 0;

        // Compara enquanto os dois vetores possuem elementos.
        while (i < a.length && j < b.length)
        {
            if (a[i] <= b[j])
            {
                resultado[k] = a[i];
                i++;
            }
            else
            {
                resultado[k] = b[j];
                j++;
            }

            k++;
        }

        // Copia os elementos que sobrou de A.
        while (i < a.length)
        {
            resultado[k] = a[i];
            i++;
            k++;
        }

        // Copia os elementos que sobrou de B.
        while (j < b.length)
        {
            resultado[k] = b[j];
            j++;
            k++;
        }

        return resultado;
    }
}