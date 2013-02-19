package jixel.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;

import jixel.entity.JixelEntity;
import jixel.entity.JixelEntityManager;
import jixel.stage.JixelGame;

public abstract class JixelCamera {
	
	private int minX, maxX, minY, maxY;
	private int cameraX, cameraY;
	private JixelMap map = new JixelMap();
	private JixelEntity lockedEntity;
	private JixelEntityManager entityManager = new JixelEntityManager();
	
	public JixelCamera(int minX, int minY, int maxX, int maxY){
		setMinX(minX);
		setMinY(minY);
		setMaxX(maxX);
		setMaxY(maxY);
	}
	
	/**
	 * @return the camera x position
	 */
	public int getCameraX() {
		return cameraX;
	}

	/**
	 * @param cameraX - Set the camera x position
	 */
	public void setCameraX(int cameraX) {
		this.cameraX = cameraX;
	}

	/**
	 * @return the camera y position
	 */
	public int getCameraY() {
		return cameraY;
	}

	/**
	 * @param cameraY - Set the camera y position
	 */
	public void setCameraY(int cameraY) {
		this.cameraY = cameraY;
	}

	/**
	 * Draw under entities
	 * @param g - Graphic object to draw it in
	 */
	public abstract void drawUnder(Graphics2D g);
	/**
	 * Draw over entities
	 * @param g - Graphic object to draw it in
	 */
	public abstract void drawOver(Graphics2D g);
	
	/**
	 * @return the entity manager for the camera
	 */
	public JixelEntityManager getEntityManager(){
		return entityManager;
	}
	public void setEntityManager(JixelEntityManager jem){
		this.entityManager = jem;
	}

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
		if(minX < 0){
			minX = 0;
			JixelGame.getConsole().printErr(new ArrayIndexOutOfBoundsException("Camera can not have a negative view"));
		}
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
		int maxWidth = JixelGame.getScreen().getWidth();
		if(maxX > maxWidth){
			maxX = maxWidth;
			JixelGame.getConsole().printErr(new ArrayIndexOutOfBoundsException("Camera can not have a greater view than the screen"));
		}
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
		if(minY < 0){
			minY = 0;
			JixelGame.getConsole().printErr(new ArrayIndexOutOfBoundsException("Camera can not have a negative view"));
		}
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
		int maxHeight = JixelGame.getScreen().getHeight();
		if(maxY > maxHeight){
			maxY = maxHeight;
			JixelGame.getConsole().printErr(new ArrayIndexOutOfBoundsException("Camera can not have a greater view than the screen"));
		}
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
