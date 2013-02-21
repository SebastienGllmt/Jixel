package jixel.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jixel.stage.JixelGame;

public class JixelEntityManager {

	private List<JixelEntity> entityList = Collections.synchronizedList(new ArrayList<JixelEntity>());

	/**
	 * Adds a given entity to the entity list
	 * @param entity - Entity to add
	 * @return whether or not the entity was added
	 */
	public synchronized boolean add(JixelEntity entity) {
		if (entity != null) {
			return entityList.add(entity);
		} else {
			JixelGame.getConsole().printErr(new NullPointerException("Can not add null entity to Entity List"));
			return false;
		}
	}

	/**
	 * Remove a given entity from the entity list
	 * @param entity - The entity to remove
	 * @return whether or not the entity was found/removed
	 */
	public synchronized boolean remove(JixelEntity entity) {
		if (entity == null) {
			return false;
		}
		return entityList.remove(entity);
	}

	/**
	 * Sorts the entity list for rendering order
	 */
	public synchronized void sort() {
		Collections.sort(entityList);
	}

	/**
	 * Sets the current entity list to a new list
	 * @param newList - The new list to replace the old one with
	 * @return whether the list was replaced successfully
	 */
	public synchronized boolean setList(ArrayList<JixelEntity> newList) {
		if (newList != null) {
			entityList = Collections.synchronizedList(newList);
			return true;
		} else {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set Entity List to null"));
			return false;
		}
	}

	/**
	 * Clears the list
	 */
	public synchronized void clear() {
		entityList.clear();
	}

	/**
	 * @return an unmodifiable version of the list
	 */
	public synchronized List<JixelEntity> getUnmodifiableList() {
		return Collections.unmodifiableList(entityList);
	}

	/**
	 * Updates all the entities in the list
	 */
	public synchronized void update() {
		for (JixelEntity entity : entityList) {
			entity.applyActions();
		}
	}

	public synchronized void resetUpdate() {
		for (JixelEntity entity : entityList) {
			entity.wasUpdated = false;
		}
	}

	/**
	 * Returns whether or not the list contains any entity with the given name
	 * @param s - The name to look for
	 * @return whether or not the list contains the entity
	 */
	public synchronized boolean containsByName(String name) {
		for (JixelEntity entity : entityList) {
			String i = entity.getName();
			if (i == null && name == null) {
				return true;
			} else if (i.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns how many entities with a given name are in the list
	 * @param name - Name of entity to look for
	 * @return how many entities with the name are in the list
	 */
	public synchronized int countOccurences(String name) {
		int count = 0;
		for (JixelEntity entity : entityList) {
			String i = entity.getName();
			if (i == null && name == null) {
				count++;
			} else if (i.equals(name)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Adds all entities from one entity list to the underlying list for this manager
	 * @param entityList - The entity list to add
	 * @return whether or not the elements were added
	 */
	public synchronized boolean addAll(List<JixelEntity> entityList) {
		if (entityList != null) {
			for (JixelEntity e : entityList) {
				if (e == null) {
					JixelGame.getConsole().printErr(new NullPointerException("Can not add null entity to Entity List"));
					return false;
				}
			}
			return entityList.addAll(entityList);
		}
		return false;
	}

	/**
	 * Returns whether or not the list contains a given entity
	 * @param e - Which entity to look for
	 * @return whether or not the list contains the entity
	 */
	public synchronized boolean contains(JixelEntity e) {
		if (e == null) {
			return false;
		}
		return entityList.contains(e);
	}

	/**
	 * Returns a string indicating the number of elements in the list
	 */
	public synchronized String toString() {
		return "Number of entities: " + entityList.size();
	}

}
