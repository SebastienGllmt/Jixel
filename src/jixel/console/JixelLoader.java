package jixel.console;

import java.util.List;

import jixel.entity.JixelEntity;
import jixel.stage.JixelGame;

@SuppressWarnings("unchecked")
public abstract class JixelLoader {
	
	protected void runLoader(){
		JixelGame.getEntityList().setEntityList((List<JixelEntity>) JixelGame.getVM().getValue("Jixel_entityList"));
		JixelGame.getScreen().lockOn((JixelEntity) JixelGame.getVM().getValue("Jixel_lockedEntity"));
		JixelGame.getScreen().adjustScreen((int)JixelGame.getVM().getValue("Jixel_xOffset"), (int)JixelGame.getVM().getValue("Jixel_yOffset"));
		loadState();
	}
	
	public abstract void loadState();

}
