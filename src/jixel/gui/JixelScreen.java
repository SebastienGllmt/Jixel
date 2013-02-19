package jixel.gui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jixel.entity.JixelEntity;
import jixel.stage.JixelGame;

public class JixelScreen {

	private int width, height, tileSize, backgroundColor;
	public final int FIXSHIFT;
	private List<JixelCamera> cameraList = Collections.synchronizedList(new ArrayList<JixelCamera>());
	private int[] pixels;
	private JFrame frame;
	public int mouseX, mouseY;
	private double scaleX = 1, scaleY = 1;
	public double rotate = 0, translateX = 0, translateY = 0;
	public Canvas canvas = new Canvas();
	public Graphics2D g;

	private BufferStrategy bs;
	private BufferedImage image;

	public JixelScreen(String title, int width, int height, int tileSizeLog2) {
		if (width < 0 || height < 0 || tileSizeLog2 < 0) {
			FIXSHIFT = 0;
			JixelGame.getConsole().printErr(new IllegalArgumentException("Invalid arguments for JixelScreen"));
			System.exit(1);
		} else {
			this.width = width;
			this.height = height;
			this.tileSize = 1 << tileSizeLog2;
			FIXSHIFT = tileSizeLog2;
			JixelGame.getVM().newVar("Jixel_xOffset", 0);
			JixelGame.getVM().newVar("Jixel_yOffset", 0);

			Dimension size = new Dimension(width, height);
			canvas.setPreferredSize(size);
			setBackground(0);

			frame = new JFrame();
			frame.setResizable(false);
			frame.setTitle(title);
			frame.add(canvas);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			canvas.createBufferStrategy(3);
			bs = canvas.getBufferStrategy();
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

			canvas.requestFocus();
		}
	}

	/**
	 * Resizes the screen
	 * @param width - The new width of the screen
	 * @param height - The new height of the screen
	 */
	private synchronized void resizeScreen(int width, int height) {
		if (width > 0 && height > 0) {
			synchronized (JixelGame.getUpdateLock()) {
				Dimension size = new Dimension((int) (width * scaleX), (int) (height * scaleY));
				canvas.setPreferredSize(size);
				frame.pack();
				frame.setLocationRelativeTo(null);
				image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			}
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative sized screen"));
		}
	}

	/**
	 * Add a new camera to the screen
	 * @param camera - The new camera
	 */
	public void addCamera(JixelCamera camera) {
		synchronized (JixelGame.getUpdateLock()) {
			if (camera != null) {
				cameraList.add(camera);
			} else {
				JixelGame.getConsole().printErr(new NullPointerException("Can not set camera to null!"));
			}
		}
	}

	/**
	 * Removes the first instance of a given camera from the camera list
	 * @param camera - The camera to remove
	 * @return whether or not the element was removed
	 */
	public boolean removeCamera(JixelCamera camera) {
		synchronized (JixelGame.getUpdateLock()) {
			if (camera != null) {
				return cameraList.remove(camera);
			}
			return false;
		}
	}

	/**
	 * Empties the camera list
	 */
	public void clearCameraList() {
		synchronized (JixelGame.getUpdateLock()) {
			cameraList.clear();
		}
	}

	/**
	 * @return the list of cameras for the screen
	 */
	public List<JixelCamera> getUnmodifiableCameraList() {
		synchronized (JixelGame.getUpdateLock()) {
			return Collections.unmodifiableList(cameraList);
		}
	}

	/**
	 * Updates all the cameras
	 */
	public void update() {
		synchronized (JixelGame.getUpdateLock()) {
			for (JixelCamera camera : cameraList) {
				camera.getEntityManager().resetUpdate();
			}
			for (JixelCamera camera : cameraList) {
				camera.getEntityManager().update();
			}
		}
	}

	/**
	 * Returns the current background color for the screen
	 */
	public Color getBackground() {
		return new Color(backgroundColor);
	}

	/**
	 * Sets the background color for the screen
	 * @param color - The new color
	 */
	public void setBackground(int color) {
		setBackground(new Color(color));
	}

