package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.Background;
import com.jonathan.survivor.entity.Player;

/*
 * Renders the game's FX displayed on screen, such as the crosshairs.
 */

public class BackgroundRenderer 
{
	/** Stores the SpriteBatcher used to draw the Spine animations. */
	private SpriteBatch batcher;	
	
	/** Stores the OrthographicCamera which the background follows. */
	private OrthographicCamera worldCamera;
	
	/** Holds the Background instance which is drawn to the screen. */
	private Background background;
	
	/** Accepts the SpriteBatch used to draw the background, and the world camera where the background tiles are rendered. */
	public BackgroundRenderer(SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the given contructor arguments in their respective member variables.
		this.batcher = batcher;
		this.worldCamera = worldCamera;
		
		//Creates the Background instance which is drawn to the screen. Accepts the center position of the Background, which is chosen to be the center-position of the camera.
		background = new Background(worldCamera.position.x, worldCamera.position.y);
	}
	
	/** Called every frame to render the Background to the screen. */
	public void render(float deltaTime)
	{		
		//Updates the background according to the player's position.
		update(deltaTime);
		
		//Begin batching the sprites used to draw the background.
		batcher.begin();
		
		//Draws the Background to the screen.
		draw(deltaTime);

		//Commit the background sprites and draw them on-screen.
		batcher.end();
	}

	/** Updates the Background so that it is always centered on the player. */
	private void update(float deltaTime) 
	{		
		//Gets the background's center-most tile to compute if the background should be moved around to be centered at the camera's position.
		Sprite centerTile = background.getCenterTile();
		
		//Stores the center position of the camera.
		float cameraX = worldCamera.position.x;
		float cameraY = worldCamera.position.y;
		
		//If the camera is to the right of the background's center tile
		if(cameraX > centerTile.getX() + Background.TILE_WIDTH)
		{
			//Shift the tiles of the background to the right, so that the background follows the camera's position.
			background.shiftRight();
		}
		//Else, if the camera is to the left of the center-most tile
		else if(cameraX < centerTile.getX())
		{
			//Shift the background's tiles to the left, so that the background follows the camera's position.
			background.shiftLeft();
		}
		//Else, if the camera is above the background's center-most tile
		else if(cameraY > centerTile.getY() + Background.TILE_HEIGHT)
		{
			//Shift the background's tiles up, so that the background follows the camera's position.
			background.shiftUp();
		}
		//Else, if the camera is below the background's center-most tile
		else if(cameraY < centerTile.getY())
		{
			//Shift the background's tiles down, so that the background follows the camera's position.
			background.shiftDown();
		}
	}
	
	/** Draws the Background instance on-screen. */
	private void draw(float deltaTime) 
	{
		//Gets the tile sprites which represent the background.
		Sprite[][] tiles = background.getTiles();
		
		//Cycles through each tile and renders it to the world camera.
		for(int i = 0; i < tiles.length; i++)
		{
			for(int j = 0; j < tiles[i].length; j++)
			{
				//Draws the tile sprite using the SpriteBatch instance stored as a member variable of this class.
				tiles[i][j].draw(batcher);
			}
		}
	}
	
}
