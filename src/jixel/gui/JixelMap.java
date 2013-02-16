package jixel.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jixel.stage.JixelGame;

public class JixelMap {

	private int width, height;
	private JixelTile[] tiles;
	private boolean holdsLevel = false;
	private JixelSprite spriteSheet;

	/**
	 * Clears the map data
	 */
	public void clearMapData(){
		width = 0;
		height = 0;
		tiles = null;
		holdsLevel = false;
	}
	
	/**
	 * Sets the sprite sheet for the map
	 * @param sprite - The sprite sheet
	 */
	public void setSpriteSheet(JixelSprite sprite){
		this.spriteSheet = sprite;
	}

	/**
	 * Returns an integer representation of a given length of bytes
	 * @param in - The input stream to read from
	 * @param length - The number of bytes to read
	 * @return an integer representation of these bytes
	 * @throws IOException - handled by the load level method
	 */
	private int getValue(InputStream in, int length) throws IOException{
		byte[] byteArray = new byte[length];
		for(int i=0; i<length; i++){
			byteArray[i] = (byte) in.read();
		}
		int result = 0;
		for(int i=0; i<length; i++){
			result += byteArray[i]<<(i<<3);
		}
		return result;
	}
	public void loadLevel(String path) {
		File f = new File(path);
		try(InputStream in = new FileInputStream(f)) {
			width = getValue(in, 4);
			height = getValue(in, 4);
			tiles = new JixelTile[width*height];
			for(int y=0; y<height; y++){
				for(int x=0; x<width; x++){
					tiles[x+y*width] = new JixelTile(getValue(in, 2));
				}
			}
			holdsLevel = true;
		} catch (FileNotFoundException e) {
			JixelGame.getConsole().printErr("Map not found at: " + f.getPath(), e);
		} catch (Exception e){
			JixelGame.getConsole().printErr("Failed to load tile sheet at " + f.getPath(), e);
		}
	}
	
	/**
	 * Returns the tileID at the given coordinates
	 * @param x - x coordinate in the map
	 * @param y - y coordinate in the map
	 * @return the tile ID
	 */
	public int getTile(int x, int y){
		if(x < 0 || x >= width || y < 0 || y >= height){
			return -1;
		}
		return tiles[x+y*width].getTileID();
	}
	
	/**
	 * @return the underlying sprite sheet for the sprite
	 */
	public JixelSprite getSpriteSheet(){
		return spriteSheet;
	}
	
	/**
	 * @return whether or not the map is loadable
	 */
	public boolean canLoad(){
		return (spriteSheet != null && holdsLevel);
	}

	/**
	 * @return How many tiles there are horizontally
	 */
	public int getWidth(){
		return width;
	}
	/**
	 * @return How many tiles there are vertically
	 */
	public int getHeight(){
		return height;
	}
}
