package jixel.stage;

import jixel.console.JixelConsole;
import jixel.console.JixelVariableManager;
import jixel.entity.JixelEntityManager;
import jixel.gui.JixelCamera;
import jixel.gui.JixelMap;
import jixel.gui.JixelScreen;
import jixel.input.JixelKeyInput;
import jixel.input.JixelMouseInput;

public abstract class JixelGame implements Runnable {

	private static JixelVariableManager vm;
	private static JixelConsole con;
	private static JixelKeyInput keyInput;
	private static JixelMouseInput mouseInput;
	private static JixelTimer timer;
	private static JixelMap map;

	private static JixelEditorScreen editorScreen;
	private static JixelGameScreen gameScreen;
	private static JixelScreen screen;

	private static JixelEntityManager entities = new JixelEntityManager();

	public boolean playing = true;
	public final String GAME_TITLE;
	private static boolean paused = false;

	private final int fps;

	private final static Object updateLock = new Object();

	private static Thread thread; // thread for the update/render

	public JixelGame(String title, int width, int height, int scale, int tileSize, int fps) {
		GAME_TITLE = title;
		this.fps = fps;

		vm = new JixelVariableManager();
		getVM().newVar("Jixel_lockedEntity", null);
		getVM().newVar("Jixel_entityList", entities.getList());

		screen = new JixelScreen(title, width, height, scale, tileSize);
		timer = new JixelTimer(title);

		con = new JixelConsole();

		keyInput = new JixelKeyInput();
		mouseInput = new JixelMouseInput();

		editorScreen = new JixelEditorScreen(0, 0, width, height);
		gameScreen = new JixelGameScreen(0, 0, width, height);
		
		getScreen().attachCamera(gameScreen);
		getScreen().addKeyListener(keyInput);
		getScreen().addMouseListener(mouseInput);
		map = new JixelMap();

		start();
	}

	public String getTitle() {
		return GAME_TITLE;
	}

	public static JixelMap getMap() {
		return map;
	}

	public static JixelScreen getScreen() {
		return screen;
	}

	public static JixelConsole getConsole() {
		return con;
	}

	public static JixelVariableManager getVM() {
		return vm;
	}

	public static boolean getPaused() {
		return paused;
	}

	public static void setPaused(boolean newState) {
		paused = newState;
	}

	public static JixelKeyInput getKeyInput() {
		return keyInput;
	}

	public static JixelMouseInput getMouseInput() {
		return mouseInput;
	}

	public static JixelTimer getTimer() {
		return timer;
	}

	public static JixelEntityManager getEntityList() {
		return entities;
	}

	public abstract void update();

	public static Object getUpdateLock() {
		return updateLock;
	}

	public void attachCamera(JixelCamera camera) {
		synchronized (getUpdateLock()) {
			getScreen().attachCamera(camera);
		}
	}

	public abstract void loadState();

	@Override
	public void run() {
		getTimer().startTimer(fps);
		while (playing) {
			synchronized (getUpdateLock()) {
				getTimer().updateTime();
				if (getTimer().timeForFrame()) {
					if (!getPaused()) {
						getKeyInput().updateKeyboard();
						update();
						getEntityList().update();
					}
					getScreen().clear();
					if (getMap().canLoad()) {
						getScreen().drawMap();
					}
					getScreen().drawEntities();
				}
			}
		}
		stop();
	}

	public synchronized void start() {
		thread = new Thread(this, "Jixel Main");
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