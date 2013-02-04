package jixel.gui;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import jixel.entity.JixelEntity;
import jixel.stage.JixelGame;



@SuppressWarnings("serial")
public class JixelScreen extends Canvas {

	private int width, height;
	private int scale;
	private int tileSize;
	private final int FIXSHIFT;

	private JFrame frame;
	private BufferStrategy bs;
	private BufferedImage image;
	public int[] pixels;

	private Font font = new Font("Courier", Font.PLAIN, 12);

	JixelEntity lockedEntity;
	Random rand = new Random();

	public JixelScreen(String title, int width, int height, int scale, int tileSize) {
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

	public void lockOn(JixelEntity entity) {
		lockedEntity = entity;
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}

	public void drawEntities() {
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		g.setFont(font);
		g.drawImage(image, 0, 0, width, height, null);

		List<JixelEntity> entityList = JixelGame.getEntityList().getList();
		for (int i = 0; i < entityList.size(); i++) {
			JixelEntity entity = entityList.get(i);
			int entityX = entity.getX();
			int entityY = entity.getY();
			if (entityX + entity.getWidth() > screenX && entityX < screenX + width) {
				if (entityY + entity.getHeight() > screenY && entityY < screenY + height) {
					g.drawImage(entity.getImg(), entityX - screenX, entityY - screenY, entity.getWidth(), entity.getHeight(), null);
				}
			}

		}

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
				g.drawString(messageList.get(i), tileSize + (tileSize / 2), height - tileSize - (tileSize / 2) - (i * 24));
			}
			g.drawString(JixelGame.getInput().getConsoleMsg(), tileSize + (tileSize / 2), height - (tileSize / 2) + 6);
		}
		g.dispose();
		bs.show();
	}

	public void setOffset(int xOffset, int yOffset) {
		JixelGame.getVM().setValue("Jixel_xOffset", xOffset);
		JixelGame.getVM().setValue("Jixel_yOffset", yOffset);
	}

	public void updateCamera(int xOffset, int yOffset) {
		if (lockedEntity != null) {
			int x = lockedEntity.getX() - (width / 2);
			int y = lockedEntity.getY() - (height / 2);
			setOffset(x, y);
		}
	}

	int screenX, screenY;
	public void drawMap() {
		screenX = JixelGame.getVM().getValue("Jixel_xOffset");
		screenY = JixelGame.getVM().getValue("Jixel_yOffset");
		updateCamera(screenX, screenY);
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		for (int y = 0; y < height; y++) {
			int yy = y + screenY;
			for (int x = 0; x < width; x++) {
				int xx = x + screenX;
				int tileID = JixelGame.getMap().getTile(xx >> FIXSHIFT, yy >> FIXSHIFT);
				pixels[x + y * width] = JixelGame.getMap().getSpriteSheet().loadImg(tileID, xx & 31, yy & 31);
			}
		}
		g.dispose();
		bs.show();

	}

	public void setTitle(String newTitle) {
		frame.setTitle(newTitle);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getScale() {
		return scale;
	}

	public int getTileSize() {
		return tileSize;
	}

	public int getFixshift() {
		return FIXSHIFT;
	}
}
