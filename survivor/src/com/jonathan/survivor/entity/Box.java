package com.jonathan.survivor.entity;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.inventory.Charcoal;
import com.jonathan.survivor.inventory.Saltpeter;
import com.jonathan.survivor.inventory.Sulfur;
import com.jonathan.survivor.inventory.Water;
import com.jonathan.survivor.inventory.Wood;

public class Box extends InteractiveObject implements Poolable
{
	/** Stores the width and height of a box's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 72.9f * Survivor.WORLD_SCALE, 
							  COLLIDER_HEIGHT = 42.45f * Survivor.WORLD_SCALE;
	
	/** Creates a box whose bottom-center is at position (0, 0). */
	public Box()
	{
		this(0,0);
	}
	
	/** Creates a box whose bottom-center is at position (x, y). */
	public Box(float x, float y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Sets up the item probability HashMap, which dictates the probability of items falling from this object when scavenged.
		setupItemProbabilityMap();
		
		//Creates the skeleton used by the box to render itself. The boxSkeletonData is used to load the bone information for the skeleton.
		setSkeleton(new Skeleton(Assets.instance.boxSkeletonData));
		
		//Tells the box it has just spawned. Tells the renderer what animations for the box to play.
		setInteractiveState(InteractiveState.SPAWN);
	}

	/** Updates the box every frame. */
	public void update(float deltaTime)
	{
		//Increments the stateTime of the box to keep track of what point of the box's animation should be playing.
		stateTime += deltaTime;
		
		//Updates the position of the collider to follow the box's position.
		updateCollider();
	}
	
	/** Called on box creation in order to populate the HashMap which dictates the probability of certain items dropping when the box is destroyed. */
	private void setupItemProbabilityMap()
	{
		//Creates the item probability map where the key dictates which item can drop once the box is destroyed, and the float is a number from 0 to 1
		//dicatating the probability of that item being dropped. Note that 1 means that the item will be dropped every time a box is destroyed.
		HashMap<Class, Float> probabilityMap = new HashMap<Class, Float>();
		
		//Adds the items which will be dropped from the box once it is destroyed.
		probabilityMap.put(Water.class, 0.6f);
		probabilityMap.put(Wood.class, 0.3f);
		probabilityMap.put(Charcoal.class, 0.5f);
		probabilityMap.put(Saltpeter.class, 0.2f);
		probabilityMap.put(Sulfur.class, 0.4f);
		
		//Sets the itemProbabilityMap of the box to the probability map created in this method. Tells the box which items will be dropped once it destroyed.
		setItemProbabilityMap(probabilityMap);
	}
	
	/** Called when the box has been opened by the player. Tells the box to enter its SCAVENGED state. */
	public void scavenged()
	{
		//Set the box to scavenged state.
		setInteractiveState(InteractiveState.SCAVENGED);
	}
}
