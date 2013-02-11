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

	private BufferStrategy bs;
	private BufferedImage image;

	private Font font = new Font("Courier", Font.PLAIN, 12);

	public JixelScreen(String title, JixelCamera camera, int width, int height, int scale, int tileSize) {
		if (camera == null) {
			throw new NullPointerException();
		}
		this.camera = camera;
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
		FIXSHIFT = (int) (Math.log(tileSize) / Math.log(2));
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

	public void attachCamera(JixelCamera camera) {
		synchronized (JixelGame.getUpdateLock()) {
			if (camera != null) {
				this.camera = camera;
			} else {
				System.err.println("Can not set camera to null!");
				JixelGame.getConsole().print("Can not set camera to null!");
			}
		}
	}

	public synchronized JixelCamera getCamera() {
		return camera;
	}

	public synchronized void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}

	private void drawEntity(Graphics2D g, JixelEntity entity) {
		int entityX = (int) entity.getX();
		int entityY = (int) entity.getY();
		BufferedImage img = new BufferedImage(entity.getWidth(), entity.getHeight(), BufferedImage.TYPE_INT_ARGB);
		int[] entityPixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();

		for (int y = 0; y < entity.getHeight(); y++) {
			if (entityY + y > camera.getMinY() && entityY + y < screenY + camera.getMaxY()) {
				for (int x = 0; x < entity.getWidth(); x++) {
					if (entityX + x > camera.getMinX() && entityX + x < screenX + camera.getMaxX()) {
						int xx = entity.isFlipH() ? entity.getWidth() - x - 1 : x;
						int yy = entity.isFlipV() ? entity.getHeight() - y - 1 : y;
						entityPixels[x + y * entity.getWidth()] = entity.loadImg(entity.getTileID(), xx, yy);
					}
				}
			}
		}
		g.drawImage(img, entityX - screenX, entityY - screenY, entity.getWidth(), entity.getHeight(), null);
	}

	public synchronized void drawEntities() {
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setFont(font);
		g.drawImage(image, 0, 0, width, height, null);

		camera.drawUnder(g);

		camera.getEntityManager().sort();
		List<JixelEntity> entityList = camera.getEntityManager().getList();
		for (int i = 0; i < entityList.size(); i++) {
			drawEntity(g, entityList.get(i));
		}

		camera.drawOver(g);

		if (JixelGame.getConsole().isRunning()) {
			Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) .5);
			g.setComposite(alpha);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
			g.setColor(Color.WHITE);
			g.drawLine(tileSize, height - tileSize, width - tileSize, height - tileSize);
			List<String> messageList = JixelGame.getConsole().getMessageList();
			for (int i = 0; i < messageList.size(); i++) {
				g.drawString(messageList.get(i), tileSize + (tileSize >> 1), height - tileSize - (tileSize >> 1) - (i * 24));
			}
			g.drawString(JixelGame.getKeyInput().getConsoleMsg(), tileSize + (tileSize >> 1), height - (tileSize >> 1) + 6);
		}
		g.dispose();
		bs.show();
	}

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

	private synchronized void updateCamera(int xOffset, int yOffset) {
		if (camera.getLockedEntity() != null) {
			int x = (int) camera.getLockedEntity().getX() - (width >> 1);
			int y = (int) camera.getLockedEntity().getY() - (height >> 1);
			adjustScreen(x, y);
		}
	}

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

	public synchronized void render() {
		updateMouse();
		updateCamera(screenX, screenY);
		if (camera.getMap().canLoad()) {
			for (int y = camera.getMinY(); y < camera.getMaxY(); y++) {
				int yy = y + screenY;
				for (int x = camera.getMinX(); x < camera.getMaxX(); x++) {
					int xx = x + screenX;
					int tileID = camera.getMap().getTile(xx >> FIXSHIFT, yy >> FIXSHIFT);
					pixels[x + y * width] = camera.getMap().getSpriteSheet().loadImg(tileID, xx & 31, yy & 31);
				}
			}
		}
		drawEntities();
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getMapWidth() {
		return camera.getMap().getWidth() << FIXSHIFT;
	}

	public int getMapHeight() {
		return camera.getMap().getHeight() << FIXSHIFT;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public int getTileSize() {
		return tileSize;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	public void setTitle(String newTitle) {
		frame.setTitle(newTitle);
	}

	public int getFixshift() {
		return FIXSHIFT;
	}
}
