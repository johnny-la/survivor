package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.TerrainLayer;
import com.jonathan.survivor.TerrainLayer.TerrainType;
import com.jonathan.survivor.math.Vector2;

/** 
 * Stores and positions pools of sprites which forms the shape of a TerrainLayer.
 *
 */

public class TerrainLayerRenderer 
{	
	/** Stores the color of transparent TerrainLayer sprites. */
	private static final Color TRANSPARENT_COLOR = new Color(0.8f, 0.8f, 0.8f, 0.3f);
	
	/** Holds the TerrainLayer instance which this renderer is assigned to draw. */
	private TerrainLayer terrainLayer;
	
	/** Stores the array of all the sprites which are currently being rendered by this instance. These are the sprites which are used to draw the TerrainLayers of the level. */
	private Array<Sprite> sprites;
	
	/** Holds the TerrainSpritePool which pools all of the unused sprites which render the shape of a TerrainLayer. */
	private SpritePool spritePool;
	
	/** Helper Color instance used to color the TerrainLayer sprites and avoid creating new color instances. */
	private Color workingColor;
	
	/** Creates the TerrainSpriteRenderer used to position and pool sprites used to form the ground of the TerrainLayers. */
	public TerrainLayerRenderer(TerrainLayer terrainLayer)
	{
		//Creates the Array which holds all of the currently-active sprites being rendered by the renderer to form the level's TerrainLayers.
		sprites = new Array<Sprite>();
		
		//Creates a new TerrainSpritePool, which pools all of the sprites which will be re-cycled when needed.
		spritePool = new SpritePool();
		
		//Helper Color instance used to avoid instantiation. Defaults to white.
		workingColor = new Color(Color.WHITE);
		
		//Update the renderer to form the line used to model the given Terrainlayer.
		update(terrainLayer);
	}
	
	/** Draws the TerrainLayer using the given SpriteBatch instance. Draws each individual sprite stored in the renderer, which ultimately form the line of the TerrainLayer
	 *  the Renderer is supposed to draw. 
	 */
	public void draw(SpriteBatch batcher, boolean drawTransparent)
	{
		//Reset the workingColor to WHITE and apply color changes from there
		workingColor.set(Color.WHITE);
		
		//If the TerrainLayer is supposed to be drawn transparent
		if(drawTransparent)
			//Multiply the working color by the TRANSPARENT_COLOR constant. This working color will then be applied to each individual TerrainLayer sprite.
			workingColor.mul(TRANSPARENT_COLOR);
		
		//Cycles through each sprite that is currently assigned to the renderer in order to draw the assigned TerrainLayer. 
		for(int i = 0; i < sprites.size; i++)
		{
			//Apply the workingColor to each TerrainLayer sprite. The workingColor stores the color that should be applied to each sprite.
			sprites.get(i).setColor(workingColor);
				
			//Draws each sprite to the screen using the given batcher.
			sprites.get(i).draw(batcher);
		}
	}
	
	/** Updates the sprites in this renderer to form the shape of the TerrainLayer stored as a member variable of the object. */
	public void update(TerrainLayer terrainLayer)
	{	
		//Updates the terrainLayer which this renderer is assigned to draw.	
		this.terrainLayer = terrainLayer;
		
		//Updates the renderer to ensure that its sprites are placed so as to render the given TerrainLayer.
		update();
	}
	
