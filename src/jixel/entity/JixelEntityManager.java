package jixel.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jixel.stage.JixelGame;

public class JixelEntityManager {

	private List<JixelEntity> entityList = Collections.synchronizedList(new ArrayList<JixelEntity>());

	/**
	 * Adds a given entity to the entity list
	 * 
	 * @param entity - Entity to add
	 * @return whether or not the entity was added
	 */
	public synchronized boolean add(JixelEntity entity) {
		if (entity != null) {
			synchronized (entityList) {
				return entityList.add(entity);
			}
		} else {
			JixelGame.getConsole().printErr(new NullPointerException("Can not add null entity to Entity List"));
			return false;
		}
	}

	/**
	 * Remove the first occurrence of a given entity from the entity list
	 * 
	 * @param entity - The entity to remove
	 * @return whether or not the entity was found/removed
	 */
	public synchronized boolean remove(JixelEntity entity) {
		if (entity == null) {
			return false;
		}
		synchronized (entityList) {
			return entityList.remove(entity);
		}
	}

	/**
	 * Removes the entity at the given index
	 * 
	 * @param index - The index of the entity
	 * @return the entity that was just removed. Will return null if not found
	 */
	public synchronized JixelEntity remove(int index) {
		synchronized (entityList) {
			if (index > 0 && index < entityList.size()) {
				return entityList.remove(index);
			} else {
				JixelGame.getConsole().printErr(new IndexOutOfBoundsException("Index " + index + " is out of bounds"));
				return null;
			}
		}
	}

