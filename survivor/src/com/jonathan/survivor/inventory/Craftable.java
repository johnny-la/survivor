package com.jonathan.survivor.inventory;

/*
 * Simple marker class used to denote an item that is not a weapon, and that is not consumable.
 */

public abstract class Craftable extends Item
{
	public Craftable(String name, String description)
	{
		super(name, description);
	}
}
