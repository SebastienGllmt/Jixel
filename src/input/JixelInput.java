package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import stage.JixelGame;

public class JixelInput implements KeyListener {

	private boolean[] keys = new boolean[KeyEvent.KEY_LAST];
	public boolean up=false, down=false, left=false, right=false, enter=false;
	public boolean key1=false, key2=false, key3=false, consoleKey=false;

	private int maxLength;
	private final int LAST_KEYS_LENGTH = 16;

	private int breakKey = KeyEvent.VK_ENTER;
	private char lastKey = 0;
	private boolean reading = false;
	
	private JixelGame game;

	public JixelInput(JixelGame game) {
		this.game = game;
		game.getVM().newVar("Jixel_upKey", KeyEvent.VK_UP);
		game.getVM().newVar("Jixel_downKey", KeyEvent.VK_DOWN);
		game.getVM().newVar("Jixel_leftKey", KeyEvent.VK_LEFT);
		game.getVM().newVar("Jixel_rightKey", KeyEvent.VK_RIGHT);
		game.getVM().newVar("Jixel_enterKey", KeyEvent.VK_ENTER);
		game.getVM().newVar("Jixel_key1", KeyEvent.VK_Z);
		game.getVM().newVar("Jixel_key2", KeyEvent.VK_X);
		game.getVM().newVar("Jixel_key3", KeyEvent.VK_A);
		game.getVM().newVar("Jixel_consoleKey", 192);
		game.getVM().newVar("Jixel_keyMsg", "");
		game.getVM().newVar("Jixel_lastKeys", "");
	}

	public void updateKeyboard() {
		up = keys[game.getVM().getValue("Jixel_upKey")];
		down = keys[game.getVM().getValue("Jixel_downKey")];
		left = keys[game.getVM().getValue("Jixel_leftKey")];
		right = keys[game.getVM().getValue("Jixel_rightKey")];
		key1 = keys[game.getVM().getValue("Jixel_key1")];
		key2 = keys[game.getVM().getValue("Jixel_key2")];
		key3 =  keys[game.getVM().getValue("Jixel_key3")];
		enter = keys[game.getVM().getValue("Jixel_enterKey")];
		consoleKey = keys[game.getVM().getValue("Jixel_consoleKey")];
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		keys[evt.getKeyCode()] = true;
		int consoleKey = game.getVM().getValue("Jixel_consoleKey");
		if(consoleKey == evt.getKeyCode()){
			game.getConsole().setState(!game.getConsole().isRunning());
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
			if ((lastKey < 32 || lastKey > 127)) {
				return;
			}
			addInput();
		}
	}

	private void updateLastKeys() {
		String prevString = game.getVM().getValue("Jixel_lastKeys");
		if (prevString.length() < LAST_KEYS_LENGTH) {
			game.getVM().setValue("Jixel_lastKeys", prevString + lastKey);
		} else {
			game.getVM().setValue("Jixel_lastKeys", prevString.substring(1, LAST_KEYS_LENGTH - 1) + lastKey);
		}
	}

	private void addInput() {
		String prevString = game.getVM().getValue("Jixel_keyMsg");
		if (lastKey != (char) 8) {
			if (prevString.length() < maxLength) {
				game.getVM().setValue("Jixel_keyMsg", prevString + lastKey);
			}
		} else {
			if (prevString.length() > 0) {
				game.getVM().setValue("Jixel_keyMsg", prevString.substring(0, prevString.length() - 1));
			}
		}
	}

	public void startMessage(int breakKey, int maxLength) {
		this.breakKey = breakKey;
		reading = true;
		this.maxLength = maxLength;
		game.getVM().setValue("Jixel_keyMsg", "");
	}

	public String lastKeys() {
		return game.getVM().getValue("Jixel_lastKeys");
	}

	public boolean isReading() {
		return reading;
	}

	public String getMessage() {
		return game.getVM().getValue("Jixel_keyMsg");
	}

}
