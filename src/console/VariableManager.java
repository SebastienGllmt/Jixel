package console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import math.JixelMath;

public class VariableManager {

	private final int TYPES = 3;
	public int FLAG = 0;
	public int INT = 1;
	public int STRING = 2;
	
	private final String SAV_DIR = "profiles";
	private final String SAV_NAME = "char";
	private final String SAV_TYPE = ".sav";

	private List<List<Variable<?>>> variableArray = new ArrayList<List<Variable<?>>>();

	public VariableManager() {
		for(int i=0; i<TYPES; i++){
			variableArray.add(new ArrayList<Variable<?>>());
		}
		//Creates starting values so these sections can't be entirely missing to help the save/load process
		newVar(0, "Null", false, true);
		newVar(1, "Null", 0, true);
		newVar(2, "Null", "Null", true);
	}

	public <T> int newVar(int type, String name, T value, boolean toSave) {
		if(!JixelMath.isStartNum(name)){
			List<Variable<?>> array = variableArray.get(type);
			for(int i=0; i<array.size(); i++){
				if(array.get(i).getName().equals(name)){
					System.out.println("Invalid variable name. Name already exists.");
					return 0;
				}
			}
			int id = array.size();
			array.add(new Variable<T>(id, name, value, toSave));
			return id;
		}else{
			System.out.println("Invalid variable name. Can not start with a number.");
			return 0;
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
		int position = 0;
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
	public <T> void setValue(int type, int id, T t){
		variableArray.get(type).get(id).setValue(t);
	}
	
	public boolean save(int profileID){
		String filepath = String.format("%s%d%s", SAV_NAME, profileID, SAV_TYPE);
		return save(filepath);
	}
	public boolean save(String file){
		File dir = new File(SAV_DIR);
		if(!dir.exists()){
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		try{
			if(!f.exists()){
				f.createNewFile();
			}
			OutputStream out = new FileOutputStream(f);
			DataOutputStream dos = new DataOutputStream(out);
			for(int i=0; i<TYPES; i++){
				List<Variable<?>> tempArray = variableArray.get(i);
				int typeSize = getSaveSize(tempArray);
				dos.writeInt(typeSize);
				for(int j=0; j<typeSize; j++){
					dos.writeUTF(tempArray.get(j).getName());
					switch(i){
						case 0:
							dos.writeBoolean(getValueBoolean(i, j));
							break;
						case 1:
							dos.writeInt(getValueInt(i, j));
							break;
							
						case 2:
							dos.writeUTF(getValueString(i, j));
							break;							
					}
					
				}
			}
			out.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}
	
	private int getSaveSize(List<Variable<?>> tempArray){
		int size=0;
		for(int i=0; i<tempArray.size(); i++){
			if(tempArray.get(i).getSave()){
				size++;
			}
		}
		return size;
	}
	
	/**
	 * Loads a profile by its id
	 * Warning: Will clear all variables, toSave or not
	 * @param profileID
	 * @return whether the profile loaded correctly
	 */
	public boolean load(int profileID){
		String filepath = String.format("%s%d%s", SAV_NAME, profileID, SAV_TYPE);
		return load(filepath);
	}
	/**
	 * Loads a file with a given name
	 * Warning: Will clear all variables, toSave or not.
	 * @param file - File name in /profiles/
	 * @return whether the profile loaded correctly
	 */
	public boolean load(String file){
		File dir = new File(SAV_DIR);
		if(!dir.exists()){
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		try{
			if(!f.exists()){
				return false;
			}
			clearVars();
			InputStream in = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(in);
			for(int i=0; i<TYPES; i++){
				int typeSize = dis.readInt();
				for(int j=0; j<typeSize; j++){
					String name = dis.readUTF();
					switch(i){
						case 0:
							newVar(i, name, dis.readBoolean(), true);
							break;
						case 1:
							newVar(i, name, dis.readInt(), true);
							break;
						case 2:
							newVar(i, name, dis.readUTF(), true);
							break;
					}
				}
			}
			in.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}
	
	/**
	 * Warning: Will clear all variables, toSave or not
	 */
	private void clearVars(){
		for(int i=0; i<TYPES; i++){
			List<Variable<?>> tempArray = variableArray.get(i);
			while(tempArray.size() > 0){
				variableArray.get(i).remove(0);
			}
		}
	}
}
