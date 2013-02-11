package jixel.gui;

public class JixelTile {

	private int tileID;

	public JixelTile(int tileID) {
		if (tileID != -1) {
			this.tileID = tileID - 1;
		}
	}

	public int getTileID() {
		return tileID;
	}
}
