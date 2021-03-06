package com.jonathan.survivor.inventory;

import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Tree;
import com.jonathan.survivor.math.Rectangle;


public abstract class MeleeWeapon extends Weapon
{
	/** Stores the horizontal reach of the melee weapon in world units. */
	private float reach;
	
	/** Holds the rectangle collider put around the melee weapon when it is equipped. Allows to test for hit detection. */
	private Rectangle collider;
	
	/** Stores the name of the slot on the player in Spine where melee weapon images are stored. */
	public static final String WEAPON_SLOT_NAME = "Axe";
	
	/** Accepts the name, description, damage, and range of the melee weapon. */
	public MeleeWeapon(String name, String description, float damage, float reach)
	{
		super(name, description, damage);
		
		//All MeleeWeapons and their images are mapped to the slot named SLOT_NAME on the Player.
		setWeaponSlotName(WEAPON_SLOT_NAME);
		
		//Creates a default rectangle for the weapon's collider.
		this.collider = new Rectangle();
		
		//Populates the member variables with the constructor's parameters.
		this.reach = reach;
	}

	/** Gets the horizontal reach in world units of the melee weapon. */
	public float getReach() {
		return reach;
	}

	/** Sets the horizontal reach in world units of the melee weapon. */
	public void setReach(float range) {
		this.reach = range;
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

	/** Returns the collider around the melee weapon used to detect when the weapon hits an enemy. */
	public Rectangle getCollider() {
		return collider;
	}

	/** Sets the collider around the melee weapon used to detect when the weapon hits an enemy. */
	public void setCollider(Rectangle collider) {
		this.collider = collider;
	}
	
}
