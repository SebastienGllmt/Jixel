package stage;

import java.awt.Canvas;

import console.Console;
import console.VariableManager;
import gui.Screen;

@SuppressWarnings("serial")
public class JixelGame extends Canvas{

	public static VariableManager vm = new VariableManager();
	private Console con = new Console(vm);
	
	public int width;
	public int height;
	public int scale;
	public int tileSize;
	
	private Screen screen;
	public final String GAME_TITLE;
	
	public JixelGame(String title, int width, int height, int scale, int tileSize){
		GAME_TITLE = title;
		this.width = width;
		this.height = height;
		this.scale = scale;
		this.tileSize = tileSize;
		
		screen = new Screen(width, height, tileSize);
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
	public Screen getScreen(){
		return screen;
	}
	public Console getConsole(){
		return con;
	}

}