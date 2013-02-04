package jixel.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jixel.stage.JixelGame;


public class JixelKeyInput implements KeyListener {

	private boolean[] keys = new boolean[525];
	public boolean up=false, down=false, left=false, right=false, enter=false;
	public boolean key1=false, key2=false, key3=false, consoleKey=false;

	private int maxLength;
	private final int LAST_KEYS_LENGTH = 16;

	private int breakKey = KeyEvent.VK_ENTER;
	private int consoleKeyCode = 192;
	private char consoleChar = '`';
	private char lastKey = 0;
	private boolean reading = false;

	public JixelKeyInput() {
		JixelGame.getVM().newVar("Jixel_upKey", KeyEvent.VK_UP);
		JixelGame.getVM().newVar("Jixel_downKey", KeyEvent.VK_DOWN);
		JixelGame.getVM().newVar("Jixel_leftKey", KeyEvent.VK_LEFT);
		JixelGame.getVM().newVar("Jixel_rightKey", KeyEvent.VK_RIGHT);
		JixelGame.getVM().newVar("Jixel_enterKey", KeyEvent.VK_ENTER);
		JixelGame.getVM().newVar("Jixel_key1", KeyEvent.VK_Z);
		JixelGame.getVM().newVar("Jixel_key2", KeyEvent.VK_X);
		JixelGame.getVM().newVar("Jixel_key3", KeyEvent.VK_A);
		JixelGame.getVM().newVar("Jixel_keyMsg", "");
		JixelGame.getVM().newVar("Jixel_lastKeys", "");
		JixelGame.getVM().newVar("Jixel_consoleMsg", "");
	}

	public void updateKeyboard() {
		up = keys[JixelGame.getVM().getValue("Jixel_upKey")];
		down = keys[JixelGame.getVM().getValue("Jixel_downKey")];
		left = keys[JixelGame.getVM().getValue("Jixel_leftKey")];
		right = keys[JixelGame.getVM().getValue("Jixel_rightKey")];
		key1 = keys[JixelGame.getVM().getValue("Jixel_key1")];
		key2 = keys[JixelGame.getVM().getValue("Jixel_key2")];
		key3 =  keys[JixelGame.getVM().getValue("Jixel_key3")];
		enter = keys[JixelGame.getVM().getValue("Jixel_enterKey")];
		consoleKey = keys[consoleKeyCode];
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		keys[evt.getKeyCode()] = true;
		if(consoleKeyCode == evt.getKeyCode()){
			JixelGame.getConsole().setState(!JixelGame.getConsole().isRunning());
		}
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		keys[evt.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		lastKey = evt.getKeyChar();
		updateLastKeys();
		if (!reading) {
			return;
		}
		if (lastKey == breakKey) {
			reading = false;
		} else {
			if ((lastKey < 32 || lastKey > 127 || lastKey == consoleChar) && lastKey != 8) {
				return;
			}
			addInput();
		}
	}

	private void updateLastKeys() {
		String prevString = JixelGame.getVM().getValue("Jixel_lastKeys");
		if (prevString.length() < LAST_KEYS_LENGTH) {
			JixelGame.getVM().setValue("Jixel_lastKeys", prevString + lastKey);
		} else {
			JixelGame.getVM().setValue("Jixel_lastKeys", prevString.substring(1, LAST_KEYS_LENGTH) + lastKey);
		}
	}

	private void addInput() {
		String prevString;
		if(JixelGame.getConsole().isRunning()){
			prevString = JixelGame.getVM().getValue("Jixel_consoleMsg");
		}else{
			prevString = JixelGame.getVM().getValue("Jixel_keyMsg");
		}
		if (lastKey != (char) 8) {
			if (prevString.length() < maxLength) {
				if(JixelGame.getConsole().isRunning()){
					JixelGame.getVM().setValue("Jixel_consoleMsg", prevString + lastKey);
				}else{
					JixelGame.getVM().setValue("Jixel_keyMsg", prevString + lastKey);
				}
			}
		} else {
			if (prevString.length() > 0) {
				String newMsg = prevString.substring(0, prevString.length() - 1);
				if(JixelGame.getConsole().isRunning()){
					JixelGame.getVM().setValue("Jixel_consoleMsg", newMsg);
				}else{
					JixelGame.getVM().setValue("Jixel_keyMsg", newMsg);
				}
			}
		}
	}

	public void startConsoleMsg(int maxLength){
		setupMsg(KeyEvent.VK_ENTER, maxLength, true);
	}
	
	public void startMessage(int breakKey, int maxLength) {
		setupMsg(breakKey, maxLength, false);
	}
	
	private void setupMsg(int breakKey, int maxLength, boolean isConsole){
		this.breakKey = breakKey;
		reading = true;
		this.maxLength = maxLength;
		clearMessage(isConsole);
	}
	
	public void clearMessage(boolean isConsole){
		if(isConsole){
			JixelGame.getVM().setValue("Jixel_consoleMsg", "");
		}else{
			JixelGame.getVM().setValue("Jixel_keyMsg", "");
		}
	}

	public String lastKeys() {
		return JixelGame.getVM().getValue("Jixel_lastKeys");
	}

	public boolean isReading() {
		return reading;
	}

	public String getConsoleMsg(){
		return JixelGame.getVM().getValue("Jixel_consoleMsg");
	}
	public String getMessage() {
		return JixelGame.getVM().getValue("Jixel_keyMsg");
	}

}
