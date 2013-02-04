package jixel.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jixel.stage.JixelGame;


public class JixelMap {

	private int width, height;
	private int[] tiles;
	private boolean holdsLevel = false;
	private JixelSprite spriteSheet;
	
	public void clearMapData(){
		width = 0;
		height = 0;
		tiles = null;
		holdsLevel = false;
	}
	
	public void setSpriteSheet(JixelSprite sprite){
		this.spriteSheet = sprite;
	}

	public void loadLevel(String path) {
		File f = new File(path);
		InputStream in;
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e1) {
			JixelGame.getConsole().print("Map not found");
			return;
		}
		try {
			width = in.read();
			height = in.read();
			tiles = new int[width*height];
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					tiles[x+y*width] = in.read();
				}
			}
			holdsLevel = true;
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public JixelSprite getSpriteSheet(){
		return spriteSheet;
	}
	
	public boolean canLoad(){
		return (spriteSheet != null && holdsLevel);
	}

	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public int getTile(int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}
		return tiles[x+y*width];
	}
}
