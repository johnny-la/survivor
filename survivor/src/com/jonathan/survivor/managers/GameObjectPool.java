package com.jonathan.survivor.managers;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.GameObject;

/**
 * Pool used to store GameObjects and never delete them. This pool was simply meant to extend the Pool class to override the newObject method to be public.
 */

public abstract class GameObjectPool extends Pool<GameObject>
{
	/** Retrieves and returns a free GameObject in the pool or creates a new one if none are free. */
	public abstract GameObject newObject();
}
