package jixel.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import jixel.gui.JixelSprite;
import jixel.stage.JixelGame;

@SuppressWarnings("serial")
public abstract class JixelEntity extends JixelSprite implements Comparable<JixelEntity>, Serializable {

	private String name;
	private double x, y;
	private double speed;

	public JixelEntity(final String PATH, String name, int tileX, int tileY, double speed) {
		super(PATH);
		this.name = name;
		int tileSize = JixelGame.getScreen().getTileSize();
		this.x = tileX*tileSize;
		this.y = tileY*tileSize;
		loadSheet();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		loadSheet();
	}
	
	public boolean equalsByName(String s){
		return s.equals(this.name);
	}
	public boolean equalsByName(JixelEntity e){
		return e.getName().equals(this.name);
	}

	public void applyActions() {
		if (!JixelGame.getPaused()) {
			update();
		}
	}

	public abstract void update();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public int compareTo(JixelEntity e){
		if(e.getY()+getHeight() > getY()+getHeight()){
			return -1;
		}else if(e.getY() == getY()){
			return 0;
		}else{
			return 1;
		}
	}

}
