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
	
	/** Returns all the GameObjects contained by the level */
	Array<GameObject> getGameObjects();
}
