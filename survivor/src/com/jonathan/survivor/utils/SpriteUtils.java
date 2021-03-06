package com.jonathan.survivor.utils;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jonathan.survivor.Assets;

/** Offers helper methods to manipulate sprites. */

public class SpriteUtils 
{
	/** Positions the center of the sprite at the given (x,y) coordinates in world coordinates. */
	public static void setPosition(Sprite sprite, float x, float y) {
		sprite.setPosition(x - sprite.getWidth()/2, y - sprite.getHeight()/2);
	}
	
	/** Fixes bleeding on a TextureRegion, removing the edge pixels that can bleed into the edges of the texture. */
	public static void fixBleeding(TextureRegion region) {
		float x = region.getRegionX();
		float y = region.getRegionY();
		float width = region.getRegionWidth();
		float height = region.getRegionHeight();
		float invTexWidth = 1f / region.getTexture().getWidth();
		float invTexHeight = 1f / region.getTexture().getHeight();
		region.setRegion((x + .75f*Assets.instance.fontScale) * invTexWidth, (y+.75f*Assets.instance.fontScale) * invTexHeight,
					   	 (x + width - .75f*Assets.instance.fontScale) * invTexWidth, (y + height - .75f*Assets.instance.fontScale) * invTexHeight);       
	}
}
