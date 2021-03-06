package com.jonathan.survivor.entity;

import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.math.Cell;
import com.jonathan.survivor.math.Collider;
import com.jonathan.survivor.math.Rectangle;
import com.jonathan.survivor.math.Vector2;

/*
 * Represents an object with a collider in the world.
 */

public abstract class GameObject 
{
	/** Stores the bottom-bottom-center position of the GameObject */
	private final Vector2 position;
	/** Holds the GameObject's previous position before the current game tick. */
	private final Vector2 previousPosition;
	/** Stores the velocity of the gameObject the previous frame. */
	private final Vector2 oldVelocity;
	/** Stores the velocity of the GameObject */
	private final Vector2 velocity;
	/** Stores the acceleration of the GameObject */
	private final Vector2 acceleration;
	
	/** Stores the collider used by the GameObject for collision */
	private Collider collider;
	/** Stores the Spine skeleton that controls the bones of the GameObject and its appearance. */
	private Skeleton skeleton;
	
	/** Stores the row and column corresponding to the layer where the GameObject resides on the TerrainLevel. */
	private Cell terrainCell;
	
	/** The GameObject's id, used to identify GameObjects inside a save file. Used to identify whether or not a GameObject should be placed on a TerrainLayer.*/
	private int objectId;
	
	/** Stores the amount of time the GameObject has been in a specific state. (i.e., if the player has been jumping for 0.5 seconds, stateTime = 0.5). */
	protected float stateTime;
	
	/** Creates a GameObject with a bottom-bottom-center at (0,0) and a rectangle collider with width/height of zero. */
	public GameObject()
	{
		this(0,0,0,0);
	}
	
	/** Creates a GameObject with bottom-center at (x,y) and rectangle collider with given width/height. */
	public GameObject(float x, float y, float width, float height)
	{
		//Creates the Vector2s for the motion variables of the GameObject.
		position = new Vector2(x, y);
		previousPosition = new Vector2();
		oldVelocity = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		
		//Creates a rectangle collider for the GameObject with the given width and height, positioned at the bottom-center of the GameObject.
		collider = new Rectangle(x - width/2, y - width/2, width, height);
		
		//Populates the terrainCell variable with a new Cell object for later user.
		terrainCell = new Cell();
	}
	
	/** Updates the GameObject's game logic. */
	public abstract void update(float deltaTime);
	
	/** Updates the position of the GameObject according to its velocity and acceleration. */
	public void updatePosition(float deltaTime)
	{
		//Store the old position before changing it
		previousPosition.set(position.x, position.y);
		
		//Store the GameObject's old velocity, before changing it
		oldVelocity.set(velocity);
		
		velocity.add(acceleration.mul(deltaTime));
		position.add(oldVelocity.add(velocity).mul(0.5f).mul(deltaTime));
	}
	
	/** Snaps the GameObject's collider to the GameObject's position */
	public void updateCollider()
	{
		//If the collider used by the GameObject is a rectangle
		if(collider instanceof Rectangle)
			//Place the lower left position of the collider at the right point. Note that a rectangle's position is its bottom-left corner.
			collider.getPosition().set(position.x - ((Rectangle)collider).getWidth()/2, position.y);
	}
	
	/** Returns true if the GameObject can be targetted by a Human. */
	public abstract boolean canTarget();
	
	/** Retrieves the bottom-center position of the gameObject as a Vector2. Operations can be performed on the Vector2 using its 
	 *  instance methods, as it is mutable. */
	public Vector2 getPosition() {
		return position;
	}
	
	/** Sets the bottom-center position of the GameObject at the desired (x,y) coordinates. */
	public void setPosition(float x, float y)
	{
		//Store the old position before changing it
		previousPosition.set(position.x, position.y);
		
		//Change the current position of the GameObject.
		position.set(x, y);
	}
	
	/** Returns the bottom-center x-position of the GameObject. */
	public float getX()
	{
		//Returns the x-position of the GameObject.
		return position.x;
	}
	
	/** Sets the center x-position of the GameObject. */
	public void setX(float x)
	{
		//Store the old x-position of the GameObject in the previousPosition Vector2.
		previousPosition.x = position.x;
		
		//Updates the center x-position of the GameObject.
		position.x = x;
	}
	
	/** Returns the bottom-center y-position of the GameObject. */
	public float getY()
	{
		//Returns the y-position of the GameObject.
		return position.y;
	}
	
	/** Sets the bottom y-position of the GameObject. */
	public void setY(float y)
	{
		//Store the old y-position of the GameObject in the previousPosition Vector2.
		previousPosition.y = position.y;
		
		//Updates the bottom y-position of the GameObject.
		position.y = y;
	}
	
