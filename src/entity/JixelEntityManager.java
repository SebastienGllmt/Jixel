package entity;

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
	public void addToFront(JixelEntity entity){
		entityList.add(0, entity);
	}
	public List<JixelEntity> getEntityList(){
		return entityList;
	}
	public void update(){
		for(JixelEntity entity : entityList){
			entity.update();
		}
	}

}
