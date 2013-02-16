package jixel.console;

import java.util.List;

import jixel.entity.JixelEntity;
import jixel.stage.JixelGame;

@SuppressWarnings("unchecked")
public abstract class JixelStateManager {
	
	protected void runLoader(){
		//JixelGame.getEntityManager().setList((List<JixelEntity>) JixelGame.getVM().getValue("Jixel_entityList"));
		//JixelGame.getCamera().setLockedEntity((JixelEntity) JixelGame.getVM().getValue("Jixel_lockedEntity"));
		//JixelGame.getScreen().adjustScreen((int)JixelGame.getVM().getValue("Jixel_xOffset"), (int)JixelGame.getVM().getValue("Jixel_yOffset"));
		loadState();
	}
	
	protected void runSaver(){
		//JixelGame.getVM().setValue("Jixel_entityList", JixelGame.getEntityManager().getList());
		saveState();
	}
	
	public abstract void saveState();
	public abstract void loadState();

}
