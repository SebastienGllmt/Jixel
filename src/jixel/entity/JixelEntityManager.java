package jixel.entity;

import java.util.ArrayList;
import java.util.List;

public class JixelEntityManager {

	private List<JixelEntity> entityList = new ArrayList<JixelEntity>(256);
	
	public void add(JixelEntity entity){
		entityList.add(entity);
	}
	public void add(int index, JixelEntity entity){
		entityList.add(index, entity);
	}
	public void remove(JixelEntity entity){
		for(int i=0; i<entityList.size(); i++){
			if(entityList.get(i).equals(entity)){
				entityList.remove(i);
			}
		}
	}
	public void addToFront(JixelEntity entity){
		entityList.add(0, entity);
	}
	public void setEntityList(List<JixelEntity> newList){
		entityList = newList;
	}
	public List<JixelEntity> getList(){
		return entityList;
	}
	public void update(){
		for(JixelEntity entity : entityList){
			entity.applyActions();
		}
	}

}