	/** Returns the GameObject's previous position before the current game tick. */
	public Vector2 getPreviousPosition() 
	{
		return previousPosition;
	}
	
	/** Retrieves the velocity of the gameObject as a Vector2. Operations can be performed on the Vector2 using its 
	 *  instance methods, as it is mutable. */
	public Vector2 getVelocity() {
		return velocity;
	}
	
	/** Sets the velocity of the GameObject at their desired (x,y) values. */
	public void setVelocity(float x, float y)
	{
		velocity.set(x, y);
	}
	
	/** Updates the x-velocity of the GameObject. */
	public void setVelocityX(float x)
	{
		velocity.x = x;
	}
	
	/** Updates the y-velocity of the GameObject. */
	public void setVelocityY(float y)
	{
		velocity.y = y;
	}
	
	/** Retrieves the acceleration of the gameObject as a Vector2. Operations can be performed on the Vector2 using its 
	 *  instance methods, as it is mutable. */
	public Vector2 getAcceleration() {
		return velocity;
	}
	
	/** Sets the acceleration of the GameObject at their desired (x,y) values. */
	public void setAcceleration(float x, float y)
	{
		acceleration.set(x, y);
	}
	
	/** Moves the GameObject to the desired (x,y) position in a straight line at the given speed in m/s. */
	public void moveTo(float x, float y, float time) 
	{
		//Finds the (x,y) distance from the GameObject to the target (x,y) position.
		float xDist = x - position.x;
		float yDist = y - position.y;
		
		//Calculates the distance between the GameObject and the target position.
		float dist = (float) Math.sqrt(xDist*xDist + yDist*yDist);
		
		//Finds the speed the GameObject must go to reach the target in the given amount of time (v = d/t).
		float speed = dist / time;
		
		//Set the GameObject's velocity to the (x,y) distance to the target
		velocity.set(xDist, yDist);
		//Normalize the velocity to keep only the direction to the target.
		velocity.normalize();
		//Multiply the normalized vector by the speed so that the GameObject moves to the target position at the correct speed.
		velocity.mul(speed);
		
	}
	
	/** Returns true if this GameObject is above the given GameObject. */
	public boolean isAbove(GameObject gameObject) 
	{
		//Gets the top-most y-position of the GameObject.
		float topY = gameObject.getY() + ((Rectangle)gameObject.getCollider()).getHeight();
		
		//If this GameObject is above the other GameObject
		if(getY() > topY)
		{
			//Return true, since this GameObject is on top of the given GameObject.
			return true;
		}
		//Else, if, last frame, this GameObject was above the GameObject, but is now below the GameObject
		if(previousPosition.y > + topY && getY() < topY)
		{
			//This GameObject is still technically above the GameObject. Therefore, return true. Otherwise, the player would never hit the zombie's head. 
			return true;
		}
		
		//If this statement is reached, this GameObject is below the given GameObject. Therefore, return false.
		return false;
	}

	/** Returns the collider used by the gameObject for collisions. */
	public <T extends Collider> T getCollider()
	{
		return (T)collider;
	}
	
	/** Sets the collider used by the gameObject for collisions. */
	public void setCollider(Collider c)
	{
		this.collider = c;
	}
	
	/** Returns the skeleton used to render the GameObject to the screen. Returns null if the GameObject doesn't use a Spine skeleton. */
	public Skeleton getSkeleton() {
		return skeleton;
	}

	/** Sets the spine skeleton used by the GameObject to be rendered on-screen. */
	public void setSkeleton(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	/** Gets the cell coordinates where the GameObject is placed on the TerrainLevel. */
	public Cell getTerrainCell() {
		return terrainCell;
	}

	/** Sets the cell coordinates where the GameObject is placed on the TerrainLevel. */
	public void setTerrainCell(int row, int col) {
		terrainCell.set(row, col);
	}
	
	/** Gets the amount of time the GameObject has been in a specific state. (e.g., stateTime = 0.4 if the GameObject has been in the JUMP 
	 *  state for 0.4 seconds. */
	public float getStateTime() {
		return stateTime;
	}
	
	/** Sets the amount of time the GameObject has been in a specific state. Only the gameObject's update() method should update this value. */
	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	/** Gets the ID of the GameObject, used to identify a GameObject in a specific TerrainLayer. */
	public int getObjectId() {
		return objectId;
	}

	/** Sets the ID of the GameObject, used to identify a GameObject in a specific TerrainLayer. */
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}
}
