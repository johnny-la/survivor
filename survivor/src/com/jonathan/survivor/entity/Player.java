package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.InteractiveObject.InteractiveState;
import com.jonathan.survivor.inventory.Axe;
import com.jonathan.survivor.inventory.Bullet;
import com.jonathan.survivor.inventory.Inventory;
import com.jonathan.survivor.inventory.Loadout;
import com.jonathan.survivor.inventory.MeleeWeapon;
import com.jonathan.survivor.inventory.RangedWeapon;
import com.jonathan.survivor.inventory.Rifle;
import com.jonathan.survivor.math.Line;
import com.jonathan.survivor.math.Rectangle;
import com.jonathan.survivor.math.Vector2;

public class Player extends Human
{
	/** Stores the width and height of the player's rectangle collider in world units. */
	public static final float COLLIDER_WIDTH = 1.56f;
	public static final float COLLIDER_HEIGHT = 2.5f;
	
	/** Holds the player's default health. */
	public static final float DEFAULT_HEALTH = 100;
	
	/** Stores the maximum walk speed of the player in the horizontal direction. */
	public static final float MAX_WALK_SPEED = 6f;
	
	/** Stores the jump speed of the player in the vertical direction when in EXPLORING mode. */
	public static final float EXPLORATION_JUMP_SPEED = 10.7f;
	/** Stores the jump speed of the player in the vertical direction when in COMBAT mode. */
	public static final float COMBAT_JUMP_SPEED = 18.0f;
	
	/** Stores the downwards speed at which the player falls through a TerrainLayer. */
	public static final float FALL_SPEED = -5;
	
	/** Holds the amount of damage delivered to a zombie when it is stomped on the head by the player. */
	private static final float HEAD_STOMP_DAMAGE = 25;
	
	/** Stores the speed at which the player jumps after hitting the zombie's head. */
	private static final float HEAD_STOMP_JUMP_SPEED = 15;
	
	/** Holds the amount of time that the player is invulnerable when hit. */
	public static final float INVULNERABLE_TIME = 3f;
	
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
		
