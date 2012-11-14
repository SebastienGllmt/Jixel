package entity;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JixelEntity{
	
	private int x, y;
	private int width, height;
	public int speed;
	private BufferedImage img;
	
	public JixelEntity(String filepath, int x, int y, int speed){
		this.x = x;
		this.y = y;
		this.speed = speed;
		try {
			this.img = ImageIO.read(new File(filepath));
			width = img.getWidth();
			height = img.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
