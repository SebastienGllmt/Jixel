package console;

public class Variable<T> {

	private T value;
	private String name;
	private int id;
	private boolean toSave;
	
	public Variable(int id, String name, T value, boolean toSave){
		this.name = name;
		this.value = value;
		this.id = id;
		this.toSave = toSave;
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
	public boolean getSave(){
		return toSave;
	}
	@Override
	public String toString(){
		return id + ") " + name + " : " + value;
	}
}
