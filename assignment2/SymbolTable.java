import java.util.Map;
import java.util.HashMap;

public class SymbolTable {

	private Map<String, Object> map;

  private Token id;
	private Token token;
	private String scope;
	private DataType dataType;

	SymbolTable(Token id, String scope, DataType dataType) {
		this.id = id;
		this.scope = scope;
		this.map = new HashMap<String, Object>();
		this.dataType = dataType;
	}

	SymbolTable(Token id, Token token, String scope, DataType dataType) {
		this.id = id;
		this.token = token;
		this.scope = scope;
		this.map = new HashMap<String, Object>();
		this.dataType = dataType;
	}

	void add(String key, Object value) {
		map.put(key, value);
	}

	Object get(String key) {
		return map.get(key);
	}

	Token getID() {
		return id;
	}

	void setIdentifer(Token id) {
		this.id = id;
	}

	Object getValues() {
		return map;
	}

	Token getToken() {
		return token;
	}

	DataType getDataType() {
		return dataType;
	}

	void setToken(Token token) {
		this.token = token;
	}

	public String toString() {
		return token + " " + id + " " + map;
	}
}
