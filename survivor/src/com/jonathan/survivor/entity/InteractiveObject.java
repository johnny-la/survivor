package com.jonathan.survivor.entity;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool.Poolable;


/*
 * Any environment GameObject that can be clicked and interacted with.
 */

public abstract class InteractiveObject extends GameObject implements Clickable, Poolable
{
	public enum InteractiveState {
		SPAWN, IDLE, CLICKED, HIT, SCAVENGED
	}
	
	/** Stores the current state of the interactive object for logic and rendering purposes. */
	private InteractiveState interactiveState;
	
	/** Holds the HashMap of possible items that can be dropped from scavenging the Interactive GameObject. Key is the type 
	 *  of item, and Float is the probability of it being dropped from 0 (least probable) to 1 (most probable). */
	private HashMap<Class, Float> itemProbabilityMap;
	
	/** Creates the Interactive GameObject with the given bottom-center position and the given collider width and height. */
	public InteractiveObject(float x, float y, float width, float height)
	{
		super(x, y, width, height);
	}
	
	
	/** Called when the Interactive GameObject has just been targetted by a Human */
	public void targetted() 
	{
		//Set the Interactive GameObject to CLICKED state when targetted.
		setInteractiveState(InteractiveState.CLICKED);	
	}
	
	/** Called when the Interactive GameObject has just been untargetted by a Human */
	public void untargetted() 
	{
		//If the GameObject hasn't been scavenged
		if(interactiveState != InteractiveState.SCAVENGED)
			//Set the Interactive GameObject to IDLE state when untargetted.
			setInteractiveState(InteractiveState.IDLE);
	}
	
	
	/** Returns true if the Interactive GameObject can be targetted by the player. */
	@Override
	public boolean canTarget()
	{
		//If the GameObject has not yet been scavenged, return true, as the GameObject can be targetted.
		return interactiveState != InteractiveState.SCAVENGED;
	}
	
	/** Gets the current state of the interactive object for logic and rendering purposes. */
	public InteractiveState getInteractiveState() {
		return interactiveState;
	}

	/** Sets the current state of the interactive object for logic and rendering purposes. */
	public void setInteractiveState(InteractiveState interactiveState) {
		this.interactiveState = interactiveState;
		
		//Reset the state time to zero since the state has just changed.
		stateTime = 0;
	}
	
	/** Returns the HashMap which holds which items will be dropped when this InteractiveObject is scavenged. The key is the type of the Item 
	 *  dropped and the float is the probability of it dropping from [0,1]. */
	public HashMap<Class, Float> getItemProbabilityMap() {
		return itemProbabilityMap;
	}

	/** Sets the HashMap which holds which items will be dropped when this InteractiveObject is scavenged. The key is the type of the Item dropped
	 *  and the float is the probability of it dropping from [0,1]. */
	public void setItemProbabilityMap(HashMap<Class, Float> itemProbabilityMap) {
		this.itemProbabilityMap = itemProbabilityMap;
	}
	
	/** Called whenever this GameObject has been pushed back into a pool. In this case, we reset the box's state back to default. */
	@Override
	public void reset()
	{
		//Tell the box it has just spawned, in order for its renderer to know to reset the box to IDLE state.
		setInteractiveState(InteractiveState.SPAWN);
	}
	
	/** Called every frame to update logic. */
	@Override
	public abstract void update(float deltaTime);
	
	/** Called when the Interactive GameObject has been scavenged and can no longer be targetted. */
	public abstract void scavenged();

}
