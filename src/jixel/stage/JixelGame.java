package jixel.stage;

import jixel.console.JixelConsole;
import jixel.console.JixelVariableManager;
import jixel.entity.JixelEntityManager;
import jixel.gui.JixelCamera;
import jixel.gui.JixelScreen;
import jixel.input.JixelKeyInput;
import jixel.input.JixelMouseInput;

public abstract class JixelGame implements Runnable {

	private static JixelVariableManager vm;
	private static JixelConsole con;
	private static JixelKeyInput keyInput;
	private static JixelMouseInput mouseInput;
	private static JixelTimer timer;

	private static JixelEditorScreen editorScreen;
	private static JixelGameScreen gameScreen;
	private static JixelScreen screen;

	private static JixelEntityManager entities;

	public static boolean playing = true;
	public final String GAME_TITLE;
	private static boolean paused = false;

	private final static Object updateLock = new Object();

	private static Thread thread; // thread for the update/render

	public JixelGame(String title, int width, int height, int scale, int tileSize, int fps) {
		GAME_TITLE = title;

		vm = new JixelVariableManager();
		entities = new JixelEntityManager();
		getVM().newVar("Jixel_lockedEntity", null);
		getVM().newVar("Jixel_entityList", entities.getList());

		editorScreen = new JixelEditorScreen(0, 0, width, height);
		gameScreen = new JixelGameScreen(0, 0, width, height);

		screen = new JixelScreen(title, gameScreen, width, height, scale, tileSize);
		timer = new JixelTimer();
		timer.setFPS(fps);

		con = new JixelConsole();

		keyInput = new JixelKeyInput();
		mouseInput = new JixelMouseInput();

		getScreen().addKeyListener(keyInput);
		getScreen().addMouseListener(mouseInput);

		start();
	}

	public String getTitle() {
		return GAME_TITLE;
	}

	public static JixelScreen getScreen() {
		return screen;
	}

	public static JixelCamera getCamera() {
		return getScreen().getCamera();
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

	public static JixelEntityManager getEntityManager() {
		return entities;
	}

	public abstract void update();

	public static Object getUpdateLock() {
		return updateLock;
	}

	@Override
	public void run() {
		while (playing) {
			synchronized (getUpdateLock()) {
				getTimer().updateTime();
				if (getTimer().timeForFrame()) {
					if (!getPaused()) {
						getKeyInput().updateKeyboard();
						update();
						getCamera().getEntityManager().update();
					}
					getScreen().clear();
					getScreen().render();
				}
			}
		}
		synchronized (getUpdateLock()) {
			stop();
		}
	}

	private synchronized void start() {
		thread = new Thread(this, "Jixel Main");
		thread.start();
	}

	public abstract void closeOperation();

	public void closeGame() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				closeOperation();
			}
		});
		playing = false;
	}

	private synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Failed to stop JixelGame thread");
		}
	}

}