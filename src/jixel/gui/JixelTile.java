package jixel.gui;

public class JixelTile {

	private int x,y;
	private JixelSprite sprite;
	private boolean solid=false;
	
	public JixelTile(JixelSprite sprite, boolean solid){
		this.solid = solid;
		this.sprite = sprite;
	}
	
	public void render(int x, int y, JixelScreen screen){
	}
	
	public boolean getSolid(){
		return solid;
	}
	
	/**
	 * @return the underlying sprite
	 */
	public JixelSprite getSprite(){
		return sprite;
	}

}
