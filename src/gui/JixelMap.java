package gui;

public class JixelMap {

	protected int width, height;
	protected int[] tiles;
	
	public JixelMap(int width, int height){
		this.width = width;
		this.height = height;
		
		this.tiles = new int[width*height];
		
		genRandLevel();
	}
	
	public JixelMap(String path){
		loadLevel(path);
	}
	
	private void genRandLevel(){
		
	}
	
	private void loadLevel(String path){
		
	}
	
	public void update(){
		
	}
	
	public void render(int xScroll, int yScroll, JixelScreen screen){
		
	}
}
