package jixel.gui;

public class JixelTile {

	private int tileID;

	public JixelTile(int spriteSheetIndex) {
		if (spriteSheetIndex != -1) {
			this.tileID = spriteSheetIndex - 1;
		}
	}

	/**
	 * @return - The id of the tile
	 */
	public int getTileID() {
		return tileID;
	}
}
