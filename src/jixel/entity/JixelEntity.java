package jixel.entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import jixel.stage.JixelGame;


@SuppressWarnings("serial")
public abstract class JixelEntity implements Serializable {

	private final String PATH;
	private String name;
	private int x, y;
	private int width, height;
	private double speed;

	transient private BufferedImage img;

	public JixelEntity(final String PATH, String name, int tileX, int tileY, double speed) {
		this.PATH = PATH;
		this.name = name;
		int tileSize = JixelGame.getScreen().getTileSize();
		this.x = tileX*tileSize;
		this.y = tileY*tileSize;
		readImage();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		readImage();
	}

	private void readImage() {
		try {
			this.img = ImageIO.read(new File(PATH));
			width = img.getWidth();
			height = img.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Failed to read entity at " + PATH);
		}
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
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	
	/**
	 * @return the img
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * @param img the img to set
	 */
	public void setImg(BufferedImage img) {
		this.img = img;
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

}
