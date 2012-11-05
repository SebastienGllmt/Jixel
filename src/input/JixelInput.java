package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JixelInput implements KeyListener {

	private boolean[] keys = new boolean[KeyEvent.KEY_LAST];
	public boolean up, down, left, right;
	
	public void updateKeyboard(){
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
		
	}
	
}
