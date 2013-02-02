package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import stage.JixelGame;

public abstract class JixelEntity implements Serializable{
	
	private final String PATH;
	private String name;
	private int x, y;
	private int width, height;
	private int speed;
	private boolean controllable;
	
	transient private BufferedImage img;
	
	public JixelEntity(final String PATH, String name, int x, int y, int speed, boolean controllable){
		this.PATH = PATH;
		this.name = name;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.controllable = controllable;
		readImage();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
	    in.defaultReadObject();
	    readImage();
	}
	
	private void readImage(){
		try {
			this.img = ImageIO.read(new File(PATH));
			width = img.getWidth();
			height = img.getHeight();
		} catch (IOException e) {
			System.out.println("Failed to read entity at " + PATH);
			e.printStackTrace();
		}
	}
	
	public void applyActions(){
		if(controllable){
			if (JixelGame.getInput().right) {
				setX(x+speed);
			}
			if (JixelGame.getInput().left) {
				setX(x-speed);
			}
			if (JixelGame.getInput().up) {
				setY(y-speed);
			}
			if (JixelGame.getInput().down) {
				setY(y+speed);
			}
		}
		update();
	}
	
	public abstract void update();
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getSpeed(){
		return speed;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public void setX(int x){
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	public void setSpeed(int speed){
		this.speed = speed;
	}
	public BufferedImage getImage(){
		return img;
	}
}
