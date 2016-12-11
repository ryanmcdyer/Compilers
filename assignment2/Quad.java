public class Quad
{
	String op, result;
	String[] arg;

	public Quad(String op0, String arg1, String arg2, String result0)
	{
		op = op0;
		arg[0] = arg1;
		arg[1] = arg2;
		result = result0;
	}

	public Quad(String op0, String arg1, String result0)
	{
		op = op0;
		arg[0] = arg1;
		arg[1] = "";
		result = result0;
	}

	public Quad(String op0, String arg1)
	{
		op = op0;
		arg[0] = arg1;
		arg[1] = "";
		result = "";
	}

	public void printQuad()
	{
		System.out.println("[ " + op + ",\t" + arg[0] + ",\t" + arg[1] + ",\t" + result + " ]");
	}
}
