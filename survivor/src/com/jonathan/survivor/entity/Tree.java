package com.jonathan.survivor.entity;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.inventory.Wood;

public class Tree extends InteractiveObject
{
	/** Stores the width and height of a tree's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 2.313f, COLLIDER_HEIGHT = 4.063f;
	
	/** Stores the default health of the tree. */
	public static final float DEFAULT_HEALTH = 150;
	
	/** Stores the tree's health. Once it drops below zero, it is destroyed. */
	private float health = DEFAULT_HEALTH;
	
	/** Creates a tree whose bottom-center is at position (0, 0). */
	public Tree()
	{
		this(0,0);
	}
	
	/** Creates a tree whose bottom-center is at position (x, y). */
	public Tree(float x, float y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		setupItemProbabilityMap();
		
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
	
	/** Called whenever this tree GameObject has been pushed back into a pool. In this case, we reset the tree's state and health back to default. */
	@Override
	public void reset()
	{
		//Resets the Tree to SPAWN state.
		super.reset();
		
		//Reset the tree to default health.
		setHealth(DEFAULT_HEALTH);
	}
	
	/** Called on tree creation in order to populate the HashMap which dictates the probability of certain items dropping when the tree is destroyed. */
	private void setupItemProbabilityMap()
	{
		//Creates the item probability map where the key dictates which item can drop once the tree is destroyed, and the float is a number from 0 to 1
		//dicatating the probability of that item being dropped. Note that 1 means that the item will be dropped every time a tree is destroyed.
		HashMap<Class, Float> probabilityMap = new HashMap<Class, Float>();
		
		//Adds the items which will be dropped from the tree once it is destroyed.
		probabilityMap.put(Wood.class, 1f);
		
		//Sets the itemProbabilityMap of the tree to the probability map created in this method. Tells the tree which items will be dropped once it destroyed.
		setItemProbabilityMap(probabilityMap);
	}

	/** Deals damage to the tree by removing the given amount from its health. */
	public void takeDamage(float damage)
	{
		//Subtract the tree's health by the damage dealt.
		health -= damage;
		
		//If the tree has been destroyed
		if(health <= 0)
		{
			//Tell the tree it has been scavenged.
			scavenged();
		}
	}
	
	/** Called when the tree's health has been depleted to zero and has been scavenged. */
	public void scavenged()
	{
		//Set the tree to scavenged state.
		setInteractiveState(InteractiveState.SCAVENGED);
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
