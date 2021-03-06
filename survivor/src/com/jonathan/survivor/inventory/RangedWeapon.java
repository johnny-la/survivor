package com.jonathan.survivor.inventory;

import java.awt.geom.Line2D;

import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.math.Line;
import com.jonathan.survivor.math.Vector2;


public abstract class RangedWeapon extends Weapon
{	
	/** Stores the name of the slot on the player in Spine where ranged weapon images are stored. */
	public static final String WEAPON_SLOT_NAME = "Rifle";
	
	/** Holds a line which traces the trajectory of the gun's bullet. In essence, this is location where a zombie can get hit by the weapon. */
	private final Line crosshair;
	
	/** Stores the position where the gun's crosshair should be placed. This is where the trajectory lines starts. */
	private final Vector2 crosshairPoint;
	
	/** Stores the range of the gun, which is the distance in world units that the gun's bullet can travel. */
	private float range;
	
	/** Holds the amount of time it takes to charge the weapon completely. */
	private float chargeTime;
	
	/** Accepts the name, description, damage, and charge time of the melee weapon. */
	public RangedWeapon(String name, String description, float damage, float range, float chargeTime)
	{
		super(name, description, damage);
		
		//Populates the object's member variables with the given constructor arguments.
		this.setRange(range);
		this.chargeTime = chargeTime;
		
		//Creates a new, default position where the crosshair should be placed on the ranged weapon.
		crosshair = new Line();
		//Creates a new, default position where the crosshair should be placed on the ranged weapon.
		crosshairPoint = new Vector2();
		
		//All MeleeWeapons and their images are mapped to the slot named SLOT_NAME on the Player.
		setWeaponSlotName(WEAPON_SLOT_NAME);
	}

	/** Called when the RangedWeapon has hit a GameObject and should deal damage to it. */
	public void hit(GameObject gameObject) 
	{
	}
	
	/** Returns a line depicting where the bullet will travel when the ranged weapon is shot. */
	public Line getCrosshair() {
		return crosshair;
	}
	
	/** Returns the position where the start of the crosshair should be placed on the weapon. This is usually the tip of the ranged weapon. */
	public Vector2 getCrosshairPoint() {
		return crosshairPoint;
	}
	
	/** Gets the range of the ranged weapon in world units. This is the distance that a bullet can travel relative to the tip of the weapon. */
	public float getRange() {
		return range;
	}

	/** Sets the range of the ranged weapon in world units. This is the distance that a bullet can travel relative to the tip of the weapon. */
	public void setRange(float range) {
		this.range = range;
	}

	/** Gets the amount of  time it takes to charge the weapon. */
	public float getChargeTime() {
		return chargeTime;
	}

	/** Gets the amount of  time it takes to charge the weapon completely. */
	public void setChargeTime(float chargeTime) {
		this.chargeTime = chargeTime;
	}
	
}
