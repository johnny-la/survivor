package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.InteractiveObject.InteractiveState;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.inventory.Loadout;

public class Player extends Human
{
	/** Stores the width and height of the player's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.56f;
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Stores the maximum walk speed of the player in the horizontal direction. */
	public static final float MAX_WALK_SPEED = 6f;
	
	public static final float WALK_ACCELERATION = 1;
	/** Stores the jump speed of the player in the vertical direction when in EXPLORING mode. */
	public static final float EXPLORATION_JUMP_SPEED = 10.7f;
	/** Stores the downwards speed at which the player falls through a TerrainLayer. */
	public static final float FALL_SPEED = -5;
	
	/** Stores the jump speed of the player in the vertical direction when in COMBAT mode. */
	public static final float COMBAT_JUMP_SPEED = 19.0f;
	
	/** Stores the player's loadout, containing the player's active weapons. */
	private Loadout loadout;
	/** Holds the player's inventory, which contains all of the player's collected items. */
	private Inventory inventory;
	
	/** Holds the zombie that the player will fight once he enters combat mode. Convenience member variable to avoid large work-arounds. */
	private Zombie zombieToFight;
	
	/** Stores the PlayerListener instance where methods are delegated upon player events. */
	private PlayerListener playerListener;
	
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
		
		//Set the player to SPAWN state once instantiated.
		setState(State.SPAWN);
		
		//Sets the player's walking speed to default.
		setWalkSpeed(MAX_WALK_SPEED);
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
		//If the player is already jumping, don't make him jump again. Return the method.
		if(getState() == State.JUMP)
			return;
		
		//If the player is in exploration mode, use a different jump speed.
		if(getMode() == Mode.EXPLORING)
		{
			//Set the y-velocity of the player to the desired jump speed.
			setVelocity(0, EXPLORATION_JUMP_SPEED);
			//Set the state of the player to his jumping state, telling the world he is jumping.
			setState(State.JUMP);
			
			//Move the player up a terrain cell. Lets the player keep track of which cell he is in for the terrain level.
			getTerrainCell().moveUp();

		}
		//Else, if the player is in COMBAT mode
		else if(getMode() == Mode.COMBAT)
		{
			//Set the y-velocity of the player to the desired jump speed.
			setVelocity(0, COMBAT_JUMP_SPEED);
			//Set the state of the player to his jumping state, telling the world he is jumping.
			setState(State.JUMP);
		}
		
		//Make the player lose his target so that he stops walking to a specific GameObject after jumping.
		loseTarget();
		
		loadout.setMeleeWeapon(new Axe());
		
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
		
		//Make the player lose his target so that he stops walking to a specific GameObject after falling.
		loseTarget();
	}

	/** Makes the player start chopping a tree */
	public void chopTree()
	{
		//If the player has a melee weapon equipped
		if(loadout.getMeleeWeapon() != null)
		{
			//Start chopping the tree.
			setState(State.CHOP_TREE);
		}
	}
	
	/** Called when the player has hit the tree stored as his target. */
	public void hitTree() 
	{	
		//Retrieve the hit tree, which is the player's target.
		Tree tree = (Tree)getTarget();
		//Tell the tree it was hit so that it plays the correct animation.
		tree.setInteractiveState(InteractiveState.HIT);
		
		//Make the player's melee weapon deal damage to the tree
		loadout.getMeleeWeapon().hit(tree);
		
		//If the tree has been scavenged after dealing damage to it in the above statement
		if(tree.getInteractiveState() == InteractiveState.SCAVENGED)
		{
			//Tell the player to lose his target of the tree, since it has been destroyed
			loseTarget();
			
			//Set the player back to IDLE state.
			setState(State.IDLE);
			
			//Tell the world to spawn items at the destroyed tree's position.
			playerListener.scavengedObject(tree);
		}
	}
	
	/** Called when the player loses his target. */
	@Override
	public void loseTarget()
	{
		//Store the player's old target
		GameObject oldTarget = getTarget();
		
		//If it isn't null
		if(oldTarget != null)
		{
			//If it is an Interactive GameObject
			if(oldTarget instanceof InteractiveObject)
			{
				//Untarget the old target.
				((InteractiveObject) oldTarget).untargetted();
			}
			//Else, if a zombie target was lost
			else if(oldTarget instanceof Zombie)
			{
				//Tell the zombie that it is no longer targetted.
				((Zombie) oldTarget).setTargetted(false);
			}
		}
		
		//Lose the target.
		super.loseTarget();
		
	}
	
	/** Override the canTarget method as always returning false since the Player can never be targetted. */
	@Override
	public boolean canTarget()
	{
		//The player can never be targetted.
		return false;
	}

	/** Returns true if the player has a melee weapon equipped. */
	public boolean hasMeleeWeapon()
	{
		//Returns true if the melee weapon in the player's loadout is not null.
		return loadout.getMeleeWeapon() != null;
	}
	
	/** Retrieves the player's loadout containing the player's weapons. */
	public Loadout getLoadout() {
		return loadout;
	}

	/** Sets the player's loadout. */
	public void setLoadout(Loadout loadout) {
		this.loadout = loadout;
	}
	
	/** Gets the loadout which stores the items held by the player. */
	public Inventory getInventory() {
		return inventory;
	}

	/** Sets the loadout which stores the items held by the player. */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	/** Returns the zombie that the player should fight once he enters combat mode. Set when the player collides with a zombie. */
	public Zombie getZombieToFight() {
		return zombieToFight;
	}

	/** Sets the zombie the player should fight once he enters combat mode. Set when the player collides with a zombie. */
	public void setZombieToFight(Zombie zombieToFight) {
		this.zombieToFight = zombieToFight;
	}
	
	/** Sets the given listener to have its methods delegated by the player instance. */
	public void setListener(PlayerListener listener)
	{
		playerListener = listener;
	}

}
