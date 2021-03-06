package com.jonathan.survivor.math;

/*
 * A line consisting of two end-points. 
 */

public class Line 
{
	/** Holds the end-points of the line. */
	private float x1, y1, x2, y2;

	/** Creates a line with both end points at (0,0). */
	public Line()
	{
	}
	
	/** Creates a line with the given end-points. */
	public Line(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/** Changes the two end-points of the line. */
	public void set(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/** Returns true if the line intersects with a given collider. */
	public boolean intersects(Collider collider)
	{
		//If the collider is a rectangle
		if(collider instanceof Rectangle)
		{
			//Casts the collider as a rectangle.
			Rectangle rectangle = (Rectangle)collider;
			
			//Calculates the slope of this line
			float slope = (y2-y1)/(x2-x1);
			//Computes the y-intercept of this line.
			float intercept = y2 - slope*x2;
			
			//Gets all four needed position coordinates for the rectangle.
			float rightX = rectangle.getPosition().x + rectangle.getWidth();
			float leftX = rectangle.getPosition().x;
			float bottomY = rectangle.getPosition().y;
			float topY = rectangle.getPosition().y + rectangle.getHeight();
						
			//Gets the y-position of the line at the right-most x-position of the rectangle.
			float y = slope*rightX + intercept;
			
			//If the y-position of the line at the given x-position is between the top and bottom of the rectangle, the line intersects with the rectangle.
			if(y >= bottomY && y <= topY)
				return true;
			
			//Computes the y-position of the line at the left most x-position of the rectangle.
			y = slope*leftX + intercept;
			
			//If the y-position of the line at the given x-position is between the top and bottom of the rectangle, the line intersects with the rectangle.
			if(y >= bottomY && y <= topY)
				return true;
			
			//Gets the x-position of the line at the bottom-most y-position of the rectangle.
			float x = (bottomY - intercept)/slope;
			
			//If the x-position of the line at the given y-position is between the left and right of the rectangle, the line intersects with the rectangle.
			if(x >= leftX && y <= rightX)
				return true;
			
			//Computes the x-position of the line at the top-most y-position of the rectangle.
			x = (topY - intercept)/slope;
			
			//If the x-position of the line at the given y-position is between the left and right of the rectangle, the line intersects with the rectangle.
			if(x >= leftX && y <= rightX)
				return true;
			
		}
		
		//If this statement is reached, the line does not collide with the collider.
		return false;
	}
	
	/** Getters and setters for the member variables of this class. */
	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}
	
	
}