package stage;

import input.JixelInput;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import console.JixelConsole;
import console.JixelVariableManager;
import gui.JixelScreen;

@SuppressWarnings("serial")
public class JixelGame extends Canvas {

	private static JixelVariableManager vm;
	private JixelConsole con;

	public boolean playing = true;
	public int width;
	public int height;
	public int scale;
	public int tileSize;
	public final String GAME_TITLE;

	private JixelScreen screen;
	private JFrame frame;
	private BufferStrategy bs;

	private double fpsNS;
	private double upsNS;
	private long timer;
	private long lastTime;
	private long now;
	private double deltaUps;
	private double deltaFps;
	private int frames;
	private int updates;
	
	private JixelInput input;

	public JixelGame(String title, int width, int height, int scale, int tileSize) {
		GAME_TITLE = title;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.tileSize = tileSize;

		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);

		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle(title);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		vm = new JixelVariableManager(this);
		input = new JixelInput(this);

		con = new JixelConsole(this);

		vm.newVar("Jixel_paused", false);

		screen = new JixelScreen(this, width, height, tileSize);
		createBufferStrategy(3);
		bs = getBufferStrategy();

		addKeyListener(input);
	}

	public void startTime(double fps, double ups) {
		fpsNS = 1000000000.0 / fps;
		upsNS = 1000000000.0 / ups;

		timer = System.currentTimeMillis();
		lastTime = System.nanoTime();

		deltaUps = 0;
		deltaFps = 0;
		frames = 0;
		updates = 0;

		now = System.nanoTime();
	}

	public void updateTime() {

		if (System.currentTimeMillis() - timer > 1000) {
			timer += 1000;
			frame.setTitle(GAME_TITLE + " Ups: " + updates + " , Fps: " + frames);
			updates = 0;
			frames = 0;
		}

		now = System.nanoTime();
		deltaFps += (now - lastTime) / fpsNS;
		deltaUps += (now - lastTime) / upsNS;
		lastTime = now;
	}

	public boolean timeForFrame() {
		if (deltaFps >= 1) {
			frames++;
			deltaFps--;
			return true;
		} else {
			return false;
		}
	}
	public boolean timeForUpdate() {
		if (deltaUps >= 1) {
			updates++;
			deltaUps--;
			return true;
		} else {
			return false;
		}
	}

	public String getTitle() {
		return GAME_TITLE;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getScale() {
		return scale;
	}

	public int getTileSize() {
		return tileSize;
	}

	public synchronized JixelScreen getScreen() {
		return screen;
	}

	public synchronized JixelConsole getConsole() {
		return con;
	}

	public static synchronized JixelVariableManager getVM() {
		return vm;
	}

	public boolean getPaused() {
		return getVM().getValue("Jixel_paused");
	}

	public BufferStrategy getBuffer() {
		return bs;
	}

	public synchronized JixelInput getInput() {
		return input;
	}

}