package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;

public class Player extends Human
{
	/** Stores the width and height of the player's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.56f;
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Stores the maximum walk speed of the player in the horizontal direction. */
	public static final float MAX_WALK_SPEED = 4f;
	
	public static final float WALK_ACCELERATION = 1;
	/** Stores the jump speed of the player in the vertical direction. */
	public static final float JUMP_SPEED = 10.7f;
	/** Stores the downwards speed at which the player falls through a TerrainLayer. */
	public static final float FALL_SPEED = -5;
	
	/** Creates a player whose bottom-center is at position (0, 0). */
	public Player()
	{
		this(0, 0);
	}
	
	/** Creates a player whose bottom-center is at position (x, y). */
	public Player(float x, float y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Creates the Spine skeleton for the player using the PlayerSkeletonData.
		setSkeleton(new Skeleton(Assets.instance.playerSkeletonData));
		
		//The player is always in exploration mode by default and not in fighting mode.
		setMode(Mode.EXPLORING);
		
		setState(State.SPAWN);
	}
	
	public void update(float deltaTime)
	{
		//Update the position of the player according to his velocity and acceleration.
		updatePosition(deltaTime);
		//Update the position of the collider to follow the player.
		updateCollider();
		
		//Update the stateTime.
		stateTime += deltaTime;
	}

	/** Makes the player jump. */
	public void jump() 
	{
		//If the player is in exploration mode, use a different jump speed.
		if(getMode() == Mode.EXPLORING)
		{
			//Set the y-velocity of the player to the desired jump speed.
			setVelocity(0, JUMP_SPEED);
			//Set the state of the player to his jumping state, telling the world he is jumping.
			setState(State.JUMP);
			
			//Move the player up a terrain cell. Lets the player keep track of which cell he is in for the terrain level.
			getTerrainCell().moveUp();
		}
		
	}

	/** Makes the player fall through one layer. */
	public void fall() 
	{
		//Make the player fall at his fall-speed in the vertical direction.
		setVelocity(0, FALL_SPEED);
		
		//Tell the player he is falling
		setState(State.FALL);
		
		//Move the player down a terrain cell.
		getTerrainCell().moveDown();
		
	}

	/** Moves the player to the right by setting his x-velocity to his walk speed. */
	public void moveRight() 
	{
		//Set the player's x-velocity to his walk speed.
		//getVelocity().x = (getVelocity().x > MAX_WALK_SPEED)? MAX_WALK_SPEED:getVelocity().x;
		
		getAcceleration().x = WALK_ACCELERATION;
		//Tell the player he is in walking state.
		setState(State.WALK);
	}
	
	/** Moves the player to the left by setting his x-velocity to the negative of his walk speed. */
	public void moveLeft() 
	{
		//Set the player's x-velocity to the negative of his walk speed.
	//getVelocity().x = -WALK_SPEED;
		
		//Tell the player he is in walking state.
		setState(State.WALK);
	}
	
	public void stopWalking()
	{
		//Stop the player from moving in the x-direction.
		getVelocity().x = 0;
		
		//Tell the player he is now in IDLE state, since he has stopped walking.
		setState(State.IDLE);
	}

}
