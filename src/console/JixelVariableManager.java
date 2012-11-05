package console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import stage.JixelGame;

public class JixelVariableManager {
	
	private final String SAV_DIR = "profiles";
	private final String SAV_NAME = "char";
	private final String SAV_TYPE = ".sav";

	private HashMap<String, Object> varMap = new HashMap<String, Object>();

	public <T> void newVar(String name, T value) {
		if(varMap.containsKey(name)){
			System.out.println("A variable with that name already exists");
			return;
		}
		varMap.put(name, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(String name){
		Object o = varMap.get(name);
		if(o == null){
			JixelGame.getConsole().print("No such variable exists.");
		}
		return (T) o;
	}
	
	public <T> void setValue(String name, T value){
		if(!varMap.containsKey(name)){
			JixelGame.getConsole().print("No such variable detected so it was created.");
		}
		varMap.put(name, value);
	}
	
	public boolean save(int id){
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
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
			ObjectOutputStream oos = new ObjectOutputStream(out);
			setValue("paused", true);
			oos.writeObject(varMap);
			oos.flush();
			oos.close();
			out.close();
			setValue("paused", false);
			return true;
		}catch(IOException e){
			setValue("paused", false);
			return false;
		}
	}
	
	/**
	 * Loads a profile by its id
	 * Warning: Will clear all variables
	 * @param profileID
	 * @return whether the profile loaded correctly
	 */
	public boolean load(int id){
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
		return load(filepath);
	}
	/**
	 * Loads a file with a given name
	 * Warning: Will clear all variables
	 * @param file - File name in /profiles/
	 * @return whether the profile loaded correctly
	 */
	@SuppressWarnings("unchecked")
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
			InputStream in = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(in);
			setValue("paused", true);
			varMap.clear();
			try {
				varMap = (HashMap<String,Object>)ois.readObject();
			} catch (ClassNotFoundException e) {
				return false;
			}
			ois.close();
			in.close();
			setValue("paused", false);
			return true;
		}catch(IOException e){
			setValue("paused", false);
			return false;
		}
	}
}
