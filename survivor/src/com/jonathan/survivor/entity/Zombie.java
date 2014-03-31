package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.attachments.RegionAttachment;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.math.Rectangle;

public class Zombie extends Human implements Clickable
{
	/** Stores the width and height of the zombie's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.56f;
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Stores the walk speed of the zombie in the horizontal direction. */
	public static final float NORMAL_WALK_SPEED = 2.2f;
	
	/** Stores the walk speed of the zombie in the horizontal direction when in COMBAT mode. */
	public static final float COMBAT_WALK_SPEED = 2.5f;
	
	/** Holds the walking speed of the zombie when he is following the player. */
	public static final float ALERTED_WALK_SPEED = 2.7f;

	/** Stores the horizontal speed of the zombie when he is charging. */
	public static final float CHARGE_WALK_SPEED = 9f;
	
	/** Holds the default amount of damage the CHARGE attack deals to the player. */
	public static final float DEFAULT_CHARGE_DAMAGE = 50;
	
	/** Stores the multiplier of the zombie's walk animation when he is alerted and is following the player. */
	public static final float ALERTED_ANIM_SPEED = ALERTED_WALK_SPEED / NORMAL_WALK_SPEED;
	
	/** Stores the jump speed of the zombie in the vertical direction. */
	public static final float JUMP_SPEED = 10.7f;
	/** Stores the downwards speed at which the zombie falls through a TerrainLayer. */
	public static final float FALL_SPEED = -5;
	
	/** Holds the amount of time that the zombie is invulnerable when hit. */
	public static final float INVULNERABLE_TIME = 3f;
	
	/** Holds the player's default health. */
	public static final float DEFAULT_HEALTH = 100;
	
	/** Holds true if the Zombie is aware that the Player is within range of him. Makes him go towards the player. */
	private boolean alerted;
	
	/** Stores true if the Zombie is being targetted by the player, and the player is trying to walk towards it. */
	private boolean targetted;
	
	/** Stores the width and height of the zombie's charge collider in world units. The charge collider dictates the region where the zombie can hit the player while charging. */
	public static final float CHARGE_COLLIDER_WIDTH = 4f;
	public static final float CHARGE_COLLIDER_HEIGHT = 1.8f;
	
	/** Stores the collider which goes around the zombie when he's charging. Determines if the zombie has hit the player. */
	private Rectangle chargeCollider;
	/** Holds the collider mapped to the zombie's arms. Used to dictate whether the zombie has hit a player with his arms. */
	private Rectangle armCollider;
	
	/** Stores the bones which controls the zombie's hands. Allows to compute the position and size of the arm's collider. */
	private Bone rightHandBone;
	private Bone leftHandBone;
	
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
		
		//Stores the bone controlling the zombie's hands
		rightHandBone = getSkeleton().findBone("R_Hand");
		leftHandBone = getSkeleton().findBone("L_Hand");
		
		//Instantiates a Rectangle mapped to the zombie to determine if he hit the player while charging.
		chargeCollider = new Rectangle(CHARGE_COLLIDER_WIDTH, CHARGE_COLLIDER_HEIGHT);
		//Creates a default rectangle collider for the zombie's arms.
		armCollider = new Rectangle();
		
		//The zombie is always in exploration mode by default and not in fighting mode.
		setMode(Mode.EXPLORING);
		
		//Set the zombie to SPAWN state once instantiated.
		setState(State.SPAWN);
		
		//Sets the zombie's walking speed to default.
		setWalkSpeed(NORMAL_WALK_SPEED);
		
