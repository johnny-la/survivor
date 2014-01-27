package com.jonathan.survivor.inventory;

import com.jonathan.survivor.inventory.Item.Type;

public class Weapon extends Item
{
	/** The amount of damage done by the weapon with one hit. */
	protected float damage;
	
	/** Creates a weapon with the given name, description, and damage. */
	public Weapon(String name, String description, float damage)
	{
		super(Type.WEAPON, name, description);
		
		//Populates the member variables with their respective constructor arguments. 
		this.damage = damage;
	}

	/** Gets the amount of damage the weapon deals in one hit. */
	public float getDamage() {
		return damage;
	}
	
	/** Sets the amount of damage the weapon deals in one hit. */
	public void setDamage(float damage) {
		this.damage = damage;
	}
}
