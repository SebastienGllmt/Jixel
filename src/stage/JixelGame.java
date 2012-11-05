package stage;

import java.awt.Canvas;

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
	public final String GAME_TITLE;
	
	public JixelGame(String title, int width, int height, int scale, int tileSize){
		GAME_TITLE = title;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.tileSize = tileSize;
		
		screen = new JixelScreen(width, height, tileSize);
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

}