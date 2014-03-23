package com.jonathan.survivor.entity;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;

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
	
	/** Holds true if the Zombie is aware that the Player is within range of him. Makes him go towards the player. */
	private boolean alerted;
	
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
		
		//If the zombie's animation state has been populated with a valid animationState
		if(animationState != null)
			//Create the animation listener which listens to the zombie's animation events. Needed to be done after the 
			createAnimationListener();
	}

	/** Populates the zombie's animation listener if it doesn't already exist. Allows the zombie's events to be registered by this renderer.
	 *  Must be called after its animation state is created */
	private void createAnimationListener() 
	{
		//Creates a listener to listen for events coming from the zombie's animations.
		AnimationStateListener animationListener = new AnimationStateListener() {

			@Override
			public void event(int trackIndex, Event event) {
				
			}

			@Override
			public void complete(int trackIndex, int loopCount) {
				//If the zombie just completed his ALERTED animation
				if(getState() == State.ALERTED)
					//Set the zombie to IDLE state so that the ZombieManager knows to make him follow the player.
					setState(State.IDLE);
				
			}

			@Override
			public void start(int trackIndex) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void end(int trackIndex) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		//Register the AnimationStateListener to the zombie's AnimationState. This will delegate zombie animation events to the listener.
		getAnimationState().addListener(animationListener);
	}
	
	/** Returns true if the Zombie is aware that the Player is there. */
	public boolean isAlerted() {
		return alerted;
	}

	/** Sets whether or not the Zombie is aware of the player. If so, the Zombie walks towards the player */
	public void setAlerted(boolean alerted) 
	{
		this.alerted = alerted;
		
		//If the zombie is alerted
		if(alerted)
			//Set him to ALERTED state so that the correct animation plays.
			setState(State.ALERTED);
	}

}
