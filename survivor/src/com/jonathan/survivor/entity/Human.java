package com.jonathan.survivor.entity;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.jonathan.survivor.entity.InteractiveObject.InteractiveState;

/**
 * A Human instance is a GameObject that can move around and that either has some form of AI or is controlled by the player. 
 * 
 * Implements the Poolable instance. Its reset() method gets called whenever the Human is placed back into a pool.
 * 
 * @author Jonathan
 *
 */

public abstract class Human extends GameObject implements Poolable
{
	public enum Mode {
		EXPLORING, COMBAT
	}
	
	/** Stores the mode of the Human (EXPLORING or FIGHTING) used to determine how the human's logic should be processed. */
	protected Mode mode;
	
	public enum State {
		SPAWN, IDLE, WALK, JUMP, DOUBLE_JUMP, FALL, CHOP_TREE, ENTER_COMBAT, MELEE, CHARGE_START, CHARGE, SMASH, FIRE, ALERTED, HIT, HIT_HEAD, DEAD, TELEPORT, WIN
	}
	
	/** Stores the state of the Human (IDLE, WALK, etc.), usually used to dictate which animations to play. */
	protected State state;
	
	/** Stores the previous state of the human. Used in humans' renderer. Animations are only changed if the human's state changed from the previous render call. */
	private State previousState;
	
	public enum Direction {
		LEFT, RIGHT
	}
	
	/** Stores the direction the Human is facing */
	protected Direction direction = Direction.RIGHT;	//RIGHT by default. Avoids null errors.
	
	/** Stores the GameObject where the human is trying to walk to. Null if the human has no target. */
	private GameObject target;
	/** Holds true if the Human has reached his target. */
	private boolean targetReached;
	
	/** Stores the human's health. Once it drops below zero, the human is dead. */
	private float health;
	
	/** Holds the amount of time that the Human is invulnerable to attacks. */
	private float invulnerabilityTime;
	
	/** Holds the walking speed of the human in the x-direction in meters/second. */
	private float walkSpeed;
	
	/** Creates a Human GameObject instance whose bottom-center is at (x,y) and whose Rectangle collider is initialized with the given width and height. */
	public Human(float x, float y, float width, float height)
	{
		//Delegates the arguments to  the super-class constructor.
		super(x, y, width, height);
	}

	/** Updates the Human's game logic, such as his state time. */
	public void update(float deltaTime)
	{
		//Update the position of the Human according to his velocity and acceleration.
		updatePosition(deltaTime);
		//Update the position of the collider to follow the Human.
		updateCollider();
		
		//Update the stateTime.
		stateTime += deltaTime;
		
		//Update the invulnerability time. This essentially removes time off of the human's invulnerability.
		invulnerabilityTime -= deltaTime;
	}
	
	/** Lose the human's current target so that he stops walking towards his target. */
	public void loseTarget()
	{
		//Makes the player's current target null.
		this.target = null;
		
		//The human has not reached his target as he no longer has one.
		targetReached = false;
	}
	
