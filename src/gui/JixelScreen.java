package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Random;

import stage.JixelGame;

public class JixelScreen {

	private int width, height;
	private int tilesX, tilesY;
	private int FIXSHIFT;
	private BufferedImage image;
	public int[] pixels;
	public int[][] tiles;
	Random rand = new Random();
	
	JixelGame game;
	
	//private int[] pixels2
	
	public JixelScreen(JixelGame game, int width, int height, int tileSize){
		this.game = game;
		this.width = width;
		this.height = height;
		tilesX = width/tileSize;
		tilesY = height/tileSize;
		FIXSHIFT = (int) (Math.log(tileSize)/Math.log(2));
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		tiles = new int[tilesY][tilesX];
		
		randomize();
		
	}
	
	public void clear(){
		for(int i=0; i<pixels.length; i++){
			pixels[i] = 0;
		}
	}
	
	public void randomize(){
		for(int i=0; i<tilesY; i++){
			for(int j=0; j<tilesX; j++){
				tiles[i][j] = rand.nextInt(0xFFFFFF);
			}
		}
	}
	
	public void update(){
		Graphics2D g = (Graphics2D)game.getBuffer().getDrawGraphics();
		
		if(game.getConsole().isRunning()){
			Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float).5);
			g.setComposite(alpha);
		}
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, game.getWidth(), game.getHeight());
		g.drawImage(image, 0, 0, game.getWidth(), game.getHeight(), null);
		
		g.dispose();
		game.getBuffer().show();
	}
	
	public void render(int xOffset, int yOffset){
		for(int y=0; y<height; y++){
			int yy = y + yOffset;
			if(yy < 0){
				yy = 0;
			}
			for(int x=0; x<width; x++){
				int xx = x + xOffset;
				if(xx < 0){
					xx = 0;
				}
				pixels[x+y*width] = tiles[(yy >> FIXSHIFT)%(tilesY-1)][(xx >> FIXSHIFT)%(tilesX-1)];
			}
		}
	}
}