	/** Updates the sprites in this renderer to follow the shape of the TerrainLayer stored as a member variable. */
	public void update()
	{		
		//Stores the bottom left and right end-points of the TerrainLayer using the methods TerrainLayer.getLeft/RightPoint():Vector2.
		Vector2 leftEndPoint = terrainLayer.getLeftPoint();
		Vector2 rightEndPoint = terrainLayer.getRightPoint();
		
		//Stores the index of the sprite in the sprites:Array<Sprite> array that is being placed in the renderer.
		int spriteIndex = 0;
		
		//Cycles from the x-position of the layer's left end-point, to the right-most x-position of the layer. 
		for(float x = leftEndPoint.x; x < rightEndPoint.x; )
		{
			//Stores the sprite that will be placed on the renderer in order to draw the TerrainLayer.
			Sprite sprite = null;
			
			//If the index of the sprite that has to be placed already exists in the sprites<Array>, simply re-use that sprite.
			if(spriteIndex < sprites.size && sprites.get(spriteIndex) != null)
			{
				//Re-use the sprite that was previously placed in the sprites:Array<Sprite> array in order to model the previous TerrainLayer.
				sprite = sprites.get(spriteIndex);
			}
			//Else, if no more sprites in the sprites:Array<Sprite> can be reused
			else
			{
				//Retrieves a sprite from the SpritePool used to draw the segment of the layer we are cycling through.
				sprite = spritePool.obtain();
			}
			
			//The length of the segment is equal to the length of the sprite used to model this segment.
			float segmentLength = sprite.getWidth();
			
			//Stores the x-position of left-end of the segment. This is the x-position that the outer 'for' loop is cycling through.
			float x1 = x;
			//Stores the y-position of the left-end of the segment. Found by finding the height of the bottom of the layer at the point's x-position.
			float y1 = terrainLayer.getBottomLayerHeight(x1);
			
			//Finds the x-position of the right-end of the segment simply by adding a segment width to the left-end's x-position.
			float x2 = x1 + segmentLength;
			
			//If the right end-position of the segment has passed the bounds of the TerrainLayer, break this for loop, since the segment is out of bounds.
			if(x2 > rightEndPoint.x)
				break;
			
			//Stores the y-position of the right-end of the segment. Found by finding the height of the bottom of the layer at the point's x-position.
			float y2 = terrainLayer.getBottomLayerHeight(x2);
			
			//Calculates the height of the segment we are modeling in this 'for' loop iteration. It is simply the subtraction of the height of each end of the segment.
			float segmentHeight = y2-y1;

			//Finds the angle of the segment by calculating the arctangent of the segment height over the segment width.
			float angle = (float)Math.atan(segmentHeight/segmentLength) * Vector2.TO_DEGREES;
			
			//Sets the sprites left-center position at (x1,y1), so that it's left-center position starts at the left-end of the segment.
			sprite.setPosition(x1, y1-sprite.getHeight()/2);
			
			//Allows the sprite to rotate around its left end-point.
			sprite.setOrigin(0, 0);
			//Rotates the sprite at the angle of the line segment it models.
			sprite.setRotation(angle);
			
			//If the index of the sprite which was placed already exists within the bounds of the sprites:Array<Sprite> array, it does not need to be re-placed into the array.
			//The sprite was taken from the array in the first place.
			if(spriteIndex < sprites.size)
			{
			}
			//Else, if the sprite being placed is out of bounds of the sprites:Array<Sprite>, add it to the array.
			else
			{
				//Add the sprite into the list of sprites used to model the line segments of the given TerrainLayer.
				sprites.add(sprite);
			}
			
			//Increment the x-value that the 'for' loop is cycling through, so that the next segment starts at the end of the last one.
			x += segmentLength;
			
			//Next iteration, the sprite in the next index of the sprites:Array<Sprite> array will be placed on the renderer.
			spriteIndex++;
			
			//If we are cycling through the last row of the layers array, we have reached the top-most layer. Thus, draw the top portion of cosine function.
			//It wasn't necessary to do so before because the top-portion of the previous layers were drawn by the bottom portions of the next layers.
			/*if(i == layers.length-1)
				//Draws the top portion of the layer by drawing the cosine line segment from the left-end point of the segment to its right end-point and.
				//Note that the y-value of the segment is found using 'TerrainLayer.getTopLayerHeight()'. We take the top height since we are drawing 
				//the top portion of the layer.
				shapeRenderer.line(x1, layers[i][j].getTopLayerHeight(x1), x2, layers[i][j].getTopLayerHeight(x2));*/
		}
	}
	
	/** Recycles he currently used sprites, and frees them back into the pools of sprites. CURRENTLY NOT USED */
	public void freeSprites()
	{
		//Cycles through each sprite that is currently assigned to draw a TerrainLayer. 
		for(int i = 0; i < sprites.size; i++)
		{
			//Frees each sprite back into the SpritePool for later re-use.
			spritePool.free(sprites.get(i));
		}
	}

	/** A pool which holds Sprites used to draw the layers in a TerrainLevel. */
	private class SpritePool extends Pool<Sprite>
	{

		/** Returns a random new sprite when none are free in the pool. */
		@Override
		protected Sprite newObject() 
		{
			//Stores the Sprite which will be created and returned by this method.
			Sprite sprite = null;
		
			//Creates a random number between 0 and 3 to determine the look of the tile.
			int type = (int)(Math.random()*4);
			
			//Switches the type of sprite to choose. Allows the TerrainLevel's groud sprites to look different. Uses template sprites from the Assets singleton.
			switch(type)
			{
			case 0:
				sprite = new Sprite(Assets.instance.snow1);
				break;
			case 1:
				sprite = new Sprite(Assets.instance.snow2);
				break;
			case 2:
				sprite = new Sprite(Assets.instance.snow3);
				break;
			case 3:
				sprite = new Sprite(Assets.instance.snow4);
				break;
			default:
				sprite = new Sprite(Assets.instance.snow4);
				break;
			}
			
			//Returns the created tile sprite.
			return sprite;
		}	
	}
	
	/** Returns the TerrainType of the layer that this renderer is assigned to draw. */
	public TerrainType getTerrainType() {
		return terrainLayer.getTerrainType();
	}
	
	/** Returns the row of the TerrainLayer that this renderer is drawing. */
	public int getRow() {
		return terrainLayer.getRow();
	}

	/** Returns the column of the TerrainLayer that this renderer is drawing. */
	public int getCol() {
		return terrainLayer.getCol();
	}

	/** Returns the TerrainLayer which this renderer draws to the screen. */
	public TerrainLayer getTerrainLayer() {
		return terrainLayer;
	}
	
	/** Sets the TerrainLayer which this renderer draws to the screen. The update() method must be called to ensure that the sprites are re-positioned to model this new layer. */
	public void setTerrainLayer(TerrainLayer layer)
	{
		this.terrainLayer = terrainLayer;
	}
}
