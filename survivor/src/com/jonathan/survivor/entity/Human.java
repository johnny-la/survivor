package com.jonathan.survivor.entity;

/**
 * A Human instance is a GameObject that can move around and that either has some form of AI or is controlled by the player. 
 * @author Jonathan
 *
 */

public abstract class Human extends GameObject 
{
	public enum Mode {
		EXPLORING, FIGHTING
	}
	
	/** Stores the mode of the Human (EXPLORING or FIGHTING) used to determine how the human's logic should be processed. */
	protected Mode mode;
	
	public enum State {
		SPAWN, IDLE, WALK, JUMP, FALL, MELEE, CHARGE, FIRE, HIT, DEAD
	}
	
	/** Stores the state of the Human (IDLE, WALK, etc.), usually used to dictate which animations to play. */
	protected State state;
	
	public enum Direction {
		LEFT, RIGHT
	}
	
	/** Stores the direction the Human is facing */
	protected Direction direction;
	
	/** Creates a Human GameObject instance whose bottom-center is at (x,y) and whose Rectangle collider is initialized with the given width and height. */
	public Human(float x, float y, float width, float height)
	{
		//Delegates the arguments to  the super-class constructor.
		super(x, y, width, height);
	}

	/** Updates the Human's game logic, such as his state time. */
	public abstract void update(float deltaTime);
	
	/** Gets the mode (EXPLORING or FIGHTING) of the GameObject */
	public Mode getMode() {
		return mode;
	}

	/** Sets the mode (EXPLORING or FIGHTING) of the GameObject */
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/** Gets the state (IDLE, JUMP, etc.) of the GameObject, used to dictate which animations to use. */
	public State getState() {
		return state;
	}

	/** Sets the state (IDLE, JUMP, etc.) of the GameObject, used to dictate which animations to use. Also sets the stateTime of the GameObject back to zero. */
	public void setState(State state) {
		this.state = state;
		
		//Re-sets the state time since the user has just changed states.
		this.stateTime = 0;
	}

	/** Gets the direction (LEFT or RIGHT) that the GameObject is facing. */
	public Direction getDirection() {
		return direction;
	}

	/** Sets the direction (LEFT or RIGHT) that the GameObject is facing. */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
}
