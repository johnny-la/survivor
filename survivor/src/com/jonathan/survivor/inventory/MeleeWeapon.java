package com.jonathan.survivor.inventory;

import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Tree;


public abstract class MeleeWeapon extends Weapon
{
	/** Stores the range in world units of the melee weapon. */
	private float range;
	
	/** Stores the name of the slot on the player in Spine where melee weapon images are stored. */
	public static final String SLOT_NAME = "Axe";
	
	/** Accepts the name, description, damage, and range of the melee weapon. */
	public MeleeWeapon(String name, String description, float damage, float range)
	{
		super(name, description, damage);
		
		//All MeleeWeapons and their images are mapped to the slot named "Axe" on the Player.
		setSlotName("Axe");
		
		//Populates the member variables with the constructor's parameters.
		this.range = range;
	}

	/** Gets the range in world units of the melee weapon. */
	public float getRange() {
		return range;
	}

	/** Sets the range in world units of the melee weapon. */
	public void setRange(float range) {
		this.range = range;
	}

	/** Called when the MeleeWeapon has hit a GameObject and should deal damage to it. */
	public void hit(GameObject gameObject) 
	{
		//If the hit GameObject is a tree
		if(gameObject instanceof Tree)
			//Delegate the method call to the hitTree() method.
			hitTree((Tree)gameObject);
		
	}
	
	/** Called when the MeleeWeapon has hit a tree and should deal damage to it. */
	public abstract void hitTree(Tree tree);
	
}