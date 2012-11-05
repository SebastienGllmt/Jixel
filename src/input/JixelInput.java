package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import stage.JixelGame;

public class JixelInput implements KeyListener {

	private boolean[] keys = new boolean[KeyEvent.KEY_LAST];
	public boolean up, down, left, right, enter, z, x;
	public final char ENTER = KeyEvent.VK_ENTER;

	private int maxLength;
	private final int LAST_KEYS_LENGTH = 16;

	private char breakKey = KeyEvent.VK_ENTER;
	public char lastKey = 0;
	private boolean reading = false;

	public JixelInput() {
		JixelGame.vm.newVar("Jixel_keyMsg", "");
		JixelGame.vm.newVar("Jixel_lastKeys", "");
	}

	public void updateKeyboard() {
		z = keys[KeyEvent.VK_Z];
		x = keys[KeyEvent.VK_X];
		enter = keys[KeyEvent.VK_ENTER];
		up = keys[KeyEvent.VK_UP];
		down = keys[KeyEvent.VK_DOWN];
		left = keys[KeyEvent.VK_LEFT];
		right = keys[KeyEvent.VK_RIGHT];
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		keys[evt.getKeyCode()] = true;
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
			if((lastKey < 32 || lastKey > 127)){
				return;
			}
			addInput();
		}
	}
	
	private void updateLastKeys(){
		String prevString = JixelGame.vm.getValue("Jixel_lastKeys");
		if(prevString.length() < LAST_KEYS_LENGTH){
			JixelGame.vm.setValue("Jixel_lastKeys", prevString + lastKey);
		}else{
			JixelGame.vm.setValue("Jixel_lastKeys", prevString.substring(1, LAST_KEYS_LENGTH-1) + lastKey);
		}
	}
	
	private void addInput(){
		String prevString = JixelGame.vm.getValue("Jixel_keyMsg");
		if (lastKey != (char) 8) {
			if (prevString.length() < maxLength) {
				JixelGame.vm.setValue("Jixel_keyMsg", prevString + lastKey);
			}
		} else {
			if (prevString.length() > 0) {
				JixelGame.vm.setValue("Jixel_keyMsg", prevString.substring(0, prevString.length() - 1));
			}
		}
	}

	public void startMessage(char breakKey, int maxLength) {
		this.breakKey = breakKey;
		reading = true;
		this.maxLength = maxLength;
		JixelGame.vm.setValue("Jixel_keyMsg", "");
	}

	public String lastKeys(){
		return JixelGame.vm.getValue("Jixel_lastKeys");
	}
	
	public boolean isReading() {
		return reading;
	}

	public String getMessage() {
		return JixelGame.vm.getValue("Jixel_keyMsg");
	}

}
