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
public class JixelGame extends Canvas{

	private JixelVariableManager vm;
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
	
	private long timer;
	private long lastTime;
	private double ns;
	private double delta;
	private int frames;
	private int updates;
	long now;
	
	private JixelInput input;
	
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
		
		vm = new JixelVariableManager(this);
		
		input = new JixelInput(this);
		con = new JixelConsole(this);
		
		vm.newVar("Jixel_paused", false);
		 
		screen = new JixelScreen(width, height, tileSize);
		createBufferStrategy(3);
		bs = getBufferStrategy();
		
		addKeyListener(input);
	}
	
	public void startTime(double ups){
		ns = 1000000000.0 / ups;
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
	public JixelConsole getConsole(){
		return con;
	}
	public synchronized JixelVariableManager getVM(){
		return vm;
	}
	public boolean getPaused(){
		return vm.getValue("Jixel_paused");
	}
	public BufferStrategy getBuffer(){
		return bs;
	}
	public synchronized JixelInput keys(){
		return input;
	}

}