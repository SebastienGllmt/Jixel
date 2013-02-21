package jixel.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

import jixel.stage.JixelGame;

class JixelKey {

	public boolean isHeld = false;
	public boolean newState = true;
}

public class JixelKeyInput implements KeyListener {

	private int maxLength;
	private int lastKeysLength = 16;

	private int breakKey = KeyEvent.VK_ENTER;
	private boolean reading = false;

	private Map<Integer, JixelKey> keyMap = new HashMap<Integer, JixelKey>();
	private Map<String, Integer> nameMap = new HashMap<String, Integer>();

	public JixelKeyInput() {
		addKey("Jixel_consoleKey", 192);
		JixelGame.getVM().newVar("Jixel_keyMsg", "");
		JixelGame.getVM().newVar("Jixel_lastKeys", "");
		JixelGame.getVM().newVar("Jixel_consoleMsg", "");
	}

	/**
	 * Makes JixelKeyInput listen for a new key Note: To override, simply re-add with a different keyCode
	 * @param name - The lookup name of that key
	 * @param keyCode - The key code of the key
	 */
	public void addKey(String name, int keyCode) {
		if (name != null) {
			nameMap.put(name, keyCode);
			keyMap.put(keyCode, new JixelKey());
		} else {
			JixelGame.getConsole().printErr(new NullPointerException("Can not have null name for key"));
		}
	}

	/**
	 * Makes JixelKeyInput listen for a new key Note: To override, simply re-add with a different char
	 * @param name - The lookup name of that key
	 * @param c - The char representation of the key
	 */
	public void addKey(String name, char c) {
		addKey(name, KeyEvent.getExtendedKeyCodeForChar(c));
	}

	/**
	 * Remove the listener for a given key
	 * @param name - The lookup name of the key
	 */
	public synchronized void removeKey(String name) {
		int keyCode = nameMap.get(name);
		keyMap.remove(keyCode);
		nameMap.remove(name);
	}

