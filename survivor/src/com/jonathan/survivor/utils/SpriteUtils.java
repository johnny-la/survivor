package com.jonathan.survivor.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;

/** Offers helper methods to manipulate sprites. */

public class SpriteUtils 
{
	/** Positions the center of the sprite at the given (x,y) coordinates in world coordinates. */
	public static void setPosition(Sprite sprite, float x, float y)
	{
		sprite.setPosition(x - sprite.getWidth()/2, y - sprite.getHeight()/2);
	}
}
