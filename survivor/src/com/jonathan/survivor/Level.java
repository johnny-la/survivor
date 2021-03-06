package com.jonathan.survivor;

import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.entity.GameObject;

/**
 * Common interface for all level types.
 */

public interface Level 
{
	/** Returns the x position where the player should spawn when the level is first created. */
	float getPlayerStartX();
	
	/** Returns the y position where the player should spawn when the level is first created. */
	float getPlayerStartY();
	
	/** Returns the y-position of the ground in world coordinates at the specified x-position of the layer in world coordinates. */
	float getGroundHeight(float xPos);
	
	/** Returns true if the given GameObject is out of bounds of the level. */
	boolean outOfBounds(GameObject gameObject);
	
	/** Adds the given GameObject to the level. Like this, the level will be aware that it contains this GameObject, and this GameObject will be drawn to the screen. */
	void addGameObject(GameObject go);
	
	/** Removes the given GameObject from the level. Like this, the level will be aware that it no longer contains this GameObject, and will thus no longer be drawn to the screen. */
	void removeGameObject(GameObject go);
	
	/** Returns all the GameObjects contained by the level */
	Array<GameObject> getGameObjects();

}