	/**
	 * Returns whether or not a given key is currently being held
	 * @param name - The lookup name of the key
	 * @return whether or not the key is down
	 */
	public synchronized boolean isKeyDown(String name) {
		if (name != null) {
			if (nameMap.containsKey(name)) {
				int keyCode = nameMap.get(name);
				if (keyMap.containsKey(keyCode)) {
					JixelKey key = keyMap.get(keyCode);
					if(key.isHeld){
						key.newState = false;
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * Returns whether or not a given key is toggled
	 * 		i.e. this will only be true once if the user keeps the key held
	 * @param name - The lookup name of the key
	 * @return whether or not the key is toggled
	 */
	public synchronized boolean isKeyToggled(String name) {
		if (name != null) {
			if (nameMap.containsKey(name)) {
				int keyCode = nameMap.get(name);
				if (keyMap.containsKey(keyCode)) {
					JixelKey key = keyMap.get(keyCode);
					if(key.isHeld){
						boolean returnValue = key.newState;
						key.newState = false;
						return returnValue;
					}
				}
			}
		}
		return false;
	}

	@Override
	public synchronized void keyPressed(KeyEvent evt) {
		if (keyMap.containsKey(evt.getKeyCode())) {
			keyMap.get(evt.getKeyCode()).isHeld = true;
			if ((int) nameMap.get("Jixel_consoleKey") == evt.getKeyCode()) {
				JixelGame.getConsole().setState(!JixelGame.getConsole().isRunning());
			}
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent evt) {
		if (keyMap.containsKey(evt.getKeyCode())) {
			keyMap.get(evt.getKeyCode()).isHeld = false;
			keyMap.get(evt.getKeyCode()).newState = true;
		}
	}

	@Override
	public synchronized void keyTyped(KeyEvent evt) {
		char lastKey = evt.getKeyChar();
		updateLastKeys(lastKey);
		if (!reading) {
			return;
		}

		if (lastKey == breakKey) {
			reading = false;
		} else {
			if ((lastKey < ' ' || lastKey > '~' || KeyEvent.getExtendedKeyCodeForChar(lastKey) == nameMap.get("Jixel_consoleKey")) && lastKey != 8) {
				return;
			}
			addInput(lastKey);
		}
	}

	/**
	 * Updates the string containing the last X keys pressed where X is lastKeysLength
	 * @param lastKey - The last key pressed to concat to the string
	 */
	private void updateLastKeys(char lastKey) {
		String prevString = JixelGame.getVM().getValue("Jixel_lastKeys");
		if (prevString.length() < lastKeysLength) {
			JixelGame.getVM().setValue("Jixel_lastKeys", prevString + lastKey);
		} else {
			JixelGame.getVM().setValue("Jixel_lastKeys", prevString.substring(1, lastKeysLength) + lastKey);
		}
	}

	/**
	 * Adds the last key pressed either to the console message or to the user message Will only have any effect if JixelKeyInput is reading
	 * @param lastKey - The last key pressed to concat to the string
	 */
	private void addInput(char lastKey) {
		if (isReading()) {
			String prevString;
			if (JixelGame.getConsole().isRunning()) {
				prevString = JixelGame.getVM().getValue("Jixel_consoleMsg");
			} else {
				prevString = JixelGame.getVM().getValue("Jixel_keyMsg");
			}
			if (lastKey != (char) 8) { //if key isn't backspace
				if (prevString.length() < maxLength) {
					if (JixelGame.getConsole().isRunning()) {
						JixelGame.getVM().setValue("Jixel_consoleMsg", prevString + lastKey);
					} else {
						JixelGame.getVM().setValue("Jixel_keyMsg", prevString + lastKey);
					}
				}
			} else {
				if (prevString.length() > 0) {
					String newMsg = prevString.substring(0, prevString.length() - 1);
					if (JixelGame.getConsole().isRunning()) {
						JixelGame.getVM().setValue("Jixel_consoleMsg", newMsg);
					} else {
						JixelGame.getVM().setValue("Jixel_keyMsg", newMsg);
					}
				}
			}
		}
	}

	/**
	 * Starts a console message which will break on enter key
	 * @param maxLength - The max length of the message
	 */
	public void startConsoleMsg(int maxLength) {
		setupMsg(KeyEvent.VK_ENTER, maxLength, true);
	}

	/**
	 * Starts a regular message which will break on the given key
	 * @param breakKey - The key on which keys will not longer be appended to the message
	 * @param maxLength - The max length of the message
	 */
	public void startMessage(int breakKey, int maxLength) {
		setupMsg(breakKey, maxLength, false);
	}

	/**
	 * Sets up the message internally
	 * @param breakKey - The break key to stop the message
	 * @param maxLength - The max length of the message
	 * @param isConsole - Whether or not the message is for the console
	 */
	private void setupMsg(int breakKey, int maxLength, boolean isConsole) {
		this.breakKey = breakKey;
		reading = true;
		this.maxLength = maxLength;
		clearMessage(isConsole);
	}

	/**
	 * Clears the message it's currently writing from the startMessage() method
	 * @param isConsole - Whether or not the message is for the console
	 */
	public void clearMessage(boolean isConsole) {
		if (isConsole) {
			JixelGame.getVM().setValue("Jixel_consoleMsg", "");
		} else {
			JixelGame.getVM().setValue("Jixel_keyMsg", "");
		}
	}

	/**
	 * Returns the last X keys pressed where X is lastKeysLength
	 * @return the last keys pressed
	 */
	public String lastKeys() {
		return JixelGame.getVM().getValue("Jixel_lastKeys");
	}

	/**
	 * @return whether or not JixelKeyInput is currently reading
	 */
	public boolean isReading() {
		return reading;
	}

	/**
	 * Stops JixelKeyInput from reading. Will not clear the message
	 */
	public void stopReading() {
		reading = false;
	}

	/**
	 * @return the current console message being stored
	 */
	public String getConsoleMsg() {
		return JixelGame.getVM().getValue("Jixel_consoleMsg");
	}

	/**
	 * @return the current message being stored
	 */
	public String getMessage() {
		return JixelGame.getVM().getValue("Jixel_keyMsg");
	}

	/**
	 * Sets a new length of the last keys string Note: Will trim the string if it currently stores more than the new maximum Note: Defaults at 16
	 */
	public void setLastKeysLength(int length) {
		if (length >= 0) {
			lastKeysLength = length;
			String currentMsg = JixelGame.getVM().getValue("Jixel_lastKeys");
			if (currentMsg.length() > length) {
				currentMsg = currentMsg.substring(0, length);
				JixelGame.getVM().setValue("Jixel_lastKeys", currentMsg);
			}
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not set lastKeysLength less than 0"));
		}
	}
}
