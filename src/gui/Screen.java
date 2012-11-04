package gui;

import java.util.Random;

public class Screen {

	private int width, height;
	private int tilesX, tilesY;
	private int tileSize;
	private int FIXSHIFT;
	public int[] pixels;
	public int[][] tiles;
	Random rand = new Random();
	
	public Screen(int width, int height, int tileSize){
		this.width = width;
		this.height = height;
		this.tilesX = width/tileSize;
		this.tilesY = height/tileSize;
		this.tileSize = tileSize;
		FIXSHIFT = (int) (Math.log(tileSize)/Math.log(2));
		
		
		pixels = new int[width * height];
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
	
	public void render(){
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				pixels[x+y*width] = tiles[y >> FIXSHIFT][x >> FIXSHIFT];
			}
		}
	}
}
