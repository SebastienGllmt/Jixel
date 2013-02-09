package jixel.entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JixelEntityManager {

	private List<JixelEntity> entityList = Collections.synchronizedList(new ArrayList<JixelEntity>());
	
	public void add(JixelEntity entity) {
		entityList.add(entity);
	}

	public void add(int index, JixelEntity entity) {
		entityList.add(index, entity);
	}

	public void remove(JixelEntity entity) {
		for (int i = 0; i < entityList.size(); i++) {
			if (entityList.get(i).equals(entity)) {
				entityList.remove(i);
			}
		}
	}

	public void addToFront(JixelEntity entity) {
		entityList.add(0, entity);
	}

	public void setEntityList(List<JixelEntity> newList) {
		entityList = newList;
	}

	public List<JixelEntity> getList() {
		return entityList;
	}

	public void update() {
		for (JixelEntity entity : entityList) {
			entity.applyActions();
		}
	}

	public boolean containsByName(String s) {
		for (JixelEntity entity : entityList) {
			if (entity.getName().equals(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsByName(JixelEntity e) {
		for (JixelEntity entity : entityList) {
			if (entity.getName().equals(e.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(JixelEntity e) {
		for (JixelEntity entity : entityList) {
			if (entity.equals(e)) {
				return true;
			}
		}
		return false;
	}

	public int getSize() {
		return entityList.size();
	}

	public String toString() {
		return "Number of entities: " + getSize();
	}

}
