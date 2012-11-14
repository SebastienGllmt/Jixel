package stage;

import input.JixelInput;

import console.JixelConsole;
import console.JixelVariableManager;
import gui.JixelScreen;

public class JixelGame {

	private static JixelVariableManager vm;
	private static JixelConsole con;
	private static JixelScreen screen;
	private static JixelInput input;
	private static JixelTimer timer;

	public boolean playing = true;
	public final String GAME_TITLE;

	public JixelGame(String title, int width, int height, int scale, int tileSize) {
		GAME_TITLE = title;

		vm = new JixelVariableManager();
		vm.newVar("Jixel_paused", false);
		
		screen = new JixelScreen(title, width, height, scale, tileSize);
		timer = new JixelTimer(title);
		
		con = new JixelConsole();
		
		input = new JixelInput();
		screen.addKeyListener(input);
	}

	public String getTitle() {
		return GAME_TITLE;
	}

	public static synchronized JixelScreen getScreen() {
		return screen;
	}

	public static synchronized JixelConsole getConsole() {
		return con;
	}

	public static synchronized JixelVariableManager getVM() {
		return vm;
	}

	public boolean getPaused() {
		return getVM().getValue("Jixel_paused");
	}

	public static synchronized JixelInput getInput() {
		return input;
	}
	
	public static JixelTimer getTimer(){
		return timer;
	}

}