	/**
	 * Sets the background color for the screen
	 * @param c - The new color
	 */
	public void setBackground(Color c) {
		if (c != null) {
			this.backgroundColor = c.getRGB();
			canvas.setBackground(c);
		} else {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set background color to null"));
		}
	}

	/**
	 * Clears the screen
	 */
	private synchronized void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = backgroundColor;
		}
	}

	/**
	 * The main method to draw entities
	 * @param g - Graphics object for the screen
	 * @param entity - Entity to draw
	 */
	private void drawEntity(JixelCamera camera, Graphics2D g, JixelEntity entity) {
		int entityX = (int) entity.getX();
		int entityY = (int) entity.getY();
		if (entityX > camera.getMaxX() + camera.getCameraX() || entityX + entity.getWidth() < camera.getCameraX() + camera.getMinX()) {
			return;
		}
		if (entityY > camera.getMaxY() + camera.getCameraY() || entityY + entity.getHeight() < camera.getCameraY() + camera.getMinY()) {
			return;
		}
		BufferedImage img = new BufferedImage(entity.getWidth(), entity.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int[] entityPixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();

		for (int y = 0; y < entity.getHeight(); y++) {
			if (entityY + y > camera.getCameraY() + camera.getMinY() - 1 && entityY + y < camera.getCameraY() + camera.getMaxY()) {
				for (int x = 0; x < entity.getWidth(); x++) {
					if (entityX + x > camera.getCameraX() + camera.getMinX() - 1 && entityX + x < camera.getCameraX() + camera.getMaxX()) {
						int xx = entity.isFlipH() ? entity.getWidth() - x - 1 : x; //whether or not to flip horizontally
						int yy = entity.isFlipV() ? entity.getHeight() - y - 1 : y; //whether or not to flip vertically
						entityPixels[x + y * entity.getWidth()] = entity.getPixel(entity.getTileID(), xx, yy);
						;
					}
				}
			}
		}
		g.drawImage(img, entityX - camera.getCameraX(), entityY - camera.getCameraY(), entity.getWidth(), entity.getHeight(), null);
	}

	/**
	 * Main method to draw entities
	 */
	private synchronized void drawSprites(JixelCamera camera, Graphics2D g) {
		camera.drawUnder(g); //draw under entities what the camera wants

		/** Draw entities **/
		camera.getEntityManager().sort();
		List<JixelEntity> entityList = camera.getEntityManager().getUnmodifiableList();
		for (int i = 0; i < entityList.size(); i++) {
			drawEntity(camera, g, entityList.get(i));
		}

		camera.drawOver(g); //draw over entities what the camera wants

		/** Draw Console **/
		if (JixelGame.getConsole().isRunning()) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .5));
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
			g.setColor(Color.WHITE);
			g.drawLine(tileSize, height - tileSize, width - tileSize, height - tileSize);
			List<String> messageList = JixelGame.getConsole().getMessageList();
			for (int i = 0; i < messageList.size(); i++) {
				g.drawString(messageList.get(i), tileSize + (tileSize >> 1), height - tileSize - (tileSize >> 1) - ((i << 3) * 3));
			}
			g.drawString(JixelGame.getKeyInput().getConsoleMsg(), tileSize + (tileSize >> 1), height - (tileSize >> 1) + 6);
		}
	}

	/**
	 * Adjusts the screen's offset
	 * @param xOffset - The new offset for the x axis
	 * @param yOffset - The new offset for the y axis
	 */
	public void adjustScreen(JixelCamera camera, int xOffset, int yOffset) {
		if (xOffset < -camera.getMinX()) {
			xOffset = -camera.getMinX();
		}
		if (yOffset < -camera.getMinY()) {
			yOffset = -camera.getMinY();
		}
		if (xOffset > (camera.getMap().getWidth() << FIXSHIFT) - camera.getMaxX()) {
			xOffset = (camera.getMap().getWidth() << FIXSHIFT) - camera.getMaxX();
		}
		if (yOffset > (camera.getMap().getHeight() << FIXSHIFT) - camera.getMaxY()) {
			yOffset = (camera.getMap().getHeight() << FIXSHIFT) - camera.getMaxY();
		}
		camera.setCameraX(xOffset);
		JixelGame.getVM().setValue("Jixel_xOffset", xOffset);
		camera.setCameraY(yOffset);
		JixelGame.getVM().setValue("Jixel_yOffset", yOffset);
	}

	/**
	 * Updates the screen's location based off the current locked entity
	 */
	private synchronized void updateCamera(JixelCamera camera) {
		if (camera.getLockedEntity() != null) {
			int x = (int) camera.getLockedEntity().getX() - (camera.getWidth() >> 1) - camera.getMinX();
			int y = (int) camera.getLockedEntity().getY() - (camera.getHeight() >> 1) - camera.getMinY();
			adjustScreen(camera, x, y);
		}
	}

	/**
	 * Updates mouse position on the screen
	 */
	private void updateMouse(JixelCamera camera) {
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePoint, canvas);
		mouseX = (int) mousePoint.getX() + camera.getCameraX() % 32;
		if (mouseX < 0) {
			mouseX = 0;
		} else if (mouseX > width) {
			mouseX = width;
		}
		mouseY = (int) mousePoint.getY() + camera.getCameraY() % 32;
		if (mouseY < 0) {
			mouseY = 0;
		} else if (mouseY > width) {
			mouseY = width;
		}
	}

	/**
	 * Main render method for the screen
	 */
	public synchronized void render() {
		clear();

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.scale(scaleX, scaleY);
		g.rotate(rotate);
		g.translate(translateX, translateY);

		for (JixelCamera camera : cameraList) {
			updateMouse(camera);
			updateCamera(camera);
			if (camera.getMap().canLoad()) {
				for (int y = camera.getMinY(); y < camera.getMaxY(); y++) {
					int yy = y + camera.getCameraY();
					for (int x = camera.getMinX(); x < camera.getMaxX(); x++) {
						int xx = x + camera.getCameraX();
						int tileID = camera.getMap().getTile(xx >> FIXSHIFT, yy >> FIXSHIFT);
						pixels[x + y * width] = camera.getMap().getSpriteSheet().getPixel(tileID, xx & 31, yy & 31);
						;
					}
				}
			}
		}
		g.drawImage(image, 0, 0, width, height, null);

		for (JixelCamera camera : cameraList) {
			if (camera.getEntityManager() != null) {
				drawSprites(camera, g);
			}
		}
		g.dispose();
		bs.show();
	}

	/**
	 * @return the width of the screen
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width - Sets a new width for the screen
	 */
	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative sized screen"));
		}
	}

	/**
	 * @return the height of the screen
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param - Sets a new height for the screen
	 */
	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative sized screen"));
		}
	}

	/**
	 * Returns the size of the screen
	 */
	public Dimension getSize() {
		return new Dimension(getWidth(), getHeight());
	}

	/**
	 * Resizes the screen
	 * @param dimension - The new dimension of the screen
	 */
	public void setSize(Dimension dimension) {
		if (dimension == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set screen dimension to null"));
		}
		if (dimension.width > 0 && dimension.height > 0) {
			this.width = dimension.width;
			this.height = dimension.height;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative sized screen"));
		}
	}

	/**
	 * Resizes the screen
	 * @param width - The new width
	 * @param height - The new height
	 */
	public void setSize(int width, int height) {
		if (width > 0 && height > 0) {
			this.width = width;
			this.height = height;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative sized screen"));
		}
	}

	/**
	 * @return the tile size
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @return the current x scale of the game
	 */
	public double getScaleX() {
		return scaleX;
	}

	/**
	 * @return the current y scale of the game
	 */
	public double getScaleY() {
		return scaleY;
	}

	/**
	 * Sets a new scale for the graphics
	 * @param scale
	 */
	public void setScale(double scaleX, double scaleY) {
		if (scaleX > 0 && scaleY > 0) {
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr(new IllegalArgumentException("Can not have a negative scale"));
		}
	}

	public void setScaleX(double scaleX) {
		setScale(scaleX, getScaleY());
	}

	public void setScaleY(double scaleY) {
		setScale(getScaleX(), scaleY);
	}

	/**
	 * @param - The new window title
	 */
	public void setTitle(String newTitle) {
		frame.setTitle(newTitle);
	}
}
