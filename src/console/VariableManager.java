package console;

import java.util.ArrayList;
import java.util.List;

public class VariableManager {

	public int FLAG = 0;
	public int INT = 1;
	public int STRING = 2;

	private List<List<Variable<?>>> variableArray = new ArrayList<List<Variable<?>>>();

	public VariableManager() {
		variableArray.add(new ArrayList<Variable<?>>());
		variableArray.add(new ArrayList<Variable<?>>());
		variableArray.add(new ArrayList<Variable<?>>());
	}

	public <T> void newVar(int type, String name, T t) {
		byte b = (byte)name.charAt(0);
		if(b < 48 || b > 57){
			variableArray.get(type).add(new Variable<T>(variableArray.size(), name, t));
		}else{
			System.out.println("Invalid variable name. Can not start with a number.");
		}
	}
	
	public String toString(int type, String name){
		return toString(type, getID(type, name));
	}
	public <T> String toString(int type, int id){
		return variableArray.get(type).get(id).toString();
	}
	public int getID(int type, String name){
		List<Variable<?>> array = variableArray.get(type);
		int position = -1;
		for(int i=0; i<array.size(); i++){
			if(array.get(i).getName().equals(name)){
				position = i;
				break;
			}
		}
		return position;
	}
	public String getName(int type, int id){
		return variableArray.get(type).get(id).getName();
	}
	
	public Object getValue(int type, int id){
		return variableArray.get(type).get(id).getValue();
	}
	public Object getValue(int type, String name){
		return variableArray.get(type).get(getID(type, name)).getValue();
	}
	
	public int getValueInt(int type, int id){
		return (Integer)getValue(type, id);
	}
	public int getValueInt(int type, String name){
		return (Integer)getValue(type, name);
	}
	public boolean getValueBoolean(int type, int id){
		return (Boolean)getValue(type, id);
	}
	public boolean getValueBoolean(int type, String name){
		return (Boolean)getValue(type, name);
	}
	public String getValueString(int type, int id){
		return getValue(type, id).toString();
	}
	public String getValueString(int type, String name){
		return getValue(type, name).toString();
	}
}
