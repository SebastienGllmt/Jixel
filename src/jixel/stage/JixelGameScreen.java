package jixel.stage;

import java.awt.Graphics2D;
import java.util.List;

import jixel.entity.JixelEntity;
import jixel.gui.JixelCamera;

public class JixelGameScreen extends JixelCamera {


	public JixelGameScreen(int minX, int minY, int maxX, int maxY) {
		super(minX, minY, maxX, maxY);
	}

	@Override
	public List<JixelEntity> getEntityList() {
		return JixelGame.getEntityList().getList();
	}

	@Override
	public void drawUnder(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawOver(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
