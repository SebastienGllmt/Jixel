package jixel.gui;

import java.awt.Graphics2D;
import java.util.List;

import jixel.entity.JixelEntity;

public abstract class JixelCamera {
	
	private int minX, maxX, minY, maxY;
	
	public JixelCamera(int minX, int minY, int maxX, int maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	public abstract void drawUnder(Graphics2D g);
	public abstract void drawOver(Graphics2D g);
	
	public abstract List<JixelEntity> getEntityList();
	
	public int getMinX(){
		return minX;
	}
	public int getMaxX(){
		return maxX;
	}
	public int getMinY(){
		return minY;
	}
	public int getMaxY(){
		return maxY;
	}
	
}
