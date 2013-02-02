package gui;

public class JixelTile {

	private int x,y;
	private JixelSprite sprite;
	private boolean solid=false;
	
	public JixelTile(JixelSprite sprite){
		this.sprite = sprite;
	}
	
	private void render(int x, int y, JixelScreen screen){
		
	}
	
	public boolean solid(){
		return solid;
	}

}
