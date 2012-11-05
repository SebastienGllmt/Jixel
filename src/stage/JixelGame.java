package stage;

import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

import console.JixelConsole;
import console.JixelVariableManager;
import gui.JixelScreen;

@SuppressWarnings("serial")
public class JixelGame extends Canvas{

	public static JixelVariableManager vm = new JixelVariableManager();
	private static JixelConsole con = new JixelConsole(vm);
	
	public int width;
	public int height;
	public int scale;
	public int tileSize;
	
	private JixelScreen screen;
	private JFrame frame;
	public final String GAME_TITLE;
	
	private long timer;
	private long lastTime;
	private double ns;
	private double delta;
	private int frames;
	private int updates;
	long now;
	
	public JixelGame(String title, int width, int height, int scale, int tileSize){
		GAME_TITLE = title;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.tileSize = tileSize;
		
		Dimension size = new Dimension(width*scale, height*scale);
		setPreferredSize(size);
		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle(title);
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		vm.newVar("paused", false);
		screen = new JixelScreen(width, height, tileSize);
	}
	
	public void startTime(double fps){
		ns = 1000000000.0 / fps;
		timer = System.currentTimeMillis();
		lastTime = System.nanoTime();
		delta = 0;
		frames = 0;
		updates = 0;
		now = System.nanoTime();
	}
	public void updateTime(){
		frames++;
		
		if(System.currentTimeMillis() - timer > 1000){
			timer += 1000;
			frame.setTitle(GAME_TITLE + " Ups: " + updates + " , Fps: " + frames);
			updates=0;
			frames=0;
		}
		
		now = System.nanoTime();
		delta += (now - lastTime) / ns;
		lastTime = now;
	}
	public boolean timeToUpdate(){
		if(delta >= 1){
			updates++;
			delta--;
			return true;
		}else{
			return false;
		}
	}
	
	public String getTitle(){
		return GAME_TITLE;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getScale(){
		return scale;
	}
	public int getTileSize(){
		return tileSize;
	}
	public JixelScreen getScreen(){
		return screen;
	}
	public static JixelConsole getConsole(){
		return con;
	}
	public boolean getPaused(){
		return vm.getValue("paused");
	}

}