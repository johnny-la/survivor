package com.jonathan.survivor.entity;

import com.jonathan.survivor.Survivor;


public class Earthquake extends Projectile
{
	/** Stores the width and height of the Earthquake's rectangle collider in world units. (Smaller than actual image so that collisions are forgiving) */
	public static final float COLLIDER_WIDTH = 80f * Survivor.WORLD_SCALE;	
	public static final float COLLIDER_HEIGHT = 40f * Survivor.WORLD_SCALE;
	
	/** Holds the velocity at which the earthquake travels. */
	public static final float FIRE_VELOCITY_X = 10;
	public static final float FIRE_VELOCITY_Y = 0;
	
	/** Stores the amount of damage dealt by the Earthquake when hitting a Human. */
	public static final float DAMAGE = 30;
	
	/** Creates a default Earthquake instance at bottom-center position (0,0). */
	public Earthquake()
	{
		this(0,0);
	}
	
	/** Instantiates an Earthquake instance at the given bottom-center (x,y) position. */
	public Earthquake(float x, float y)
	{
		super(x,y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Sets the velocity at which the Earthquake instance travels when fired
		getFireVelocity().set(FIRE_VELOCITY_X, FIRE_VELOCITY_Y);
		
		//Sets the amount of damage the earthquake will deal when hitting a Human instance.
		setDamage(DAMAGE);
	}
	
	@Override
	public boolean canTarget() {
		//Return false, since an Earthquake instance can never be targetted.
		return false;
	}
}
