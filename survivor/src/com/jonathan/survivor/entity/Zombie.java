package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.renderers.ZombieRenderer;

public class Zombie extends Human
{
	/** Stores the width and height of the zombie's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.56f;
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Stores the maximum walk speed of the zombie in the horizontal direction. */
	public static final float MAX_WALK_SPEED = 2.2f;
	
	/** Stores the jump speed of the zombie in the vertical direction. */
	public static final float JUMP_SPEED = 10.7f;
	/** Stores the downwards speed at which the zombie falls through a TerrainLayer. */
	public static final float FALL_SPEED = -5;
	
	/** Controls the zombie's animations. Allows for crossfading between animations. */
	private AnimationState animationState;
	
	/** Creates a zombie whose bottom-center is at position (0, 0). */
	public Zombie()
	{
		this(0, 0);
	}
	
	/** Creates a zombie whose bottom-center is at position (x, y). */
	public Zombie(float x, float y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Creates the Spine skeleton for the zombie using the ZombieSkeletonData.
		setSkeleton(new Skeleton(Assets.instance.zombieSkeletonData));
		
		//The zombie is always in exploration mode by default and not in fighting mode.
		setMode(Mode.EXPLORING);
		
		//Set the zombie to SPAWN state once instantiated.
		setState(State.SPAWN);
	}
	
	public void update(float deltaTime)
	{
		//Update the position of the zombie according to his velocity and acceleration.
		updatePosition(deltaTime);
		//Update the position of the collider to follow the zombie.
		updateCollider();
		
		//Update the stateTime.
		stateTime += deltaTime;
	}

	/** Makes the zombie jump. */
	public void jump() 
	{
		//If the zombie is in exploration mode, use a different jump speed.
		if(getMode() == Mode.EXPLORING)
		{
			//Set the y-velocity of the zombie to the desired jump speed.
			setVelocity(0, JUMP_SPEED);
			//Set the state of the zombie to his jumping state, telling the world he is jumping.
			setState(State.JUMP);
			
			//Move the zombie up a terrain cell. Lets the zombie keep track of which cell he is in for the terrain level.
			getTerrainCell().moveUp();
		}
		
		//Make the zombie lose his target so that he stops walking to a specific GameObject after jumping.
		loseTarget();		
	}

	/** Makes the zombie fall through one layer. */
	public void fall() 
	{
		//Make the zombie fall at his fall-speed in the vertical direction.
		setVelocity(0, FALL_SPEED);
		
		//Tell the zombie he is falling
		setState(State.FALL);
		
		//Move the zombie down a terrain cell.
		getTerrainCell().moveDown();
		
		//Make the zombie lose his target so that he stops walking to a specific GameObject after falling.
		loseTarget();
	}
	
	/** Called when the zombie loses his target. */
	@Override
	public void loseTarget()
	{
		//Store the zombie's old target
		GameObject oldTarget = getTarget();
		
		//Lose the target.
		super.loseTarget();
		
	}
	
	/** Override the canTarget method as always returning false since the Zombie can never be targetted. */
	@Override
	public boolean canTarget()
	{
		//The zombie can never be targetted.
		return false;
	}

	/** Retrieves the Spine AnimationState instance used to change the zombie's animations and control them. */
	public AnimationState getAnimationState() {
		return animationState;
	}

	/** Sets the Spine AnimationState instance used to modify the zombie's animations and control them. */
	public void setAnimationState(AnimationState animationState) {
		this.animationState = animationState;
	}

}