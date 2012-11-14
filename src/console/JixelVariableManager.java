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
		if (varMap.containsKey(name)) {
			JixelGame.getConsole().print("A variable with the name " + name + " exists.");
			return;
		}
		varMap.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(String name) {
		if (varMap.containsKey(name)) {
			Object o = varMap.get(name);
			return (T) o;
		} else {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return null;
		}
	}

	private boolean setValue(String name, String value){
		Class<?> clazz = JixelGame.getVM().getValue(name).getClass();
		try{
			if(clazz.equals(Byte.class)){
				varMap.put(name, Byte.parseByte(value));
			}else if(clazz.equals(Short.class)){
				varMap.put(name, Short.parseShort(value));
			}else if(clazz.equals(Integer.class)){
				varMap.put(name, Integer.parseInt(value));
			}else if(clazz.equals(Long.class)){
				varMap.put(name, Long.parseLong(value));
			}else if(clazz.equals(Float.class)){
				varMap.put(name, Float.parseFloat(value));
			}else if(clazz.equals(Double.class)){
				varMap.put(name, Double.parseDouble(value));
			}else if(clazz.equals(Boolean.class)){
				varMap.put(name, Boolean.parseBoolean(value));
			}else if(clazz.equals(Character.class)){
				varMap.put(name, value.charAt(0));
			}else{
				JixelGame.getConsole().print("The variable type of " + name + " can not be set");
				return false;
			}
		} catch(NumberFormatException e){
			JixelGame.getConsole().print("Invalid conversion from " + value + " to " + clazz.getName());
			return false;
		}
		return true;
	}

	public <T> boolean setValue(String name, T value) {
		if (!varMap.containsKey(name)) {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return false;
		} else {
			Class<?> mapClass = varMap.get(name).getClass();
			Class<?> valueClass = value.getClass();
			if(mapClass.equals(String.class) || mapClass.equals(valueClass)){
				varMap.put(name, value);
				return true;
			}else if(valueClass.equals(String.class)){
				return setValue(name, value.toString());
			}else{
				JixelGame.getConsole().print("Uncompatible conversion from " + valueClass.toString() + " to " + mapClass.toString());
				return false;
			}
		}
	}

	public boolean contains(String name) {
		return varMap.containsKey(name);
	}

	public boolean save(int id) {
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
		return save(filepath);
	}

	public boolean save(String file) {
		File dir = new File(SAV_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			OutputStream out = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(out);
			setValue("Jixel_paused", true);
			oos.writeObject(varMap);
			oos.flush();
			oos.close();
			setValue("Jixel_paused", false);
			return true;
		} catch (IOException e) {
			setValue("Jixel_paused", false);
			return false;
		}
	}

	/**
	 * Loads a profile by its id Warning: Will clear all variables
	 * 
	 * @param profileID
	 * @return whether the profile loaded correctly
	 */
	public boolean load(int id) {
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
		return load(filepath);
	}

	/**
	 * Loads a file with a given name Warning: Will clear all variables
	 * 
	 * @param file
	 *              - File name in /profiles/
	 * @return whether the profile loaded correctly
	 */
	@SuppressWarnings("unchecked")
	public boolean load(String file) {
		File dir = new File(SAV_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		try {
			if (!f.exists()) {
				return false;
			}
			InputStream in = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(in);
			setValue("Jixel_paused", true);
			varMap.clear();
			try {
				varMap = (HashMap<String, Object>) ois.readObject();
			} catch (ClassNotFoundException e) {
				ois.close();
				return false;
			}
			ois.close();
			setValue("Jixel_paused", false);
			return true;
		} catch (IOException e) {
			setValue("Jixel_paused", false);
			return false;
		}
	}
}
