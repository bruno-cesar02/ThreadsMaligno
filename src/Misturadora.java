import java.util.Vector;

public class Misturadora extends Thread
{
    private Vector<Byte> a;
    private Vector<Byte> b;
    private Vector<Byte> resultado;

    public Misturadora(Vector<Byte> a, Vector<Byte> b)
        throws Exception
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
    public Vector<Byte> getResultado()
    {
        return this.resultado;
    }

   
    public static Vector<Byte> merge(Vector<Byte> a, Vector<Byte> b)
    {
        Vector<Byte> resultado = new Vector<Byte>();

        int i = 0;
        int j = 0;

        
        while (i < a.size() && j < b.size())
        {
            if (a.get(i) <= b.get(j))
            {
                resultado.add(a.get(i));
                i++;
            }
            else
            {
                resultado.add(b.get(j));
                j++;
            }
        }

        // Copia o q sobrou de A.
        while (i < a.size())
        {
            resultado.add(a.get(i));
            i++;
        }

        // Copia o q sobrou de B.
        while (j < b.size())
        {
            resultado.add(b.get(j));
            j++;
        }

        return resultado;
    }
}