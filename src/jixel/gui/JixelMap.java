package jixel.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jixel.stage.JixelGame;

public class JixelMap {

	private int width, height;
	private JixelTile[] tiles;
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
		try(InputStream in = new FileInputStream(f)) {
			width = in.read();
			height = in.read();
			tiles = new JixelTile[width*height];
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					tiles[x+y*width] = new JixelTile(in.read());
				}
			}
			holdsLevel = true;
		} catch (IOException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Map not found");
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
		return tiles[x+y*width].getTileID();
	}
}
