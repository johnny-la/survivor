package com.jonathan.survivor.managers;

import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.entity.ItemObject;

/*
 * The pool used by the GameObjectManager to store and create Item GameObjects for the user to pick up in the game world.
 */

public class ItemObjectPool extends Pool<ItemObject>
{
	/** Called when a new ItemObject must be created inside the pool. */
	@Override
	protected ItemObject newObject()
	{
		//Returns a new Item GameObject placed at bottom-center position (0,0).
		return new ItemObject();
	}
}
