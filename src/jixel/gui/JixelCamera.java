package jixel.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

import jixel.entity.JixelEntity;
import jixel.entity.JixelEntityManager;

public abstract class JixelCamera {
	
	private int minX, maxX, minY, maxY;
	private JixelMap map = new JixelMap();
	private JixelEntity lockedEntity;
	
	public JixelCamera(int minX, int minY, int maxX, int maxY){
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	public abstract void drawUnder(Graphics2D g);
	public abstract void drawOver(Graphics2D g);
	
	public abstract JixelEntityManager getEntityManager();

	/**
	 * @return the map
	 */
	public JixelMap getMap() {
		return map;
	}

	/**
	 * @return the lockedEntity
	 */
	public JixelEntity getLockedEntity() {
		return lockedEntity;
	}

	/**
	 * @param lockedEntity the lockedEntity to set
	 */
	public void setLockedEntity(JixelEntity lockedEntity) {
		this.lockedEntity = lockedEntity;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(JixelMap map) {
		this.map = map;
	}

	/**
	 * @return the minX
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * @param minX the minX to set
	 */
	public void setMinX(int minX) {
		this.minX = minX;
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @param maxX the maxX to set
	 */
	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the minY
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * @param minY the minY to set
	 */
	public void setMinY(int minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @param maxY the maxY to set
	 */
	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}
	
	/**
	 * @return the absolute height of the camera
	 */
	public int getHeight(){
		return maxY-minY;
	}
	/**
	 * @return the absolute width of the camera
	 */
	public int getWidth(){
		return maxX-minX;
	}
	/**
	 * @return the size of the camera
	 */
	public Dimension getSize(){
		return new Dimension(getWidth(), getHeight());
	}
	/**
	 * Sets the max size of the camera
	 * @param maxX - The max x position the camera will display
	 * @param maxY - The max y position the camera will display
	 */
	public void setMaxSize(int maxX, int maxY){
		setMaxX(maxX);
		setMaxY(maxY);
	}
	/**
	 * Sets the min size of the camera
	 * @param minX - The min x position the camera will display
	 * @param minY - The min y position the camera will display
	 */
	public void setMinSize(int minX, int minY){
		setMinX(minX);
		setMinY(minY);
	}
	
}
