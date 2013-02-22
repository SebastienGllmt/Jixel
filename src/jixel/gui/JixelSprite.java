package jixel.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import jixel.stage.JixelGame;

public class JixelSprite implements Serializable{

	private String path;
	private int width, height;

	private transient BufferedImage img;
	private int sheetWidth, sheetHeight;
	private int[] sheetPixels;
	private int currentTile;
	private double x, y;

	private boolean flipH = false, flipV = false;

	/**
	 * Constructor for JixelSprite
	 * @param path - Location of the sprite sheet
	 * @param width - Width of a given sprite
	 * @param height - Height of a given sprite
	 */
	public JixelSprite(String path) {
		int tileSize = JixelGame.getScreen().getTileSize();
		loadSheet(path, tileSize, tileSize);
	}
	
	/**
	 * Constructor for JixelSprite
	 * @param path - Location of the sprite sheet
	 * @param width - Width of a given sprite
	 * @param height - Height of a given sprite
	 */
	public JixelSprite(String path, int width, int height) {
		loadSheet(path, width, height);
	}

	/**
	 * Returns the pixel for the given tile/coordinates
	 * @param tileID - the ID of the tile in the file
	 * @param xx - The x offset from the given tile's origin
	 * @param yy - The y offset from the given tile's origin
	 * @return the pixel at the given location
	 */
	public int getPixel(int tileID, int xx, int yy) {
		if (tileID == -1) {
			return 0;
		}
		int tileX = tileID % (sheetWidth / width);
		int tileY = tileID / (sheetWidth / width);
		int index = (tileX * width + xx) + (tileY * height + yy) * sheetWidth;
		if (index < 0 || index >= sheetPixels.length) {
			return 0;
		}
		return sheetPixels[(tileX * width + xx) + (tileY * height + yy) * sheetWidth];
	}

	/**
	 * Returns the pixel for the given tile/coordinates
	 * @param tileX - The x coordinate of the tile in the file
	 * @param tileY - The y coordinate of the tile in the file
	 * @param xx - The x offset from the given tile's origin
	 * @param yy - The y offset from the given tile's origin
	 * @return the pixel at the given location
	 */
	public int getPixel(int tileX, int tileY, int xx, int yy) {
		int index = (tileX * width + xx) + (tileY * height + yy) * sheetWidth;
		if (index < 0 || index >= sheetPixels.length) {
			return 0;
		}
		return sheetPixels[(tileX * width + xx) + (tileY * height + yy) * sheetWidth];
	}

	/**
	 * Loads an underlying tile sheet for the sprite
	 */
	public synchronized void loadSheet(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
		File f = new File(path);
		try {
			img = ImageIO.read(f);
			sheetWidth = img.getWidth();
			sheetHeight = img.getHeight();
			this.sheetPixels = new int[sheetWidth * sheetHeight];
			img.getRGB(0, 0, sheetWidth, sheetHeight, sheetPixels, 0, sheetWidth);
		} catch (FileNotFoundException e) {
			JixelGame.getConsole().printErr("Could not find sprite sheet at " + f.getPath(), e);
		} catch (IOException e) {
			JixelGame.getConsole().printErr("Failed to load tile sheet at " + f.getPath(), e);
		}
	}
	
	/**
	 * Rereading image after loading from serialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		loadSheet(path, width, height);
	}

	/**
	 * The main method to draw sprites
	 * @param g - Graphics object for the screen
	 * @param camera - The camera to draw it in
	 */
	public synchronized void getDrawn(Graphics2D g, JixelCamera camera) {
		int spriteX = (int) getX();
		int spriteY = (int) getY();
		if (spriteX > camera.getMaxX() + camera.getCameraX() || spriteX + getWidth() < camera.getCameraX() + camera.getMinX()) {
			return;
		}
		if (spriteY > camera.getMaxY() + camera.getCameraY() || spriteY + getHeight() < camera.getCameraY() + camera.getMinY()) {
			return;
		}
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		int[] spritePixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();

		for (int y = 0; y < getHeight(); y++) {
			if (spriteY + y > camera.getCameraY() + camera.getMinY() - 1 && spriteY + y < camera.getCameraY() + camera.getMaxY()) {
				for (int x = 0; x < getWidth(); x++) {
					if (spriteX + x > camera.getCameraX() + camera.getMinX() - 1 && spriteX + x < camera.getCameraX() + camera.getMaxX()) {
						int xx = isFlipH() ? getWidth() - x - 1 : x; //whether or not to flip horizontally
						int yy = isFlipV() ? getHeight() - y - 1 : y; //whether or not to flip vertically
						spritePixels[x + y * getWidth()] = getPixel(getTileID(), xx, yy);
					}
				}
			}
		}
		g.drawImage(img, spriteX - camera.getCameraX(), spriteY - camera.getCameraY(), getWidth(), getHeight(), null);
	}

	/**
	 * @return the image of the sprite
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the currentTile
	 */
	public int getTileID() {
		return currentTile;
	}

	/**
	 * @param currentTile - The current tile the sprite is on
	 */
	public void setTileID(int tileID) {
		this.currentTile = tileID;
	}

	/**
	 * @return whether or not the sprite is flipped horizontally
	 */
	public boolean isFlipH() {
		return flipH;
	}

	/**
	 * @param flipH - Whether or not the sprite is flipped horizontally
	 */
	public void setFlipH(boolean state) {
		this.flipH = state;
	}

	/**
	 * @return whether or not the sprite is flipped vertically
	 */
	public boolean isFlipV() {
		return flipV;
	}

	/**
	 * @param flipV - Whether or not the sprite is flipped vertically
	 */
	public void setFlipV(boolean state) {
		this.flipV = state;
	}

	/**
	 * @return the x position
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param the new x position of the entity
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y position
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param the new y position of the entity
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * @return the path to the underlying sprite sheet
	 */
	public String getPath(){
		return path;
	}

	@Override
	public String toString() {
		return "JixelSprite [PATH=" + path + ", width=" + width + ", height=" + height + ", x=" + x + ", y=" + y + "]";
	}
}
