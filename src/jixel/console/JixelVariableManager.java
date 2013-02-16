package jixel.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

final class defaultStateManager extends JixelStateManager {
	@Override
	public void loadState() {
	}

	@Override
	public void saveState() {
	}

}

public final class JixelVariableManager {
	private static final String SAV_DIR = "saves";
	private static final String SAV_NAME = "profile";
	private static final String SAV_TYPE = ".sav";

	private JixelStateManager stateManager = new defaultStateManager();

	private Map<String, Object> varMap = new HashMap<String, Object>();
	private Map<String, Object> objectMap = new HashMap<String, Object>();
	private Map<Object, HashMap<String, Method>> classMap = new HashMap<Object, HashMap<String, Method>>();

	public JixelVariableManager() {
		varMap = Collections.synchronizedMap(varMap);
		objectMap = Collections.synchronizedMap(objectMap);
		classMap = Collections.synchronizedMap(classMap);
	}

	/**
	 * Adds a new variable to the VM
	 * @param name - The name of the variable
	 * @param value - The initial value of the variable
	 */
	public synchronized <T> void newVar(String name, T value) {
		if (varMap.containsKey(name)) {
			JixelGame.getConsole().print("A variable with the name " + name + " exists.");
			return;
		}

		varMap.put(name, value);
	}

	/**
	 * Adds a class to the VM
	 * @param o - The instance to run methods on
	 * @param clazz - The class to add
	 */
	public synchronized void newClass(Object o, Class<?> clazz) {
		if (o == null || clazz == null) {
			JixelGame.getConsole().printErr("Classes and instances in console must be non-null", new NullPointerException());
		}
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

	/**
	 * Returns whether or not the VM contains a given class
	 * @param className - The name of the class
	 * @return whether or not the VM contains it
	 */
	public boolean containsClass(String className) {
		return objectMap.containsKey(className);
	}

	/**
	 * Runs a method of a class on the instance stored in the VM
	 * @param className - The name of the class
	 * @param methodName - The name of the method
	 * @param args - The arguments to the method
	 * @return any return value the method has Note: Returns null if method could not be run
	 */
	public synchronized <T> T runMethod(String className, String methodName, Object... args) {
		Object o = objectMap.get(className);
		if (o == null) {
			JixelGame.getConsole().printErr("No class: " + className + " found", new ClassNotFoundException());
		}
		try {
			Method method = classMap.get(o).get(methodName);
			if (method == null) {
				JixelGame.getConsole().printErr("No method " + methodName + " found", new NoSuchMethodException());
				return null;
			}
			Type[] parameters = method.getParameterTypes();
			int paraAmount = parameters.length;
			if (args == null) {
				if (paraAmount == 0) {
					return (T) method.invoke(o);
				}
			} else {
				if (paraAmount == args.length) {
					for (int i = 0; i < args.length; i++) {
						args[i] = convertValue((Class<?>) parameters[i], (String) args[i]);
					}
					return (T) method.invoke(o, args);
				}
			}
			JixelGame.getConsole().printErr("Wrong number of arguments for " + methodName + " in " + className, new IllegalArgumentException());
		} catch (IllegalArgumentException e) {
			JixelGame.getConsole().printErr("Illegal arguments for " + methodName + " in " + className, new IllegalArgumentException());
		} catch (IllegalAccessException e) {
			JixelGame.getConsole().printErr("Illegal access to " + methodName + " in " + className, new IllegalArgumentException());
		} catch (InvocationTargetException e) {
			JixelGame.getConsole().printErr("The method " + methodName + " in " + className + " threw " + e.getCause().toString(), e);
		}
		return null;
	}

	/**
	 * Gets the value of a given variable in the VM
	 * @param name - The name of the variable
	 * @return the value of the variable
	 */
	public synchronized <T> T getValue(String name) {
		if (varMap.containsKey(name)) {
			Object o = varMap.get(name);
			return (T) o;
		} else {
			JixelGame.getConsole().print("No such variable with the name " + name + " exists.");
			return null;
		}
	}

	/**
	 * Converts a String to a wrapper of a primitive type
	 * @param clazz - The Object wrapper of a primitive type
	 * @param value - The string representation of the value
	 * @return the wrapper of the corresponding primitive type
	 */
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

	/**
	 * Sets the value of a given variable
	 * @param name - The name of the variable to set
	 * @param value - The string representation of its new value
	 * @return whether or not the value change was successful
	 */
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

	/**
	 * Sets the value of a given variable
	 * @param name - The name of the variable to set
	 * @param value - The new value of the variable
	 * @return whether or not the value change was successful
	 */
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

	/**
	 * Whether or not a given variable is in the VM
	 * @param name - The name of the variable
	 * @return whether or not it's in the vm
	 */
	public boolean containsVar(String name) {
		return varMap.containsKey(name);
	}

	/**
	 * Saves the state with the standard name with the given id
	 * @param id - The id of the save
	 * @return whether or not the save was successful
	 */
	public boolean save(int id) {
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
		return save(filepath);
	}

	/**
	 * Saves the state to a given file
	 * @param file - The file to save to
	 * @return whether or not the save was successful
	 */
	public boolean save(String file) {
		if (file == null) {
			return false;
		}
		File dir = new File(SAV_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				JixelGame.getConsole().printErr("Failed to create file:" + f.getPath(), e);
				return false;
			}
		}
		try (OutputStream out = new FileOutputStream(f); ObjectOutputStream oos = new ObjectOutputStream(out)) {
			synchronized (JixelGame.getUpdateLock()) {
				if (stateManager != null) {
					stateManager.saveState();
				}
				oos.writeObject(varMap);
			}
			return true;
		} catch (IOException e) {
			JixelGame.getConsole().printErr("IO Error on save of " + f.getPath(), e);
			return false;
		}
	}

	/**
	 * Loads a profile by its id Warning: Will clear all variables
	 * @param profileID
	 * @return whether the profile loaded correctly
	 */
	public boolean load(int id) {
		String filepath = String.format("%s%d%s", SAV_NAME, id, SAV_TYPE);
		return load(filepath);
	}

	/**
	 * Loads a file with a given name Warning: Will clear all variables
	 * @param file - File name in /profiles/
	 * @return whether the profile loaded correctly
	 */
	@SuppressWarnings("unchecked")
	public boolean load(String file) {
		if (file == null) {
			return false;
		}
		File dir = new File(SAV_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File f = new File(SAV_DIR + "\\" + file);
		if (!f.exists()) {
			JixelGame.getConsole().printErr("Failed to create file:" + f.getPath(), new FileNotFoundException());
			return false;
		}
		try (InputStream in = new FileInputStream(f); ObjectInputStream ois = new ObjectInputStream(in)) {
			synchronized (JixelGame.getUpdateLock()) {
				JixelGame.setPaused(true);
				varMap.clear();
				try {
					varMap = (Map<String, Object>) ois.readObject();
				} catch (ClassNotFoundException e) {
					ois.close();
					return false;
				}
				stateManager.runLoader();
			}
			return true;
		} catch (IOException e) {
			JixelGame.getConsole().printErr("IO Error on load of " + f.getPath(), e);
			return false;
		}
	}

	/**
	 * Sets a new state manager for save/load operations
	 * @param stateManager - The new state manager
	 */
	public void setStateManager(JixelStateManager stateManager) {
		if (stateManager != null) {
			this.stateManager = stateManager;
		} else {
			JixelGame.getConsole().printErr("Can not set stateManager to null", new NullPointerException());
		}
	}
}
