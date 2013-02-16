package jixel.gui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jixel.entity.JixelEntity;
import jixel.stage.JixelGame;

@SuppressWarnings("serial")
public class JixelScreen extends Canvas {

	private int width, height, scale, tileSize;
	private final int FIXSHIFT;
	private JixelCamera camera;
	private int[] pixels;
	private JFrame frame;
	private int screenX, screenY;
	private int mouseX, mouseY;
	private int backgroundColor = 0;

	private BufferStrategy bs;
	private BufferedImage image;

	public JixelScreen(String title, JixelCamera camera, int width, int height, int scale, int tileSizeLog2) {
		attachCamera(camera);
		if (width < 0 || height < 0 || scale < 0 || tileSizeLog2 < 0) {
			FIXSHIFT = 0;
			JixelGame.getConsole().printErr("Invalid arguments for JixelScreen", new IllegalArgumentException());
			System.exit(1);
		} else {
			this.width = width;
			this.height = height;
			this.tileSize = 1 << tileSizeLog2;
			FIXSHIFT = tileSizeLog2;
			JixelGame.getVM().newVar("Jixel_xOffset", 0);
			JixelGame.getVM().newVar("Jixel_yOffset", 0);

			Dimension size = new Dimension(width * scale, height * scale);
			setPreferredSize(size);

			frame = new JFrame();
			frame.setResizable(false);
			frame.setTitle(title);
			frame.add(this);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			createBufferStrategy(3);
			bs = getBufferStrategy();
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

			requestFocus();
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
				Dimension size = new Dimension(width * scale, height * scale);
				setPreferredSize(size);
				frame.setSize(getWidth(), getHeight());
				frame.setLocationRelativeTo(null);
				image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
				pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			}
		} else {
			JixelGame.getConsole().printErr("Can not have a negative sized screen", new IllegalArgumentException());
		}
	}

	/**
	 * Attaches a new camera to the screen
	 * @param camera - The new camera
	 */
	public void attachCamera(JixelCamera camera) {
		synchronized (JixelGame.getUpdateLock()) {
			if (camera != null) {
				this.camera = camera;
			} else {
				JixelGame.getConsole().printErr("Can not set camera to null!", new NullPointerException());
			}
		}
	}

	/**
	 * @return the underlying camera for the screen
	 */
	public synchronized JixelCamera getCamera() {
		return camera;
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
		this.backgroundColor = color;
	}

	/**
	 * Sets the background color for the screen
	 * @param c - The new color
	 */
	public void setBackground(Color c) {
		if (c != null) {
			this.backgroundColor = c.getRGB();
		} else {
			JixelGame.getConsole().printErr("Can not set background color to null", new NullPointerException());
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
	private void drawEntity(Graphics2D g, JixelEntity entity) {
		int entityX = (int) entity.getX();
		int entityY = (int) entity.getY();
		if (entityX > camera.getMaxX() + screenX || entityX + entity.getWidth() < screenX + camera.getMinX()) {
			return;
		}
		if (entityY > camera.getMaxY() + screenY || entityY + entity.getHeight() < screenY + camera.getMinY()) {
			return;
		}
		BufferedImage img = new BufferedImage(entity.getWidth(), entity.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int[] entityPixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();

		for (int y = 0; y < entity.getHeight(); y++) {
			if (entityY + y > screenY + camera.getMinY() - 1 && entityY + y < screenY + camera.getMaxY()) {
				for (int x = 0; x < entity.getWidth(); x++) {
					if (entityX + x > screenX + camera.getMinX() - 1 && entityX + x < screenX + camera.getMaxX()) {
						int xx = entity.isFlipH() ? entity.getWidth() - x - 1 : x; //whether or not to flip horizontally
						int yy = entity.isFlipV() ? entity.getHeight() - y - 1 : y; //whether or not to flip vertically
						entityPixels[x + y * entity.getWidth()] = entity.getPixel(entity.getTileID(), xx, yy);
					}
				}
			}
		}
		g.drawImage(img, entityX - screenX, entityY - screenY, entity.getWidth(), entity.getHeight(), null);
	}

	/**
	 * Main method to draw entities
	 */
	private synchronized void drawSprites() {
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setFont(this.getFont());
		g.drawImage(image, 0, 0, width, height, null);

		camera.drawUnder(g); //draw under entities what the camera wants

		/** Draw entities **/
		camera.getEntityManager().sort();
		List<JixelEntity> entityList = camera.getEntityManager().getList();
		for (int i = 0; i < entityList.size(); i++) {
			drawEntity(g, entityList.get(i));
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

		g.dispose();
		bs.show();
	}

	/**
	 * Adjusts the screen's offset
	 * @param xOffset
	 * @param yOffset
	 */
	public void adjustScreen(int xOffset, int yOffset) {
		if (xOffset < 0) {
			xOffset = 0;
		}
		if (yOffset < 0) {
			yOffset = 0;
		}
		if (xOffset > getMapWidth() - getWidth()) {
			xOffset = getMapWidth() - getWidth();
		}
		if (yOffset > getMapHeight() - getHeight()) {
			yOffset = getMapHeight() - getHeight();
		}
		screenX = xOffset;
		JixelGame.getVM().setValue("Jixel_xOffset", xOffset);
		screenY = yOffset;
		JixelGame.getVM().setValue("Jixel_yOffset", yOffset);
	}

	/**
	 * Updates the screen's location based off the current locked entity
	 */
	private synchronized void updateCamera() {
		if (camera.getLockedEntity() != null) {
			int x = (int) camera.getLockedEntity().getX() - (width >> 1);
			int y = (int) camera.getLockedEntity().getY() - (height >> 1);
			adjustScreen(x, y);
		}
	}

	/**
	 * Updates mouse position on the screen
	 */
	private void updateMouse() {
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePoint, this);
		mouseX = (int) mousePoint.getX() + screenX % 32;
		if (mouseX < 0) {
			mouseX = 0;
		} else if (mouseX > width) {
			mouseX = width;
		}
		mouseY = (int) mousePoint.getY() + screenY % 32;
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
		updateMouse();
		updateCamera();
		if (camera.getMap().canLoad()) {
			if (camera.getMinY() < 0 || camera.getMinX() < 0) {
				throw new ArrayIndexOutOfBoundsException("Camera can not have a negative view");
			}
			if (camera.getMaxY() > getHeight() || camera.getMaxX() > getWidth()) {
				throw new ArrayIndexOutOfBoundsException("Camera can not have a greater view than the screen");
			}
			for (int y = camera.getMinY(); y < camera.getMaxY(); y++) {
				int yy = y + screenY;
				for (int x = camera.getMinX(); x < camera.getMaxX(); x++) {
					int xx = x + screenX;
					int tileID = camera.getMap().getTile(xx >> FIXSHIFT, yy >> FIXSHIFT);
					pixels[x + y * width] = camera.getMap().getSpriteSheet().getPixel(tileID, xx & 31, yy & 31);
				}
			}
		}
		drawSprites();
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
			JixelGame.getConsole().printErr("Can not have a negative sized screen", new IllegalArgumentException());
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
			JixelGame.getConsole().printErr("Can not have a negative sized screen", new IllegalArgumentException());
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
			JixelGame.getConsole().printErr("Can not set screen dimension to null", new NullPointerException());
		}
		if (dimension.width > 0 && dimension.height > 0) {
			this.width = dimension.width;
			this.height = dimension.height;
			resizeScreen(getWidth(), getHeight());
		} else {
			JixelGame.getConsole().printErr("Can not have a negative sized screen", new IllegalArgumentException());
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
			JixelGame.getConsole().printErr("Can not have a negative sized screen", new IllegalArgumentException());
		}
	}

	/**
	 * @return the width of the current map
	 */
	public int getMapWidth() {
		return camera.getMap().getWidth() << FIXSHIFT;
	}

	/**
	 * @return the height of the current map
	 */
	public int getMapHeight() {
		return camera.getMap().getHeight() << FIXSHIFT;
	}

	/**
	 * @return the current scale of the screen
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * @param
	 */
	public void setScale(int scale) {
		if (scale > 0) {
			this.scale = scale;
		} else {
			JixelGame.getConsole().printErr("Can not set scale negative or equal to zero", new IllegalArgumentException());
		}
	}

	/**
	 * @return the tile size
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @return the bitwise shift equivalent to the tile size
	 */
	public int getFixshift() {
		return FIXSHIFT;
	}

	/**
	 * @param - The new window title
	 */
	public void setTitle(String newTitle) {
		frame.setTitle(newTitle);
	}
}
