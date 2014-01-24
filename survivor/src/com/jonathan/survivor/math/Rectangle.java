package com.jonathan.survivor.math;

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
}
