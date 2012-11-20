package gui;

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

import entity.JixelEntity;

import stage.JixelGame;

@SuppressWarnings("serial")
public class JixelScreen extends Canvas {
	
	private int width, height;
	private int scale;
	private int tilesX, tilesY;
	private int tileSize;
	private int FIXSHIFT;
	
	private JFrame frame;
	private BufferStrategy bs;
	private BufferedImage image;
	private JixelSprite tileMap = new JixelSprite("/spritesheet.png", 32, 32);
	public int[] pixels;
	public int[][] tiles;
	
	private Font font = new Font("Courier", Font.PLAIN, 12);
	
	JixelEntity lockedEntity;
	Random rand = new Random();
	
	public JixelScreen(String title, int width, int height, int scale, int tileSize){
		this.width = width;
		this.height = height;
		this.tileSize = tileSize;
		tilesX = width/tileSize;
		tilesY = height/tileSize;
		FIXSHIFT = (int) (Math.log(tileSize)/Math.log(2));
		
		JixelGame.getVM().newVar("Jixel_xOffset", 0);
		JixelGame.getVM().newVar("Jixel_yOffset", 0);
		
		Dimension size = new Dimension(width*scale, height*scale);
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
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		tiles = new int[tilesY][tilesX];
		
		requestFocus();
	}
	
	public void lockOn(JixelEntity entity){
		lockedEntity = entity;
	}
	
	
	public void clear(){
		for(int i=0; i<pixels.length; i++){
			pixels[i] = 0;
		}
	}
	
	public void update(){
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		g.setFont(font);
		g.drawImage(image, 0, 0, width, height, null);
		
		List<JixelEntity> entityList = JixelGame.getEntityList().getEntityList();
		for(int i=0; i<entityList.size(); i++){
			JixelEntity entity = entityList.get(i);
			int screenX = JixelGame.getVM().getValue("Jixel_xOffset");
			int screenY = JixelGame.getVM().getValue("Jixel_yOffset");
			int entityX = entity.getX();
			int entityY = entity.getY();
			if(entityX+entity.getWidth() > screenX && entityX < screenX + width){
				if(entityY+entity.getHeight() > screenY &&  entityY < screenY + height){
					g.drawImage(entity.getImage(), entityX - screenX, entityY-screenY, entity.getWidth(), entity.getHeight(), null);
				}
			}
		}
		
		if(JixelGame.getConsole().isRunning()){
			Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float).5);
			g.setComposite(alpha);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);
			
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1));
			g.setColor(Color.WHITE);
			g.drawLine(tileSize, height-tileSize,  width-tileSize, height-tileSize);
			List<String> messageList = JixelGame.getConsole().getMessageList();
			for(int i=0; i<messageList.size(); i++){
				g.drawString(messageList.get(i), tileSize+(tileSize/2), height-tileSize-(tileSize/2)-(i*24));
			}
			g.drawString(JixelGame.getInput().getConsoleMsg(), tileSize+(tileSize/2), height-(tileSize/2)+6);
		}
		g.dispose();
		bs.show();
	}
	
	public void render(){
		int xOffset = JixelGame.getVM().getValue("Jixel_xOffset");
		int yOffset = JixelGame.getVM().getValue("Jixel_yOffset");
		if(lockedEntity != null){
			int x = lockedEntity.getX() - (width/2);
			int y = lockedEntity.getY() - (height/2);
			JixelGame.getVM().setValue("Jixel_xOffset", x);
			JixelGame.getVM().setValue("Jixel_yOffset", y);
		}
		for(int y=0; y<height; y++){
			int yy = y - yOffset;
			if(yy < 0 || yy >= height){
				continue;
			}
			for(int x=0; x<width; x++){
				int xx = x - xOffset;
				if(xx >= 0 && xx < width){
					pixels[xx + yy*width] = tileMap.loadImg(x >> FIXSHIFT, y >> FIXSHIFT, x&31, y&31);
				}
			}
		}
	}
	
	public void setTitle(String newTitle){
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
}
