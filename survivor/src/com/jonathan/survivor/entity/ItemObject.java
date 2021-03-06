package com.jonathan.survivor.entity;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.inventory.Item;

public class ItemObject extends GameObject implements Poolable
{
	/** Stores the width and height of an item GameObject's collider. This is the touchable region of the item. All Item GameObjects have the same collider size. */
	private static final float COLLIDER_WIDTH = 50 * Survivor.WORLD_SCALE;
	private static final float COLLIDER_HEIGHT = 50 * Survivor.WORLD_SCALE;
	
	/** Holds the minimum and maximum y-velocity of the Item GameObject when it is spawned. */
	private static final float MIN_Y_SPAWN_VELOCITY = 2.5f, MAX_Y_SPAWN_VELOCITY = 2.5f; 
	
	/** Holds the minimum and maximum y-velocity of the Item GameObject when it is spawned. */
	private static final float MIN_X_SPAWN_VELOCITY = 2.5f, MAX_X_SPAWN_VELOCITY = 2.5f; 
	
	/** Stores the different possible state of an item object that is dropped into the world. */
	public enum ItemState {
		SPAWN, GROUNDED, FLY, CLICKED
	}
	
	/** Stores the state of the item, which determines the animation it plays. */
	private ItemState itemState;
	
	/** Stores the Item held by the GameObject. */
	private Item item;
	
	/** Creates an ItemObject at bottom-center position (0,0). */
	public ItemObject()
	{
		this(0,0);
	}
	
	/** Creates an Item GameObject at the given bottom-center (x,y) position. */
	public ItemObject(int x, int y)
	{
		super(x, y, COLLIDER_WIDTH, COLLIDER_HEIGHT);
		
		//Creates a skeleton for the Item GameObject, which will display the GameObject in the world.
		setSkeleton(new Skeleton(Assets.instance.itemSkeletonData));
		
		//By default, the ItemObject is in SPAWN state, as it has just spawned.
		setItemState(ItemState.SPAWN);
	}

	@Override
	public void update(float deltaTime) 
	{
		//Update the amount of time the Item GameObject has been in its current state.
		stateTime += deltaTime;
		
		//Update the position of the Item GameObject in order to integrate acceleration and velocity.
		updatePosition(deltaTime);
		//Update the position of the collider to follow the GameObject's position.
		updateCollider();
		
	}
	
	/** Spawns the item at the given position. Gives the item a random upward velocity to simulate confetti explosion. 
	 *  The position is the bottom-center position of the gameObject.
	 * @param velocityMultiplier Scalar by which velocity is multiplied, to allow certain items to fly further. Allows items to be spread apart if many are spawned. 
	 * @param direction Specifies the direction in which the items fly when spawned
	 * */
	public <T extends Item> void spawn(float x, float y, float velocityMultiplier, Direction direction)
	{
		//Sets the item represented by this Item GameObject. This tells the GameObject which item to respresent.
		setItem(item);
		
		//Sets the bottom-center (x,y) position of the item object to the given position.
		setPosition(x, y);
		
		//Finds the spawning x and y-velocity of the Item GameObject. Finds a value between the min and max
		//given by the pre-defined constants.
		float xVel = (float) (MIN_X_SPAWN_VELOCITY + Math.random()*(MAX_X_SPAWN_VELOCITY-MIN_X_SPAWN_VELOCITY));
		float yVel = (float) (MIN_Y_SPAWN_VELOCITY + Math.random()*(MAX_Y_SPAWN_VELOCITY-MIN_Y_SPAWN_VELOCITY));
		
		//Applies the multiplier to the item's velocity. Allows certain items to fly further than others.
		xVel *= velocityMultiplier;
		yVel *= velocityMultiplier;
		
		//The Item GameObject is supposed to fly to the left
		if(direction == Direction.LEFT)
			//Inverse the horizontal velocity of the ItemObject.
			xVel *= -1;
		
		//Shoots the ItemObject upwards.
		setVelocity(xVel, yVel);
	}

	/** An ItemObject can never be targetted. */
	@Override
	public boolean canTarget() {
		//Return false as an ItemObject can't be targetted.
		return false;
	}
	
	/** Gets the ItemState which determines which animation the object should be playing when dropped into the world. */
	public ItemState getItemState() {
		return itemState;
	}

	/** Sets the ItemState which determines which animation the object should be playing when dropped into the world. */
	public void setItemState(ItemState itemState) {
		this.itemState = itemState;
		
		//Reset state time since the ItemObject's state has changed.
		stateTime = 0;
	}
	
	/** Gets the item represented by the ItemObject. */
	public Item getItem() {
		return item;
	}

	/** Sets the item the GameObject contains and displays. */
	public void setItem(Item item) {
		this.item = item;
	}

	/** Called when an ItemObject is placed back into a pool. The GameObject must be reset to default configuration. */
	@Override
	public void reset() 
	{
		//Tell the ItemObject that it has just spawned for the next time it is fetched back from the pool.
		setItemState(ItemState.SPAWN);
	}
}
