import java.util.HashMap;

class STC extends Object
{
	Token name;
	Token type;
	DataType dType; 
	String scope;
	HashMap<String,Object> values;

	public STC(Token iname, Token itype, String iscope,DataType iDType)
	{
		name = iname;
		type = itype;
		scope = iscope;
		dType = iDType;
		values = new HashMap<String,Object>();
	}

	public STC(Token iname, Token itype,DataType iDType)
	{
		name = iname;
		type = itype;
		dType = iDType;
		values = new HashMap<String,Object>();
	}

	public void setScope(String iscope)
	{
		scope = iscope; 
	}

	public void addValue(String name, Object value)
	{
		values.put(name,value);
	}

	public Object getValue(String name)
	{
		return values.get(name);
	}

}