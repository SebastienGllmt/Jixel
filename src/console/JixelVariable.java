package console;

public class JixelVariable<T> {

	private T value;
	private String name;
	private int id;
	
	public JixelVariable(int id, String name, T value){
		this.name = name;
		this.value = value;
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object value) { 
		this.value = (T) value; 
	}
	public T getValue() {
		return value;
	}
	public String getName(){
		return name;
	}
	public int getID(){
		return id;
	}
	@Override
	public String toString(){
		return id + ") " + name + " : " + value;
	}
}
