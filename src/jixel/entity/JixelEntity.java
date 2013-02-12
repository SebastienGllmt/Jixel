package jixel.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jixel.gui.JixelSprite;
import jixel.stage.JixelGame;

@SuppressWarnings("serial")
public abstract class JixelEntity extends JixelSprite implements Comparable<JixelEntity>, Serializable {

	private String name;
	private double x, y;
	private double speed;

	private String currentAnim = null;
	private int animIndex = 1, fps = 0, frameCount = 0;
	private final Map<String, List<Integer>> animMap = new HashMap<String, List<Integer>>();

	public JixelEntity(final String IMG_PATH, final String ANIM_PATH, String name, int tileX, int tileY, double speed) {
		super(IMG_PATH);
		this.name = name;
		int tileSize = JixelGame.getScreen().getTileSize();
		this.x = tileX * tileSize;
		this.y = tileY * tileSize;
		if (ANIM_PATH != null) {
			readAnim(ANIM_PATH);
		}
		loadSheet();
	}

	/**
	 * Reads the animation file for an entity
	 * @param path - The path of the anim file
	 */
	private void readAnim(String path) {
		File f = new File(path);
		try (Scanner scan = new Scanner(f)) {
			fps = scan.nextInt();
			scan.nextLine();
			while (scan.hasNextLine()) {
				String name = scan.next();
				List<Integer> tiles = new ArrayList<Integer>();
				if (animMap.size() == 0) {
					currentAnim = name;
				}
				while (scan.hasNextInt()) {
					tiles.add(scan.nextInt());
				}
				animMap.put(name, tiles);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Could not find anim file at: " + f.getPath());
		} catch (Exception e) {
			e.printStackTrace();
			JixelGame.getConsole().print("Failed to read anim file at: " + f.getPath());
		}
	}

	/**
	 * Updates the animation of the entity
	 */
	private void updateAnim() {
		if (frameCount == fps) {
			List<Integer> tiles = animMap.get(currentAnim);
			frameCount = 0;
			animIndex++;
			if (animIndex == tiles.size()) {
				if (tiles.get(0) == 0) {
					animIndex--;
				} else {
					animIndex = 1;
				}
			}
			setTileID(tiles.get(animIndex));
		}
	}

	public void playAnim(String name) {
		if (animMap.containsKey(name)) {
			if (!name.equals(currentAnim)) {
				animIndex = 1;
				currentAnim = name;
			}
		} else {
			JixelGame.getConsole().print("No anim called " + name + " found in " + this.name);
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
		loadSheet();
	}

	/**
	 * Compares if this entity's name is equal to a given String
	 * @param name - The name to compare to
	 * @return whether or not the string is equal to the name
	 */
	public boolean equalsByName(String name) {
		return name.equals(this.name);
	}

	/**
	 * Compares if two entities have the same name
	 * @param e - Entity to compare
	 * @return whether or not they have the same name
	 */
	public boolean equalsByName(JixelEntity entity) {
		return entity.getName().equals(this.name);
	}

	/**
	 * Applies the update if game isn't paused
	 */
	public void applyActions() {
		if (!JixelGame.getPaused()) {
			if (currentAnim != null && fps > 0) {
				frameCount++;
				updateAnim();
			}
			update();
		}
	}

	/**
	 * Update method to be run every frame
	 */
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

	/**
	 * Compares an entity by Y axis position
	 * @return -1 if entity is lower, 0 if equal, 1 if higher
	 */
	public int compareTo(JixelEntity e) {
		if (e.getY() + getHeight() > getY() + getHeight()) {
			return -1;
		} else if (e.getY() == getY()) {
			return 0;
		} else {
			return 1;
		}
	}

}
