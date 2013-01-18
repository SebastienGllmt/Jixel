package console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

import stage.JixelGame;

public class JixelVariableManager {

	private final String SAV_DIR = "profiles";
	private final String SAV_NAME = "char";
	private final String SAV_TYPE = ".sav";

	private HashMap<String, Object> varMap = new HashMap<String, Object>();
	private HashMap<String, Object> objectMap = new HashMap<String, Object>();
	private HashMap<Object, HashMap<String, Method>> classMap = new HashMap<Object, HashMap<String, Method>>();

	public <T> void newVar(String name, T value) {
		if (varMap.containsKey(name)) {
			JixelGame.getConsole().print("A variable with the name " + name + " exists.");
			return;
		}
		varMap.put(name, value);
	}

	public void newClass(Object o, Class<?> clazz) {
		String className = clazz.getName();
		if (objectMap.containsKey(className)) {
			JixelGame.getConsole().print("The class " + className + " is already visible to the console");
			return;
		}
		objectMap.put(className, o);
		HashMap<String, Method> classData = new HashMap<String, Method>();
		Method[] methods = clazz.getMethods();
		for (Method i : methods) {
			String name = i.getName();
			if(classData.containsKey(name)){
				int j=2; //iterator in case method name already exists
				while(classData.containsKey(name)){
					name = i.getName() + j;
					j++;
				}
				JixelGame.getConsole().print("Duplicate method name " + i.getName() + " detected and was renamed to " + name);
			}
			classData.put(name, i);
		}

		classMap.put(o, classData);
	}

	public boolean containsClass(String className) {
		return objectMap.containsKey(className);
	}

	public Object runMethod(String className, String methodName, Object... args) {
		String errorMessage = "";
		Object o = objectMap.get(className);
		if (o == null) {
			return "No such class found: ";
		}
		try {
			Method method = classMap.get(o).get(methodName);
			if (method == null) {
				return "No such method found.";
			}
			Type[] parameters = method.getParameterTypes();
			int paraAmount = parameters.length;
			if (paraAmount == args.length) {
				for(int i=0; i<args.length; i++){
					args[i] = getValue((Class<?>)parameters[i], (String) args[i]);
				}
				return method.invoke(o, args);
			} else if (paraAmount == 0) {
				return method.invoke(o);
			} else {
				return "Invalid amount of parameters. Expected " + paraAmount;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			errorMessage = "Illegal Argument Exception: ";
		} catch (IllegalAccessException e) {
			errorMessage = "Illegal Access Exception: ";
		} catch (InvocationTargetException e) {
			errorMessage = "Invocation Target Exception: ";
		}
		return errorMessage + "Failed to run " + methodName + " in " + className;
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
	
	private Object getValue(Class<?> clazz, String value){
		if(clazz.equals(String.class)){
			return value;
		}if (clazz.equals(Byte.class)) {
			return Byte.parseByte(value);
		} else if (clazz.equals(Short.class)) {
			return Short.parseShort(value);
		} else if (clazz.equals(Integer.class)) {
			return Integer.parseInt(value);
		} else if (clazz.equals(Long.class)) {
			return Long.parseLong(value);
		} else if (clazz.equals(Float.class)) {
			return Float.parseFloat(value);
		} else if (clazz.equals(Double.class)) {
			return Double.parseDouble(value);
		} else if (clazz.equals(Boolean.class)) {
			return Boolean.parseBoolean(value);
		} else if (clazz.equals(Character.class)) {
			return value.charAt(0);
		} else {
			JixelGame.getConsole().print("Uncompatible conversion from String to " + clazz.getClass().toString());
			return null;
		}
	}

	public boolean setValue(String name, String value) {
		Class<?> clazz = JixelGame.getVM().getValue(name).getClass();
		try {
			varMap.put(name, getValue(clazz, value));
			return true;
		} catch (NumberFormatException e) {
			JixelGame.getConsole().print("Incompatible conversion from " + value + " to " + clazz.getName());
			return false;
		}
	}

	public <T> boolean setValue(String name, T value) {
		if (!varMap.containsKey(name)) {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return false;
		} else {
			Class<?> nameClass = varMap.get(name).getClass();
			Class<?> valueClass = value.getClass();
			if (nameClass.equals(valueClass)) {
				varMap.put(name, value);
				return true;
			}else{
				JixelGame.getConsole().print("Incompatible conversion from " + valueClass + " to " + nameClass);
				return false;
			}
		}
	}

	public boolean containsVar(String name) {
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
