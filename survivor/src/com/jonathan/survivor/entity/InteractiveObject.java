package com.jonathan.survivor.entity;


/*
 * Any environment GameObject that can be clicked and interacted with.
 */

public abstract class InteractiveObject extends GameObject implements Clickable
{
	public enum InteractiveState {
		SPAWN, IDLE, CLICKED, HIT, SCAVENGED
	}
	
	/** Stores the current state of the interactive object for logic and rendering purposes. */
	private InteractiveState interactiveState;
	
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
	
	
	/** Called every frame to update logic. */
	@Override
	public abstract void update(float deltaTime);
	
	/** Called when the Interactive GameObject has been scavenged and can no longer be targetted. */
	public abstract void scavenged();


}
