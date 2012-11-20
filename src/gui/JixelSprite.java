package gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import stage.JixelGame;

public class JixelSprite {

	private final String PATH;
	private int width, height;
	
	private int sheetWidth, sheetHeight;
	public int[] sheetPixels;
	private int[] tilePixels;
	
	public JixelSprite(String path, int width, int height){
		this.PATH = path;
		this.width = width;
		this.height = height;
		this.tilePixels = new int[width*height];
		loadSheet();
	}
	
	public int loadImg(int x, int y, int xx, int yy){
		return sheetPixels[(x*width + xx) + (y*height + yy)*sheetWidth];
	}
	
	private void loadSheet(){
		try {
			BufferedImage img = ImageIO.read(JixelSprite.class.getResource(PATH));
			sheetWidth = img.getWidth();
			sheetHeight = img.getHeight();
			this.sheetPixels = new int [sheetWidth*sheetHeight];
			img.getRGB(0, 0, sheetWidth, sheetHeight, sheetPixels, 0, sheetWidth);
		} catch (IOException e) {
			JixelGame.getConsole().print("Failed to load tile sheet at " + PATH);
		}
		
	}
}
