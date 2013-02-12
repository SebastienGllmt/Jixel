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

	public JixelGame(String title, int width, int height, int scale, int tileSizeLog2, int fps) {
		GAME_TITLE = title;

		vm = new JixelVariableManager();
		entities = new JixelEntityManager();
		getVM().newVar("Jixel_lockedEntity", null);
		getVM().newVar("Jixel_entityList", entities.getList());

		editorScreen = new JixelEditorScreen(0, 0, width, height);
		gameScreen = new JixelGameScreen(0, 0, width, height);

		screen = new JixelScreen(title, gameScreen, width, height, scale, tileSizeLog2);
		timer = new JixelTimer();
		timer.setFPS(fps);

		con = new JixelConsole();

		keyInput = new JixelKeyInput();
		mouseInput = new JixelMouseInput();

		getScreen().addKeyListener(keyInput);
		getScreen().addMouseListener(mouseInput);

		//Adds a user-specified shutdown hook for the game
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				closeOperation();
			}
		});
		
		start();
	}

	/**
	 * @return the title of the game
	 */
	public String getTitle() {
		return GAME_TITLE;
	}

	/**
	 * @return the main screen object for the engine
	 */
	public static JixelScreen getScreen() {
		return screen;
	}

	/**
	 * @return the current camera for the screen
	 */
	public static JixelCamera getCamera() {
		return getScreen().getCamera();
	}

	/**
	 * @return the console for the engine
	 */
	public static JixelConsole getConsole() {
		return con;
	}

	/**
	 * @return the main variable manager for the engine
	 */
	public static JixelVariableManager getVM() {
		return vm;
	}

	/**
	 * @return whether or not the game is paused
	 */
	public static boolean getPaused() {
		return paused;
	}

	/**
	 * Pauses or unpauses the game
	 * @param newState
	 */
	public static void setPaused(boolean newState) {
		paused = newState;
	}

	/**
	 * @return the key input object
	 */
	public static JixelKeyInput getKeyInput() {
		return keyInput;
	}

	/**
	 * @return the mouse input object
	 */
	public static JixelMouseInput getMouseInput() {
		return mouseInput;
	}

	/**
	 * @return the timer that keeps track of fps
	 */
	public static JixelTimer getTimer() {
		return timer;
	}

	/**
	 * @return the main entity manager for the engine
	 */
	public static JixelEntityManager getEntityManager() {
		return entities;
	}

	/**
	 * The abstract method to run user code every frame
	 */
	public abstract void update();

	/**
	 * @return the lock for updating the game
	 */
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
					getScreen().render();
				}
			}
		}
		synchronized (getUpdateLock()) {
			stop();
		}
	}

	/**
	 * Starts the main thread
	 */
	private synchronized void start() {
		thread = new Thread(this, "Jixel Main");
		thread.start();
	}

	/**
	 * Abstract method the user can use to decide what happens when the game closes
	 */
	public abstract void closeOperation();

	/**
	 * Closes the game
	 */
	public void closeGame() {
		playing = false;
	}

	/**
	 * Joins the main thread
	 */
	private synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Failed to stop JixelGame thread");
		}
	}

}