	/** Returns true if the given human is facing the other human, and this human can thus see the other one. */
	public boolean isFacing(Human other)
	{
		//If both the human and the other human are facing right
		if(getDirection() == Direction.RIGHT && other.getDirection() == Direction.RIGHT)
		{
			//If the other human is to the right of this human
			if(other.getX() > getX())
				return true;	//Return true, since the human can see the other human.
		}
		//If the human is facing the right, and the human is facing the left
		else if(getDirection() == Direction.RIGHT && other.getDirection() == Direction.LEFT)
		{
			//If the human is to the left of the other human, the human is facing the other human, and can see him.
			if(getX() < other.getX())
				return true;	//Return true, since the human can see the other human.
		}
		else if(getDirection() == Direction.LEFT && other.getDirection() == Direction.LEFT)
		{
			//If the other human is to the left of the human, the human is facing the other human.
			if(other.getX() < getX())
				return true;	//Return true, since the human can see the other human.
		}
		//Else, if the human is facing the left, and the other human is facing the right
		else if(getDirection() == Direction.LEFT && other.getDirection() == Direction.RIGHT)
		{
			//If the human is to the right of the other human, the human can see the other human.
			if(getX() > other.getX())
				return true;	//Return true, since the human can see the other human.
		}
		
		return false; //If this statement is reached, the human is not facing the other human.
	}
	
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
		//Ensure that the state is not the same as the current state to avoid resetting stateTime
		if(this.state != state)
		{
			//Sets the new state.
			this.state = state;
		
			//Re-sets the state time since the user has just changed states.
			this.stateTime = 0;	
		}
	}
	
	/** Retrieves the previous state of the Human. Used to determine whether or not a GameObject's state changes from one render call to the next. */
	public State getPreviousState() {
		return previousState;
	}

	/** Sets the previous state of the Human. Used to determine whether or not a GameObject's state changes from one render call to the next. */
	public void setPreviousState(State previousState) {
		this.previousState = previousState;
	}

	/** Gets the direction (LEFT or RIGHT) that the GameObject is facing. */
	public Direction getDirection() {
		return direction;
	}

	/** Sets the direction (LEFT or RIGHT) that the GameObject is facing. */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/** Sets the target where the human wants to walk to. */
	public void setTarget(GameObject target) 
	{
		//Lose the current target before setting a new target.
		loseTarget();
		
		//If the human is targetting a zombie
		if(target instanceof Zombie)
			//Tell the zombie he is being targetted by the human. Its color will be changed to indicate that he is being targetted.
			((Zombie)target).setTargetted(true);
		
		//Sets the new target for the Human.
		this.target = target;
	}
	
	/** Gets the target where the human wants to walk to. */
	public GameObject getTarget() 
	{
		return target;
	}

	/** Returns true if the human has reached his target. */
	public boolean isTargetReached() {
		return targetReached;
	}

	/** Sets whether or not the human has reached his target. */
	public void setTargetReached(boolean targetReached) {
		this.targetReached = targetReached;
	}
	
	/** Gets the human's walking speed in the x-direction. */
	public float getWalkSpeed() {
		return walkSpeed;
	}

	/** Sets the human's walking speed in the x-direction in meters/second. */
	public void setWalkSpeed(float walkSpeed) {
		this.walkSpeed = walkSpeed;
	}
	
	/** Deals damage to the human by removing the given amount from its health. */
	public void takeDamage(float damage)
	{
		//Subtract the tree's health by the damage dealt.
		health -= damage;
		
		//If the zombie's health has dropped below zero
		if(isDead())
		{
			//Make the zombie play its DEAD animation
			setState(State.DEAD);
		}
		//Else, if the zombie is not dead yet, simply make the zombie show his HIT animation
		else
		{
			//Tell the zombie he was hit
			setState(State.HIT);
		}
		
		//Make the Human invulnerable after being hit.
		makeInvulnerable();
	}
	
	/** Makes the Human invulnerable for a given amount of seconds. */
	public abstract void makeInvulnerable();
	
	/** Returns true if the Human is invulnerable to incoming attacks. */
	public boolean isInvulnerable()
	{
		//If the invulnerabilityTime is greater than zero, the Human is still invulnerable. 
		return invulnerabilityTime > 0;
	}
	
	/** Gets the amount of time that the Human is invulnerable for. */
	public float getInvulnerabilityTime() {
		return invulnerabilityTime;
	}

	/** Sets the amount of time that the Human is invulnerable for. */
	public void setInvulnerabilityTime(float invulnerabilityTime) {
		this.invulnerabilityTime = invulnerabilityTime;
	}
	
	/** Gets the human's health. */
	public float getHealth() {
		return health;
	}

	/** Sets the human's health. */
	public void setHealth(float health) {
		this.health = health;
	}
	
	/** Returns true if the Human's health has dropped to zero or below. */
	public boolean isDead()
	{
		//Returns true if the Human's health is at zero or lower.
		return health <= 0;
	}
	
	/** Called whenever this box GameObject has been pushed back into a pool. In this case, we reset the box's state back to default. */
	@Override
	public void reset()
	{
		//Tell the Human it has just spawned, in order for its renderer to know to reset the Human back to IDLE state.
		setState(State.SPAWN);
		
		//Zero the human's velocity and acceleration so that he doesn't start moving when he respawns.
		setVelocity(0,0);
		setAcceleration(0,0);
	}
}
