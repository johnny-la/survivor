package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.math.Vector2;

/*
 * Denotes a projectile shot by a zombie in COMBAT mode.
 */

public abstract class Projectile extends GameObject
{
	
	/** Stores the amount of damage the projectile deals when colliding with a Human. */
	private float damage;
	
	/** Holds the velocity at which the projectile will be fired. Note that this is in the right direction. The vector is transformed to point to the left when desired. */
	private final Vector2 fireVelocity;
	
	/** Spawns a projectile at position (0,0) with collider size zero. */
	public Projectile()
	{
		this(0,0,0,0);
	}
	
	/** Creates a Projectile with the given (x,y) coordinates and the given collider size. */
	public Projectile(float x, float y, float width, float height)
	{
		super(x,y, width, height);
		
		//Assign the projectile a default velocity vector.
		fireVelocity = new Vector2();
		
		//Creates a new Skeleton instance used to render the visual element of the projectile.
		setSkeleton(new Skeleton(Assets.instance.projectileSkeletonData));
	}
	
	/** Updates the Projectile's game logic. */
	@Override
	public void update(float deltaTime)
	{
		//Update the position of the projectile according to its velocity and acceleration.
		updatePosition(deltaTime);
		//Update the position of the projectile's collider.
		updateCollider();
		
		//Update the stateTime.
		stateTime += deltaTime;
	}
	
	/** Fires the projectile at the given bottom-center (x,y) position and in the given direction. */
	public void fire(float x, float y, Direction direction)
	{
		//Sets the bottom-center of the projectile at the given (x,y) position.
		setPosition(x,y);
		
		//If the projectile is meant to go right
		if(direction == Direction.RIGHT)
		{
			//Fire the projectile in the +x direction.
			setVelocity(fireVelocity.x, fireVelocity.y);
		}
		//Else, if the projectile is supposed to be fired to the left
		else if(direction == Direction.LEFT)
		{
			//Fire the projectile in the -x direction.
			setVelocity(-fireVelocity.x, fireVelocity.y);
		}
	}
	
	/** Called when the projectile hits a Human. Deals damage to the hit Human. */
	public void hit(Human human)
	{
		//Deal damage to the Human which was hit.
		human.takeDamage(damage);
	}

	/** Gets the amount of damage dealt by the projectile when hitting a Human instance. */
	public float getDamage() {
		return damage;
	}

	/** Sets the amount of damage dealt by the projectile when hitting a Human instance. */
	public void setDamage(float damage) {
		this.damage = damage;
	}

	/** Gets the velocity at which the projectile is fired when the fire() method is called. */
	public Vector2 getFireVelocity() {
		return fireVelocity;
	}
}
