package com.jonathan.survivor.managers;

import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.entity.Earthquake;

/*
 * The pool used by the GameObjectManager to store and create Earthquake instance for zombies to fire in COMBAT mode.
 */

public class EarthquakePool extends Pool<Earthquake>
{
	/** Called when a new Earthquake must be created inside the pool. */
	@Override
	protected Earthquake newObject()
	{
		//Returns a new Earthquake placed at bottom-center position (0,0).
		return new Earthquake();
	}
}