	/**
	 * Removes all of a given entity from the underlying list
	 * 
	 * @param entity - The entity to look for
	 * @return how many of that entity was found
	 */
	public synchronized int removeAll(JixelEntity entity) {
		int count = 0;
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).equals(entity)) {
					entityList.remove(i--);
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Removes all of a given entity from the underlying list by name
	 * 
	 * @param name - The name of the entity to look for
	 * @return how many of that entity was found
	 */
	public synchronized int removeAllByName(String name) {
		int count = 0;
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).getName() == null && name == null) {
					entityList.remove(i--);
					count++;
				} else if (entityList.get(i).getName().equals(name)) {
					entityList.remove(i--);
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Remove the first occurrence of an entity with a given name
	 * 
	 * @param name - The name of the entity to look for
	 * @return the entity that was just removed from the list. Will return null if not found
	 */
	public synchronized JixelEntity removeByName(String name) {
		return remove(indexOfByName(name));
	}

	/**
	 * Set an entity at a given index to a new entity
	 * 
	 * @param index - The index of the entity
	 * @param entity - The new entity
	 * @return the entity was was removed. Will return null if not found or on invalid index
	 */
	public synchronized JixelEntity set(int index, JixelEntity entity) {
		if (entity == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set an entity to null"));
			return null;
		}
		synchronized (entityList) {
			if (index > 0 && index < entityList.size()) {
				return entityList.set(index, entity);
			} else {
				JixelGame.getConsole().printErr(new IndexOutOfBoundsException("Index " + index + " is out of bounds"));
				return null;
			}
		}
	}

	/**
	 * Replace the first occurrence of a given entity to a new entity
	 * 
	 * @param oldEntity - The entity to replace
	 * @param newEntity - The entity to replace it with
	 * @return the index of the replaced entity. Will return -1 if old entity is not found
	 */
	public synchronized int replace(JixelEntity oldEntity, JixelEntity newEntity) {
		if (oldEntity == null || newEntity == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set entity to or from null"));
			return -1;
		}
		int index = indexOf(oldEntity);
		if (index != -1) {
			synchronized (entityList) {
				entityList.set(index, newEntity);
			}
			return index;
		} else {
			return -1;
		}
	}

	/**
	 * Replace all of a given entity with a new entity
	 * 
	 * @param oldEntity - The old entity to replace
	 * @param newEntity - The entity to replace with
	 * @return the number of entities repalced. Will return -1 if no old entity is found
	 */
	public synchronized int replaceAll(JixelEntity oldEntity, JixelEntity newEntity) {
		if (oldEntity == null || newEntity == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set entity to or from null"));
			return -1;
		}
		int count = 0;
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).equals(newEntity)) {
					count++;
					entityList.set(i, newEntity);
				}
			}
		}
		return count;
	}

	/**
	 * Sets the first occurrence of an entity with the given name to a new entity
	 * 
	 * @param name - The name to look for
	 * @param entity - The entity to replace it with
	 * @return the index of the replaced entity. Will return -1 if old entity is not found
	 */
	public synchronized int replaceByName(String name, JixelEntity entity) {
		if (entity == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set entity to null"));
			return -1;
		}
		int index = indexOfByName(name);
		if (index != -1) {
			synchronized (entityList) {
				entityList.set(index, entity);
			}
			return index;
		} else {
			return -1;
		}
	}

	/**
	 * Replaces all entities with a given name by another entity
	 * 
	 * @param name - The name to look for
	 * @param entity - The entity to replace it with
	 * @return the amount of entities replaced
	 */
	public synchronized int replaceAllByName(String name, JixelEntity entity) {
		if (entity == null) {
			JixelGame.getConsole().printErr(new NullPointerException("Can not set entity to null"));
			return -1;
		}
		int count = 0;
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (name == null && entity.getName() == null) {
					count++;
					entityList.set(i, entity);
				} else if (name.equals(entity.getName())) {
					count++;
					entityList.set(i, entity);
				}
			}
		}
		return count;
	}

	/**
	 * Renames all entities with a given name
	 * 
	 * @param name - The name to replace
	 * @param newName - The new name
	 * @return how many entities had their names replaced
	 */
	public synchronized int renameAll(String name, String newName) {
		int count = 0;
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).getName().equals(name)) {
					entityList.get(i).setName(newName);
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Sorts the entity list for rendering order
	 */
	public synchronized void sort() {
		synchronized (entityList) {
			Collections.sort(entityList);
		}
	}

	/**
	 * Sets the current entity list to a new list
	 * 
	 * @param newList - The new list to replace the old one with
	 * @return whether the list was replaced successfully
	 */
	public synchronized boolean setList(ArrayList<JixelEntity> newList) {
		if (newList != null) {
			synchronized (entityList) {
				entityList = Collections.synchronizedList(newList);
			}
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
		synchronized (entityList) {
			entityList.clear();
		}
	}

	/**
	 * Returns an unmodifiable version of the underlying list Note: Use of this list must be synchronized with the list as a lock. Failure to do so will result in a ConcurrentModificationException
	 * or undetermined behavior.
	 * 
	 * @return an unmodifiable version of the list
	 */
	public synchronized List<JixelEntity> getUnmodifiableList() {
		synchronized (entityList) {
			return Collections.unmodifiableList(entityList);
		}
	}

	/**
	 * Updates all the entities in the list
	 */
	public synchronized void update() {
		List<JixelEntity> entityListCopy;
		synchronized (entityList) {
			entityListCopy = new ArrayList<JixelEntity>(entityList);
		}
		for (JixelEntity entity : entityListCopy) {
			entity.applyActions();
			if (entity instanceof JixelCollidable) {
				checkCollisions(entity);
			}
		}
	}

	private void checkCollisions(JixelEntity entity) {
		List<JixelEntity> collisions = new ArrayList<JixelEntity>();
		JixelCollidable collidableEntity = ((JixelCollidable) entity);

		for (JixelEntity e : entityList) {
			if (e.intersects(entity)) {
				if (e != entity) {
					collisions.add(e);
				}
			}
		}
		List<JixelEntity> eCollisionList = entity.getCollisionList();

		synchronized (eCollisionList) {
			Iterator<JixelEntity> i = eCollisionList.iterator();
			while (i.hasNext()) {
				JixelEntity e = i.next();
				if (!collisions.contains(e)) {
					collidableEntity.onSeparation(e);
					i.remove();
				}
			}
			for (JixelEntity e : collisions) {
				collidableEntity.isColliding(e);
				if (!eCollisionList.contains(e)) {
					collidableEntity.onHit(e);
					eCollisionList.add(e);
				}
			}
		}
	}

	public synchronized void resetUpdate() {
		synchronized (entityList) {
			for (JixelEntity entity : entityList) {
				entity.wasUpdated = false;
			}
		}
	}

	/**
	 * Returns whether or not the list contains any entity with the given name
	 * 
	 * @param name - The name to look for
	 * @return whether or not the list contains the entity
	 */
	public synchronized boolean containsByName(String name) {
		return indexOfByName(name) != -1;
	}

	/**
	 * Returns the index of the first occurrence of an entity with a given name in the underlying entity list
	 * 
	 * @param name - The name to look for
	 * @return the index of the entity with the given name
	 */
	public synchronized int indexOfByName(String name) {
		synchronized (entityList) {
			for (int i = 0; i < entityList.size(); i++) {
				if (entityList.get(i).getName() == null && name == null) {
					return i;
				} else if (entityList.get(i).getName().equals(name)) {
					return i;
				}
			}
			return -1;
		}
	}

	/**
	 * Returns how many entities with a given name are in the list
	 * 
	 * @param name - Name of entity to look for
	 * @return how many entities with the name are in the list
	 */
	public synchronized int countOccurences(String name) {
		int count = 0;
		synchronized (entityList) {
			for (JixelEntity entity : entityList) {
				String i = entity.getName();
				if (i == null && name == null) {
					count++;
				} else if (i.equals(name)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Adds all entities from one entity list to the underlying list for this manager
	 * 
	 * @param entityList - The entity list to add
	 * @return whether or not the elements were added
	 */
	public synchronized boolean addAll(List<JixelEntity> entityList) {
		synchronized (entityList) {
			if (entityList != null) {
				for (JixelEntity e : entityList) {
					if (e == null) {
						JixelGame.getConsole().printErr(new NullPointerException("Can not add null entity to Entity List"));
						return false;
					}
				}
				return this.entityList.addAll(entityList);
			}
			return false;
		}
	}

	/**
	 * Returns whether or not the list contains a given entity
	 * 
	 * @param e - Which entity to look for
	 * @return whether or not the list contains the entity
	 */
	public synchronized boolean contains(JixelEntity e) {
		return indexOf(e) != -1;
	}

	/**
	 * Returns the index of the first occurrence of a given entity in the underlying entity list
	 * 
	 * @param e - Which entity to look for
	 * @return the index of the given entity Will return -1 if the entity is not found
	 */
	public synchronized int indexOf(JixelEntity e) {
		if (e == null) {
			return -1;
		}
		synchronized (entityList) {
			return entityList.indexOf(e);
		}
	}

	/**
	 * Returns a string indicating the number of elements in the list
	 */
	public synchronized String toString() {
		synchronized (entityList) {
			return "Number of entities: " + entityList.size();
		}
	}

}
