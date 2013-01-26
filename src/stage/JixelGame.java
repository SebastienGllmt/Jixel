package stage;

import input.JixelInput;

import console.JixelConsole;
import console.JixelVariableManager;
import entity.JixelEntityManager;
import gui.JixelScreen;

public abstract class JixelGame implements Runnable{

	private static JixelVariableManager vm;
	private static JixelConsole con;
	private static JixelScreen screen;
	private static JixelInput input;
	private static JixelTimer timer;
	
	private static JixelEntityManager entities = new JixelEntityManager();

	public boolean playing = true;
	public final String GAME_TITLE;
	
	private final int fps, ups;

	Thread thread; //thread for the update/render
	
	public JixelGame(String title, int width, int height, int scale, int tileSize, int fps, int ups) {
		GAME_TITLE = title;
		this.fps = fps;
		this.ups = ups;

		vm = new JixelVariableManager();
		vm.newVar("Jixel_paused", false);
		
		screen = new JixelScreen(title, width, height, scale, tileSize);
		timer = new JixelTimer(title);
		
		con = new JixelConsole();
		
		input = new JixelInput();
		screen.addKeyListener(input);
		
		start();
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
	
	public JixelTimer getTimer(){
		return timer;
	}

	public static JixelEntityManager getEntityList(){
		return entities;
	}
	
	
	public abstract void update(); //abstract update method for user to override
	
	@Override
	public void run(){
		getTimer().startTimer(fps, ups);
		while (playing) {
			while (!getPaused()) {
				getTimer().updateTime();
				if (getTimer().timeForUpdate() && !getConsole().isRunning()) {
					getInput().updateKeyboard();
					update();
					getEntityList().update();
				}
				if (getTimer().timeForFrame()) {
					getScreen().clear();
					getScreen().drawMap();
					getScreen().drawEntities();
				}
			}
		}
		stop();
	}
	
	public synchronized void start() {
		thread = new Thread(this, "GUI");
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}