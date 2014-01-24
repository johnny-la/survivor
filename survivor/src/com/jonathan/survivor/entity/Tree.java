package com.jonathan.survivor.entity;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;

public class Tree extends GameObject implements Poolable
{
	/** Stores the width and height of a tree's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 2.313f, COLLIDER_HEIGHT = 4.063f;
	
	/** Stores the tree's health. Once it drops below zero, it is destroyed. */
	private float health;
	
	public enum TreeState {
		SPAWN, IDLE, HIT, DESTROYED
	}
	
	/** Stores the current state of the tree for logic and rendering purposes. */
	private TreeState treeState;
	
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
		setTreeState(TreeState.SPAWN);
	}
	
	/** Updates the tree every frame. */
	public void update(float deltaTime)
	{
		//Increments the stateTime of the tree to keep track of what point of the tree's animation should be playing.
		stateTime += deltaTime;
	}
	
	/** Called whenever this tree GameObject has been pushed back into a pool. In this case, we reset the tree's state back to default. */
	@Override
	public void reset()
	{
		//Tell the tree it has just spawned, in order for its correct animations to play.
		setTreeState(TreeState.SPAWN);
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

	/** Gets the current state of the tree for logic and rendering purposes. */
	public TreeState getTreeState() {
		return treeState;
	}

	/** Sets the current state of the tree for logic and rendering purposes. */
	public void setTreeState(TreeState treeState) {
		this.treeState = treeState;
	}

}
