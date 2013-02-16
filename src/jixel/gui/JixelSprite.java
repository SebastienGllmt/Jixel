package jixel.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jixel.stage.JixelGame;


public class JixelSprite {

	private final String PATH;
	private int width, height;
	
	private transient BufferedImage img;
	private int sheetWidth, sheetHeight;
	private int[] sheetPixels;
	private int currentTile;
	
	private boolean flipH=false, flipV=false;

	/**
	 * Constructor for JixelSprite
	 * @param PATH - Location of the sprite sheet
	 */
	public JixelSprite(final String PATH){
		this.PATH = PATH;
		this.width = JixelGame.getScreen().getTileSize();
		this.height = JixelGame.getScreen().getTileSize();
		loadSheet();
	}
	
	/**
	 * Constructor for JixelSprite
	 * @param PATH - Location of the sprite sheet
	 * @param width - Width of a given sprite
	 * @param height - Height of a given sprite
	 */
	public JixelSprite(final String PATH, int width, int height){
		this.PATH = PATH;
		this.width = width;
		this.height = height;
		loadSheet();
	}
	
	/**
	 * Returns the pixel for the given tile/coordinates
	 * @param tileID - the ID of the tile in the file
	 * @param xx - The x offset from the given tile's origin
	 * @param yy - The y offset from the given tile's origin
	 * @return the pixel at the given location
	 */
	public int getPixel(int tileID, int xx, int yy){
		if(tileID == -1){
			return 0;
		}
		int tileX = tileID%(sheetWidth/width);
		int tileY = tileID/(sheetWidth/width);
		int index = (tileX*width + xx) + (tileY*height + yy)*sheetWidth;
		if(index < 0 || index >= sheetPixels.length){
			return 0;
		}
		return sheetPixels[(tileX*width + xx) + (tileY*height + yy)*sheetWidth];
	}
	
	/**
	 * Returns the pixel for the given tile/coordinates
	 * @param tileX - The x coordinate of the tile in the file
	 * @param tileY - The y coordinate of the tile in the file
	 * @param xx - The x offset from the given tile's origin
	 * @param yy - The y offset from the given tile's origin
	 * @return the pixel at the given location
	 */
	public int getPixel(int tileX, int tileY, int xx, int yy){
		int index = (tileX*width + xx) + (tileY*height + yy)*sheetWidth;
		if(index < 0 || index >= sheetPixels.length){
			return 0;
		}
		return sheetPixels[(tileX*width + xx) + (tileY*height + yy)*sheetWidth];
	}
	
	/**
	 * Loads the tilesheet for the sprite
	 */
	protected void loadSheet(){
		File f = new File(PATH);
		try {
			img = ImageIO.read(f);
			sheetWidth = img.getWidth();
			sheetHeight = img.getHeight();
			this.sheetPixels = new int [sheetWidth*sheetHeight];
			img.getRGB(0, 0, sheetWidth, sheetHeight, sheetPixels, 0, sheetWidth);
		} catch (IOException e) {
			JixelGame.getConsole().printErr("Failed to load tile sheet at " + f.getPath(), e);
		}
	}

	/**
	 * @return the image of the sprite
	 */
	public BufferedImage getImg(){
		return img;
	}
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return the currentTile
	 */
	public int getTileID() {
		return currentTile;
	}

	/**
	 * @param currentTile the currentTile to set
	 */
	public void setTileID(int tileID) {
		this.currentTile = tileID;
	}
	
	/**
	 * @return the flipH
	 */
	public boolean isFlipH() {
		return flipH;
	}

	/**
	 * @param flipH the flipH to set
	 */
	public void setFlipH(boolean state) {
		this.flipH = state;
	}

	/**
	 * @return the flipV
	 */
	public boolean isFlipV() {
		return flipV;
	}

	/**
	 * @param flipV the flipV to set
	 */
	public void setFlipV(boolean state) {
		this.flipV = state;
	}
}
