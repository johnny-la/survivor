package com.jonathan.survivor.entity;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.InteractiveObject.InteractiveState;

public class Tree extends InteractiveObject implements Poolable
{
	/** Stores the width and height of a tree's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 2.313f, COLLIDER_HEIGHT = 4.063f;
	
	/** Stores the tree's health. Once it drops below zero, it is destroyed. */
	private float health;
	
	/** Creates a tree whose bottom-center is at position (0, 0). */
	public Tree()
	{
		this(0,0);
	}
	
	/** Creates a tree whose bottom-center is at position (x, y). */
	public Tree(float x, float y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Creates the skeleton used by the tree to render itself. The TreeSkeletonData is used to load the bone information for the skeleton.
		setSkeleton(new Skeleton(Assets.instance.treeSkeletonData));
		
		//Tells the tree it has just spawned. Tells the renderer what animations for the tree to play.
		setInteractiveState(InteractiveState.SPAWN);
	}

	/** Updates the tree every frame. */
	public void update(float deltaTime)
	{
		//Increments the stateTime of the tree to keep track of what point of the tree's animation should be playing.
		stateTime += deltaTime;
		
		//Updates the position of the collider to follow the tree's position.
		updateCollider();
	}
	
	/** Called whenever this tree GameObject has been pushed back into a pool. In this case, we reset the tree's state back to default. */
	@Override
	public void reset()
	{
		//Tell the tree it has just spawned, in order for its correct animations to play.
		setInteractiveState(InteractiveState.SPAWN);
	}

	/** Deals damage to the tree by removing the given amount from its health. */
	public void takeDamage(float damage)
	{
		//Subtract the tree's health by the damage dealt.
		health -= damage;
	}
	
	/** Gets the tree's health. */
	public float getHealth() {
		return health;
	}

	/** Sets the tree's health. */
	public void setHealth(float health) {
		this.health = health;
	}

}
