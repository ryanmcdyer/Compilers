public class Quadruple
{
	String op;
	String arg1;
	String arg2;
	String result;

	public Quadruple(String iop, String iResult, String iarg1, String iarg2)
	{
		op = iop;
		arg1 = iarg1;
		arg2 = iarg2;
		result = iResult;
	}

	public Quadruple(String iop, String iResult, String iarg1 )
	{
		op = iop;
		arg1 = iarg1;
		arg2 = "";
		result = iResult;
	}

	public Quadruple(String iop, String iarg1 )
	{
		op = iop;
		arg1 = iarg1;
		arg2 = "";
		result = "";
	}

	public void printQuad()
	{
		System.out.println("[ "+op+" , "+arg1+" , "+arg2+" , "+result+" ]");
	}
}