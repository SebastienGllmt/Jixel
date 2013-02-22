package jixel.entity;

public interface JixelCollidable {

	/**
	 * Will fire an event for every collision with another entity for every frame
	 * @param e - The entity that involved in the collision
	 */
	public void isColliding(JixelEntity e);
	
	/**
	 * Will only fire an event the first time entities hit
	 * @param e - The entity that involved in the collision
	 */
	public void onHit(JixelEntity e);
	
	/**
	 * Will only fire an event when two entities that were hitting separate
	 * @param e - The entity that involved in the collision
	 */
	public void onSeparation(JixelEntity e);
}
