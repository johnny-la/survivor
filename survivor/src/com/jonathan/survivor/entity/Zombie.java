package com.jonathan.survivor.entity;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationState.AnimationStateListener;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.inventory.Iron;
import com.jonathan.survivor.inventory.Water;
import com.jonathan.survivor.math.Rectangle;

public class Zombie extends Human implements Clickable
{
	/** Stores the width and height of the zombie's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.50f;	//Default: 1.56f
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Stores the width and height of the zombie's charge collider in world units. The charge collider dictates the region where the zombie can hit the player while charging. */
	public static final float CHARGE_COLLIDER_WIDTH = 3.5f;
	public static final float CHARGE_COLLIDER_HEIGHT = 1.8f;
	
	/** Stores the walk speed of the zombie in the horizontal direction. */
	public static final float NORMAL_WALK_SPEED = 2.2f;
	
	/** Stores the walk speed of the zombie in the horizontal direction when in COMBAT mode. */
	public static final float COMBAT_WALK_SPEED = 8f;
	
	/** Holds the walking speed of the zombie when he is following the player. */
	public static final float ALERTED_WALK_SPEED = 2.7f;

	/** Stores the horizontal speed of the zombie when he is charging. */
	public static final float CHARGE_WALK_SPEED = 10f;
	
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
	
	/** Stores the collider which goes around the zombie when he's charging. Determines if the zombie has hit the player. */
	private Rectangle chargeCollider;
	/** Holds the collider mapped to the zombie's arms. Used to dictate whether the zombie has hit a player with his arms. */
	private Rectangle armCollider;
	
	/** Stores the bones which controls the zombie's hands. Allows to compute the position and size of the arm's collider. */
	private Bone rightHandBone;
	private Bone leftHandBone;
	
	/** Holds the HashMap of possible items that can be dropped from scavenging the Interactive GameObject. Key is the type 
	 *  of item, and Float is the probability of it being dropped from 0 (least probable) to 1 (most probable). */
	private HashMap<Class, Float> itemProbabilityMap;
	
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
		
		//Sets up the probability map which dictates which items the zombie will drop.
		setupItemProbabilityMap();
	}
	
	/** Called on zombie creation in order to populate the HashMap which dictates the probability of certain items dropping when the zombie is killed. */
	private void setupItemProbabilityMap()
	{
		//Creates the item probability map where the key dictates which item can drop once the zombie is destroyed, and the float is a number from 0 to 1
		//dicatating the probability of that item being dropped. Note that 1 means that the item will be dropped every time a zombie is destroyed.
		HashMap<Class, Float> probabilityMap = new HashMap<Class, Float>();
		
		//Adds the items which will be dropped from the zombie once it is killed.
		probabilityMap.put(Iron.class, 1f);
		
		//Sets the itemProbabilityMap of the tree to the probability map created in this method. Tells the tree which items will be dropped once it destroyed.
		setItemProbabilityMap(probabilityMap);
	}
	
	/** Updates the various colliders mapped to the zombie. */
	public void updateColliders() 
	{
		//Gets the zombie's skeleton.
		Skeleton skeleton = getSkeleton();
		
		//Gets the x-position of the left-most hand in world units.
		float handXLeft = Math.min(skeleton.getX() + rightHandBone.getWorldX(), skeleton.getX() + leftHandBone.getWorldX());
		//Gets the y-position of the bottom-most hand in world coordinates.
		float handYBottom = Math.min(skeleton.getY() + rightHandBone.getWorldY(), skeleton.getY() + leftHandBone.getWorldY());
		
		//Computes the width and height between the zombie's two hands.
		float handSeparationX = Math.abs(rightHandBone.getWorldX() - leftHandBone.getWorldX());
		float handSeparationY = Math.abs(rightHandBone.getWorldY() - leftHandBone.getWorldY());
		
		//Sets the bottom-left position of the arm collider to the right position in world units.
		armCollider.setPosition(handXLeft, handYBottom);
		//Modifies the size of the arm's collider. The collider goes from the center of one of the zombie's hands to the center of the other.
		armCollider.setSize(handSeparationX,handSeparationY);
		
		//Sets the bottom-left position of the charge collider so as to follow the zombie's left hand.
		chargeCollider.setPosition(handXLeft, getY());
		
		//Sets the size of the charge collider to spawn the width between the zombie's hands, and to be the same height as the zombie.
		chargeCollider.setSize(handSeparationX, COLLIDER_HEIGHT);
		
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
			//If the zombie is in EXPLORATION state
			if(getMode() == Mode.EXPLORING)
			{
				//If the zombie is alerted, he walks faster
				if(isAlerted())
					//Set the zombie's walk speed to the correct pre-defined constant
					setWalkSpeed(ALERTED_WALK_SPEED);
				//Else, if the zombie is not alerted, set him to walk at regular speeds
				else
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
	
	/** Called when the Zombie instance is put back into a pool. All his data fields must be reset to default. */
	@Override 
	public void reset()
	{
		//Resets the zombie's data fields to default
		alerted = false;
		targetted = false;
		setHealth(DEFAULT_HEALTH);
	}

}