		//Gives the player default health when instantiated.
		setHealth(DEFAULT_HEALTH);
	}
	
	/** Updates the player's internal game logic. */
	@Override
	public void update(float deltaTime)
	{
		//Updates the player's game logic, such as its stateTime and its collider's position.
		super.update(deltaTime);
	}

	/** Makes the player jump. */
	public void jump() 
	{
		//If the player is already jumping, don't make him jump again. Or, if his y-velocity is non-zero, he is in the air. Thus, he can't jump. Also,
		//if he is dead, he can't jump. Therefore, return the method.
		if(getState() == State.JUMP || getVelocity().y != 0 || isDead())
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
		else if(getMode() == Mode.COMBAT )
		{
			//Set the y-velocity of the player to the desired jump speed.
			setVelocity(0, COMBAT_JUMP_SPEED);
			//Set the state of the player to his jumping state, telling the world he is jumping.
			setState(State.JUMP);
		}
		
		//Make the player lose his target so that he stops walking to a specific GameObject after jumping.
		loseTarget();		
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
	
	/** Makes the player swing his melee weapon if he has one. */
	public void melee()
	{
		//If the player is dead, he can't melee. Therefore, return the method.
		if(isDead())
			return;
		
		//If the player is in combat mode
		if(getMode() == Mode.COMBAT)
		{
			//If the player has a melee weapon and is not meleeing already or jumping or being hit
			if(hasMeleeWeapon() && getState() != State.MELEE && getState() != State.JUMP && getState() != State.HIT)
			{
				//Switch to MELEE state so that the MELEE animation plays.
				setState(State.MELEE);
			}
		}
	}
	
	/** Makes the player start to charge his gun. Call only once, when the player starts charging his ranged weapon. */
	public void charge()
	{
		//If the player doesn't have a ranged weapon equipped, or doesn't have bullets in his inventory, he can't charge his ranged weapon. Therefore, return this method.
		if(!hasRangedWeapon() || !hasBullets())
			return;
		
		//Set the player to CHARGE_START state, telling the player to pull out his gun before charging it. When done, the player will switch to CHARGE state.
		setState(State.CHARGE_START);
	}
	
	/** Makes the player fire his ranged weapon */
	public void fire() 
	{
		//If the player does not have a ranged weapon, or if he is dead, he cannot fire a gun. Therefore, return this method.
		if(!hasRangedWeapon() || isDead())
			return;
		
		//If the player is in CHARGE_START state, the player is trying to fire his gun before he pulled it out. This cannot be done
		if(getState() == State.CHARGE_START)
		{
			//Thus, set the player back to IDLE state.
			setState(State.IDLE);
		}
		//Else, if the player is not in CHARGE_STATE, but is in CHARGE state, he can fire his ranged weapon.
		else if(getState() == State.CHARGE)
		{
			//Fire the player's ranged weapon at the zombie that he's fighting. If the weapon's crosshair does not hit the zombie, the method returns.
			fireWeapon(getZombieToFight());
		}
		
	}

	/** Deals damage to the zombie with the player's melee weapon. Only deals damage if the player's melee weapon is colliding with the given zombie. */
	public void meleeHit(Zombie zombie) 
	{
		//If the zombie is invulnerable, he can't get hit. Also, if the melee weapon is not intersecting the zombie, the zombie is not being touched by the weapon.
		//Therefore, return the method.
		if(zombie.isInvulnerable() || !getMeleeWeaponCollider().intersects(zombie.getCollider()))
			return;
		
		//Deals damage to the zombie according to the meleeWeapon's damage value.
		zombie.takeDamage(loadout.getMeleeWeapon().getDamage());
		
		//Checks if the zombie is dead. If so, play the KO animation.
		checkDead(zombie);
	}

	/** Fire the player's currently equipped ranged weapon at the given zombie. */
	private void fireWeapon(Zombie zombie) 
	{
		//If the weapon's crosshair intersects with the zombie's collider, the weapon has a change of hitting the zombie. The player must also have bullets.
		if(getCrosshair().intersects(zombie.getCollider()) && hasBullets())
		{
			//Computes a random value between 0.9 and 1 which will dictate if the player's ranged weapon misses.
			float rand = 0.75f + (float)Math.random()*0.25f;
			
			//If the random number is less than the percent charge completion of the player's weapon, odds are in the player's favour. The bullet has hit the zombie.
			if(rand < getChargeCompletion())
			{
				//Deal damage to the zombie according to the strength of the player's ranged weapon.
				zombie.takeDamage(getRangedWeapon().getDamage());
				
				//Checks if the zombie is dead. If so, play the KO animation.
				checkDead(zombie);
			}
		}
		
		//Use one bullet in the player's inventory.
		useBullets(1);
		
		//Tell the player that he is firing his gun, making his FIRE animation play. The player had to be in CHARGE state to call getChargeCompletion().
		setState(State.FIRE);
		
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
	
	/** Called when the player hits a zombie's head. Deals damage to this zombie and changes its state. */
	public void hitHead(Zombie zombie) 
	{
		//If the zombie is invulnerable, he can't get hit. Therefore, return the method.
		if(zombie.isInvulnerable())
			return;
		
		//Sets the y-velocity of the player to slightly jump after hitting the zombie's head.
		setVelocityY(HEAD_STOMP_JUMP_SPEED);
		
		//Tell the player to play his double jump animation.
		setState(State.DOUBLE_JUMP);
		
		//Deals damage to the zombie according to the head stomp constant.
		zombie.takeDamage(HEAD_STOMP_DAMAGE);
		
		//Checks if the zombie is dead. If so, play the KO animation.
		checkDead(zombie);
	}
	
	/** Checks if the zombie is dead. If so, plays the KO animation. */
	private void checkDead(Zombie zombie) 
	{
		//If the zombie is dead
		if(zombie.isDead())
		{
			//Tell the world to play the KO animation.
			playerListener.playKoAnimation();
		}
	}
	
	/** Uses a given amount of bullets inside the player's inventory. */
	private void useBullets(int quantity)
	{
		//Remove the given amount of bullets from the player's inventory.
		inventory.addItem(Bullet.class, -quantity);
	}
	
	/** Returns true if the player has bullets in his inventory. */
	public boolean hasBullets() 
	{
		//If the value for the Bullet key inside the inventory is not null, the player has bullets in his inventory.
		return inventory.getItemMap().get(Bullet.class) != null;
	}
	
	/** Returns a float between 0 and 1 representing the charge completion of the player's ranged weapon. 1 means that the weapon is done charging completely. */
	public float getChargeCompletion()
	{
		//If the player is not charging his gun, this method will return incorrect values based on the stateTime since the player is not charging his ranged weapon.
		if(getState() != State.CHARGE)
			return 0;
			
		//Returns a percent completion of the player charging his gun. Computes a normalized value between 0 and 1, where 1 means that the gun has finished charging.
		float chargeRate = getStateTime() / getRangedWeapon().getChargeTime();
		//Cap the charge rate at 1.
		chargeRate = (chargeRate > 1)? 1:chargeRate;
		
		//Returns the charge completion of the player's ranged weapon.
		return chargeRate;
	}
	
	/** Regenerates the player to default health. */
	public void regenerate()
	{
		//Give the player his default health back.
		setHealth(DEFAULT_HEALTH);
	}
	
	/** Overrides the takeDamage() method to take note of when the player dies, in order to display the KO animation. */
	@Override 
	public void takeDamage(float amount)
	{
		//Deal damage to the player
		super.takeDamage(amount);
		
		//If the player died while taking damage
		if(getState() == State.DEAD)
		{
			//Tell the World instance to play the KO animation. When the animation is done, the "Game Over" screen will be displayed.
			playerListener.playKoAnimation();
		}
	}
	
	/** Makes the player lose all of the items in his inventory. */
	public void loseLoot() 
	{
		//Clears the player's inventory.
		inventory.clear();
		
		//Clear the player's loadout to ensure that he loses all his weapons.
		loadout.clear();
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
	
	/** Returns true if the player has won the game. */
	public boolean didWin()
	{
		//If the player is in TELEPORT state, he has won the game.
		return getState() == State.TELEPORT;
	}
	
	/** Override the canTarget method as always returning false since the Player can never be targetted. */
	@Override
	public boolean canTarget()
	{
		//The player can never be targetted.
		return false;
	}
	
	/** Returns the melee weapon that the player has equipped. */
	public MeleeWeapon getMeleeWeapon()
	{
		//Returns the melee weapon stored in the player's loadout, which is the one that the player has equipped.
		return getLoadout().getMeleeWeapon();
	}
	
	/** Returns the ranged weapon that the player has equipped. */
	public RangedWeapon getRangedWeapon()
	{
		//Returns the ranged weapon stored in the player's loadout, which is the one that the player has equipped.
		return getLoadout().getRangedWeapon();
	}
	
	/** Returns true if the player has a melee weapon equipped. */
	public boolean hasMeleeWeapon()
	{
		//Returns true if the melee weapon in the player's loadout is not null.
		return loadout.getMeleeWeapon() != null;
	}
	
	/** Returns true if the player has a ranged weapon equipped. */
	public boolean hasRangedWeapon()
	{
		//Returns true if the ranged weapon in the player's loadout is not null. If not, the player has a ranged weapon equipped.
		return loadout.getRangedWeapon() != null;
	}
	
	/** Returns true if the player has his ranged weapon out and visible. */
	public boolean hasRangedWeaponOut() 
	{
		//Returns true if the player is in any state which requires his ranged weapon to be visible.
		return getState() == State.CHARGE_START || getState() == State.CHARGE || getState() == State.FIRE;
	}
	
	/** Returns the Collider of the player's melee weapon. Allows to test if the player has hit a zombie with his weapon. */
	public Rectangle getMeleeWeaponCollider() 
	{
		//Returns the collider attached to the player's melee weapon.
		return loadout.getMeleeWeapon().getCollider();
	}
	
	/** Returns the crosshair line, which dictates where the player's ranged weapon will fire and where the bullet will travel. */
	public Line getCrosshair()
	{
		//Returns the crosshair point of the ranged weapon the player has equipped.
		return getRangedWeapon().getCrosshair();
	}

	/** Returns the position where the weapon crosshair should be placed on the player in world units. This is usually the tip of the player's ranged weapon. */
	public Vector2 getCrosshairPoint()
	{
		//Returns the crosshair point of the ranged weapon the player has equipped.
		return getRangedWeapon().getCrosshairPoint();
	}
	
	/** Makes the player invulnerable from attacks for a given amount of seconds. */
	@Override
	public void makeInvulnerable()
	{
		//Makes the player invulnerable for a given amount of seconds from the stored constant.
		setInvulnerabilityTime(INVULNERABLE_TIME);
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
