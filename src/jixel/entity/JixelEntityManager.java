package jixel.entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JixelEntityManager {

	private List<JixelEntity> entityList = Collections.synchronizedList(new ArrayList<JixelEntity>());
	
	public synchronized void add(JixelEntity entity) {
		entityList.add(entity);
	}

	public synchronized void add(int index, JixelEntity entity) {
		entityList.add(index, entity);
	}

	public synchronized void remove(JixelEntity entity) {
		for (int i = 0; i < entityList.size(); i++) {
			if (entityList.get(i).equals(entity)) {
				entityList.remove(i);
			}
		}
	}
	
	public synchronized void sort(){
		Collections.sort(entityList);
	}
	
	public synchronized void setList(List<JixelEntity> newList) {
		entityList = newList;
	}

	public List<JixelEntity> getList() {
		return entityList;
	}

	public synchronized void update() {
		for (JixelEntity entity : entityList) {
			entity.applyActions();
		}
	}

	public synchronized boolean containsByName(String s) {
		for (JixelEntity entity : entityList) {
			if (entity.getName().equals(s)) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean containsByName(JixelEntity e) {
		for (JixelEntity entity : entityList) {
			if (entity.getName().equals(e.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(JixelEntity e) {
		return entityList.contains(e);
	}

	public synchronized int getSize() {
		return entityList.size();
	}

	public String toString() {
		return "Number of entities: " + getSize();
	}

}
