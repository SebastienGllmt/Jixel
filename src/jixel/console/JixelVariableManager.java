package jixel.console;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jixel.stage.JixelGame;

final class defaultLoader extends JixelLoader
{
	@Override
	public void loadState() {
	}
	
}

public final class JixelVariableManager {

	private static final String SAV_DIR = "profiles";
	private static final String SAV_NAME = "char";
	private static final String SAV_TYPE = ".sav";

	private JixelLoader loader = new defaultLoader();

	private Map<String, Object> varMap = new HashMap<String, Object>();
	private Map<String, Object> objectMap = new HashMap<String, Object>();
	private Map<Object, HashMap<String, Method>> classMap = new HashMap<Object, HashMap<String, Method>>();

	public JixelVariableManager() {
		varMap = Collections.synchronizedMap(varMap);
		objectMap = Collections.synchronizedMap(objectMap);
		classMap = Collections.synchronizedMap(classMap);
	}

	public synchronized <T> void newVar(String name, T value) {
		if (varMap.containsKey(name)) {
			JixelGame.getConsole().print("A variable with the name " + name + " exists.");
			return;
		}

		varMap.put(name, value);
	}

	public synchronized void newClass(Object o, Class<?> clazz) {
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
			if (classData.containsKey(name)) {
				int j = 2; // iterator in case method name already exists
				while (classData.containsKey(name)) {
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

	public synchronized Object runMethod(String className, String methodName, Object... args) {
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
				for (int i = 0; i < args.length; i++) {
					args[i] = convertValue((Class<?>) parameters[i], (String) args[i]);
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
	public synchronized <T> T getValue(String name) {
		if (varMap.containsKey(name)) {
			Object o = varMap.get(name);
			return (T) o;
		} else {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return null;
		}
	}

	private synchronized Object convertValue(Class<?> clazz, String value) {
		if (clazz.equals(String.class)) {
			return value;
		}
		if (clazz.equals(Byte.class)) {
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
			JixelGame.getConsole().print("Incompatible conversion from String to " + clazz.getClass().toString());
			return null;
		}
	}

	public synchronized boolean setValue(String name, String value) {
		Class<?> clazz = JixelGame.getVM().getValue(name).getClass();
		try {
			varMap.put(name, convertValue(clazz, value));
			return true;
		} catch (NumberFormatException e) {
			JixelGame.getConsole().print("Incompatible conversion from " + value + " to " + clazz.getName());
			return false;
		}
	}

	public synchronized <T> boolean setValue(String name, T value) {
		if (!varMap.containsKey(name)) {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return false;
		} else {
			Object mapValue = varMap.get(name);
			Class<?> valueClass = value.getClass();
			try {
				varMap.put(name, valueClass.cast(value));
				return true;
			} catch (ClassCastException e) {
				JixelGame.getConsole().print("Incompatible conversion from " + valueClass + " to " + mapValue.getClass());
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
			setValue("Jixel_entityList", JixelGame.getEntityList().getList()); // force
															// update
			synchronized (JixelGame.getUpdateLock()) {
				oos.writeObject(varMap);
				oos.flush();
				oos.close();
			}
			return true;
		} catch (IOException e) {
			JixelGame.getConsole().print("IO Error on save of " + f.getPath());
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
	 * @param file - File name in /profiles/
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
			synchronized (JixelGame.getUpdateLock()) {
				JixelGame.setPaused(true);
				varMap.clear();
				try {
					varMap = (Map<String, Object>) ois.readObject();
				} catch (ClassNotFoundException e) {
					ois.close();
					return false;
				}
				if (loader != null) {
					loader.runLoader();
				} else {
					JixelGame.getConsole().print("Error: No loader attached to VM");
				}
				ois.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("IO Error on load of " + f.getPath());
			return false;
		}
	}

	public void setLoader(JixelLoader loader) {
		this.loader = loader;
	}
}
