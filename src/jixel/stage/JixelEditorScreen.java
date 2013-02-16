package jixel.stage;

import java.awt.Graphics2D;

import jixel.gui.JixelCamera;

public class JixelEditorScreen extends JixelCamera {

	public JixelEditorScreen(int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);
	}

	@Override
	public void drawUnder(Graphics2D g) {
	}

	@Override
	public void drawOver(Graphics2D g) {
	}

}
