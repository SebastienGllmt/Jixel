package console;

public class Variable<T> {

	private T t;
	private String name;
	private int id;
	
	public Variable(int id, String name, T type){
		this.name = name;
		this.t = type;
		this.id = id;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(Object o) { 
		this.t = (T) o; 
	}
	public T getValue() {
		return t;
	}
	public String getName(){
		return name;
	}
	public int getID(){
		return id;
	}
	@Override
	public String toString(){
		return id + ") " + name + " : " + t;
	}
}
