package com.jonathan.survivor.math;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class Collider 
{
	/** Stores the position of the collider. If a rectangle collider is used, this position specifies the lower-left position of the rectangle. */
	protected final Vector2 position;
	
	/** Creates a collider placed at (0,0). */
	public Collider()
	{
		this(0, 0);
	}
	
	/** Creates a collider placed at the desired (x,y) coordinates */
	public Collider(float x, float y)
	{
		position = new Vector2(x, y);
	}
	
	/** Sets the position of the collider. */
	public void setPosition(float x, float y)
	{
		position.x = x;
		position.y = y;
	}
	
	/** Returns the position of the collider. Since Vector2s are mutable, the position can be changed using the Vector2's instance methods. */
	public Vector2 getPosition()
	{
		return position;
	}
	
	/** Returns true if this collider intersects with another collider*/
	public abstract boolean intersects(Collider r);
	
	/** Returns true if this collider is inside the camera. */
	public abstract boolean insideCamera(OrthographicCamera cam);
}