		//Give the zombie default health when instantiated.
		setHealth(DEFAULT_HEALTH);
	}
	
	/** Updates the various colliders mapped to the zombie. */
	public void updateColliders() 
	{
		//Gets the zombie's skeleton.
		Skeleton skeleton = getSkeleton();
		
		//Sets the bottom-left position of the charge collider so as to follow the zombie's position
		chargeCollider.setPosition(getX() - CHARGE_COLLIDER_WIDTH/2, getY());
		
		//Gets the x-position of the left-most hand in world units.
		float leftX = Math.min(skeleton.getX() + rightHandBone.getWorldX(), skeleton.getX() + leftHandBone.getWorldX());
		//Gets the y-position of the bottom-most hand in world coordinates.
		float bottomY = Math.min(skeleton.getY() + rightHandBone.getWorldY(), skeleton.getY() + leftHandBone.getWorldY());
		
		//Computes the width and height between the zombie's two hands.
		float width = Math.abs(rightHandBone.getWorldX() - leftHandBone.getWorldX());
		float height = Math.abs(rightHandBone.getWorldY() - leftHandBone.getWorldY());
		
		//Sets the bottom-left position of the arm collider to the right position in world units.
		armCollider.setPosition(leftX, bottomY);
		//Modifies the size of the arm's collider. The collider goes from the center of one of the zombie's hands to the center of the other.
		armCollider.setSize(width,height);
		
	}

	/** Updates the zombie's internal game logic. */
	public void update(float deltaTime)
	{
		//Update the zombie and his game logic.
		super.update(deltaTime);
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
	
	/** Make the zombie charge hit the player. */
	public void chargeHit(Player player) 
	{
		//If the player is invulnerable, he can't get hit. Therefore, return the method.
		if(player.isInvulnerable())
			return;
		
		//Deal damage to the player according to the pre-defined constant.
		player.takeDamage(DEFAULT_CHARGE_DAMAGE);
		
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
		//The zombie can always be targetted.
		return true;
	}
	
	//Changes the zombie's state. Overridden to add functionality.
	@Override
	public void setState(State state)
	{
		//Change the zombie's state.
		super.setState(state);
		
		//If the zombie is walking.
		if(state == State.WALK)
		{
			//If the zombie is alerted, he walks faster
			if(isAlerted())
				//Set the zombie's walk speed to the correct pre-defined constant
				setWalkSpeed(ALERTED_WALK_SPEED);
			//Else, if the zombie is not alerted, and doesn't want to follow the player, his speed is different
			else
			{
				//If the zombie is in EXPLORATION state
				if(getMode() == Mode.EXPLORING)
				{
					//Set the zombie's walk speed to the correct, pre-defined constant
					setWalkSpeed(NORMAL_WALK_SPEED);
				}
				//Else, if the zombie is in COMBAT state, he is trying to walk back to his starting point. Thus, his speed is faster.
				else if(getMode() == Mode.COMBAT)
				{
					//Set the zombie's walk speed to the correct, pre-defined constant
					setWalkSpeed(COMBAT_WALK_SPEED);
				}
				
			}
		}
		//Else, if the zombie is charging
		else if(state == State.CHARGE)
		{
			//Set the zombie's walk speed to the correct pre-defined constant
			setWalkSpeed(CHARGE_WALK_SPEED);
		}
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
				{
					//Set the zombie to IDLE state so that the ZombieManager knows to make him follow the player.
					setState(State.IDLE);
				}
				//Else, if the ENTER_COMBAT animation has just finished playing
				else if(getState() == State.ENTER_COMBAT)
				{
					//Set the zombie back to IDLE state so that his correct animation plays.
					setState(State.IDLE);
				}
				//Else, if the zombie has finished playing its charge taunting animation
				else if(getState() == State.CHARGE_START)
				{
					//Tell the zombie to charge at the player.
					setState(State.CHARGE);
				}
				//Else, if the player was hit
				else if(getState() == State.HIT)
				{
					//If the zombie is in EXPLORATION mode
					if(getMode() == Mode.EXPLORING)
					{
						
					}
					//Else, if the zombie is in COMBAT mode with the player.
					else if(getMode() == Mode.COMBAT)
					{
						//Set the zombie to WALK state, telling him to walk back to his starting position facing the player.
						setState(State.WALK);
						
						//Tell the zombie to walk to the RIGHT to go back to his original position.
						setDirection(Direction.RIGHT);
					}
				}
				//Else, if the player was hit in the head.
				else if(getState() == State.HIT_HEAD)
				{
					//If the zombie is in EXPLORATION mode
					if(getMode() == Mode.EXPLORING)
					{
						
					}
					//Else, if the zombie is in COMBAT mode with the player.
					else if(getMode() == Mode.COMBAT)
					{
						//Set the zombie to WALK state, telling him to walk back to his starting position facing the player.
						setState(State.WALK);
						
						//Tell the zombie to walk to the RIGHT to go back to his original position.
						setDirection(Direction.RIGHT);
					}
				}
				
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
		{
			//Set him to ALERTED state so that the correct animation plays.
			setState(State.ALERTED);
			
			//Makes the zombie walk faster if he is alerted
			setWalkSpeed(ALERTED_WALK_SPEED);
		}
		//Else, if the zombie is not alerted anymore
		else 
		{
			//Makes the zombie walk slower since he is no longer alerted
			setWalkSpeed(NORMAL_WALK_SPEED);
		}
	}
	
	/** Makes the zombie invulnerable from the player's attacks for a given amount of seconds. */
	@Override
	public void makeInvulnerable()
	{
		//Makes the zombie invulnerable for the given amount of seconds in the stored constant.
		setInvulnerabilityTime(INVULNERABLE_TIME);
	}

	/** Returns true if the zombie is being targetted by the player. If so, the player has clicked the zombie, and is walking towards it. */
	public boolean isTargetted() {
		return targetted;
	}

	/** Sets whether or not the zombie is being targetted by the player. If so, the player has clicked the zombie, and is walking towards it. */
	public void setTargetted(boolean targetted) {
		this.targetted = targetted;
	}
	
	/** Gets the collider mapped to the zombie when he's charging. Used to determine if the zombie has charge hit the player. */
	public Rectangle getChargeCollider() {
		return chargeCollider;
	}

	/** Sets the collider mapped to the zombie when he's charging. Used to determine if the zombie has charge hit the player. */
	public void setChargeCollider(Rectangle chargeCollider) {
		this.chargeCollider = chargeCollider;
	}
	
	/** Returns the collider mapped to the zombie's arms. Used to dictate if the zombie melee hit the player. */
	public Rectangle getArmCollider() {
		return armCollider;
	}

	/** Sets the collider mapped to the zombie's arms. Used to dictate if the zombie melee hit the player. */
	public void setArmCollider(Rectangle armCollider) {
		this.armCollider = armCollider;
	}
	
	/** Gets the bone mapped to the zombie's right hand. Allows to position the zombie's arm collider to dictate if the zombie hit the player. */
	public Bone getRightHandBone() {
		return rightHandBone;
	}

	/** Sets the bone mapped to the zombie's right hand. Allows to position the zombie's arm collider to dictate if the zombie hit the player. */
	public void setRightHandBone(Bone rightHandBone) {
		this.rightHandBone = rightHandBone;
	}
	
	/** Gets the bone mapped to the zombie's left hand. Allows to position the zombie's arm collider to dictate if the zombie hit the player. */
	public Bone getLeftHandBone() {
		return leftHandBone;
	}

	/** Sets the bone mapped to the zombie's left hand. Allows to position the zombie's arm collider to dictate if the zombie hit the player. */
	public void setLeftHandBone(Bone leftHandBone) {
		this.leftHandBone = leftHandBone;
	}

}
