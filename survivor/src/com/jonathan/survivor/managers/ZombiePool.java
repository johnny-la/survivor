package com.jonathan.survivor.managers;

import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.entity.Zombie;

class ZombiePool extends Pool<Zombie>
{

	/** Called when no free objects are available in the pool, and a new one must be created. */
	@Override
	public Zombie newObject()
	{
		//Create and return a new zombie whose bottom-center is positioned at (0,0).
		return new Zombie();
	}
	
}
