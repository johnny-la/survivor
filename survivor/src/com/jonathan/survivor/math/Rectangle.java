package com.jonathan.survivor.math;

import com.badlogic.gdx.graphics.OrthographicCamera;

public class Rectangle extends Collider
{
	/** Stores the width and height of the rectangle */
	private float width, height;
	
	/** Creates a rectangle at lower-left position (0,0) with width/height of zero */
	public Rectangle()
	{
	}
	
	/** Creates a rectangle at lower-left position (0,0) with given width/height */
	public Rectangle(float width, float height)
	{
		this(0, 0, width, height);
	}
	
	/** Creates a rectangle at lower-left position (x,y) with given width/height */
	public Rectangle(float x, float y, float width, float height)
	{
		super(x, y);
		
		this.width = width;
		this.height = height;
	}

	/** Returns true if this rectangle intersects with another collider */
	@Override
	public boolean intersects(Collider c) 
	{
		//If the collider is a rectangle, perform an intersection check with a rectangle.
		if(c instanceof Rectangle)
		{
			//Cast the collider to a rectangle for convenience.
			Rectangle r = (Rectangle) c;
			
			//Returns true if the two rectangles intersect.
			if( position.x < r.position.x + r.width &&
				position.x + width > r.position.x &&
				position.y < r.position.y + r.height &&
				position.y + height > r.position.y )
			{
				return true;
			}
		}
		
		//Returns false if they don't intersect
		return false;
	}
	
	/** Returns true if this point is within the rectangle. */
	@Override
	public boolean intersects(Vector2 point)
	{
		//If the point lies within the rectangle, return true.
		if(point.x >= getPosition().x && point.x <= getPosition().x + this.width && 
		   point.y >= getPosition().y && point.y <= getPosition().y + this.height )
		{
			return true;
		}
		
		//Return false if the point is not inside the rectangle.
		return false;
	}
	
	/** Returns true if this rectangle collider is inside the bounds of the camera. Used for culling. */
	public boolean insideCamera(OrthographicCamera camera)
	{
		//Returns true if the rectangle collider intersects with the camera or is inside the camera.
		if( position.x < camera.position.x + camera.viewportWidth/2 &&
			position.x + width > camera.position.x - camera.viewportWidth/2 &&
			position.y < camera.position.y + camera.viewportHeight/2 &&
			position.y + height > camera.position.y - camera.viewportHeight/2 )
		{
			return true;
		}
		
		//Returns false if they don't intersect
		return false;
	}
	
	/** Returns the y-position of the top of the collider in world units. */
	public float getTop()
	{
		return position.x + height;
	}

	/** Sets the width and height of the rectangle from its bottom-left position. */
	public void setSize(float width, float height)
	{
		this.width = width;
		this.height = height;
	}
	
	/** Retrieves the width of the rectangle */
	public float getWidth() {
		return width;
	}

	/** Sets the width of the rectangle */
	public void setWidth(float width) {
		this.width = width;
	}

	/** Gets the height of the rectangle */
	public float getHeight() {
		return height;
	}

	/** Sets the height of the rectangle */
	public void setHeight(float height) {
		this.height = height;
	}
	
	public String toString()
	{
		return "Position: " + getPosition() + " Width: " + getWidth() + " Height: " + getHeight();
	}
}
