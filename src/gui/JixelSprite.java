package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import stage.JixelGame;

public class JixelSprite {

	private final String PATH;
	private int width, height;
	
	private BufferedImage img;
	private int sheetWidth, sheetHeight;
	private int[] sheetPixels;
	
	public JixelSprite(String path){
		this.PATH = path;
		this.width = JixelGame.getScreen().getTileSize();
		this.height = JixelGame.getScreen().getTileSize();
		loadSheet();
	}
	
	public JixelSprite(String path, int width, int height){
		this.PATH = path;
		this.width = width;
		this.height = height;
		loadSheet();
	}
	
	public int loadImg(int tileID, int xx, int yy){
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
	
	public int loadImg(int tileX, int tileY, int xx, int yy){
		int index = (tileX*width + xx) + (tileY*height + yy)*sheetWidth;
		if(index < 0 || index >= sheetPixels.length){
			return 0;
		}
		return sheetPixels[(tileX*width + xx) + (tileY*height + yy)*sheetWidth];
	}
	
	private void loadSheet(){
		try {
			File f = new File(PATH);
			img = ImageIO.read(f);
			sheetWidth = img.getWidth();
			sheetHeight = img.getHeight();
			this.sheetPixels = new int [sheetWidth*sheetHeight];
			img.getRGB(0, 0, sheetWidth, sheetHeight, sheetPixels, 0, sheetWidth);
		} catch (IOException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Failed to load tile sheet at " + PATH);
		}
	}

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
	
}
