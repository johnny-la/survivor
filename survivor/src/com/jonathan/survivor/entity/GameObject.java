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
	
	/** Retrieves the bottom-center position of the gameObject as a Vector2. Operations can be performed on the Vector2 using its 
	 *  instance methods, as it is mutable. */
	public Vector2 getPosition() {
		return position;
	}
	
	/** Sets the bottom-center position of the GameObject at the desired (x,y) coordinates. */
	public void setPosition(float x, float y)
	{
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
		//Updates the bottom y-position of the GameObject.
		position.y = y;
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
}
