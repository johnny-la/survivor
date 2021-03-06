package com.jonathan.survivor.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Survivor;
import com.jonathan.survivor.TerrainLayer;
import com.jonathan.survivor.TerrainLayer.TerrainDirection;

public class Background
{
	/** Holds the size of the background in world units. */
	private static final float BACKGROUND_WIDTH = 600 * Survivor.WORLD_SCALE,
							   BACKGROUND_HEIGHT = 500 * Survivor.WORLD_SCALE;
	
	/** Stores the size of each tile in world units. */
	public static final float TILE_WIDTH = 130 * Survivor.WORLD_SCALE,
							  TILE_HEIGHT = 70 * Survivor.WORLD_SCALE;
	
	/** Computes the number of rows and columns needed so that the tiles form a background of desired width and height. */
	private static final int NUM_ROWS = (int) Math.ceil(BACKGROUND_HEIGHT / TILE_HEIGHT),
							 NUM_COLS = (int) Math.ceil(BACKGROUND_WIDTH / TILE_WIDTH);
	
	/** Stores the sprites of each background tile in a matrix. */
	private Sprite[][] tiles;
	
	/** Creates a new Background which contains a matrix of sprite tiles. Creates it so that the center of the background is at the given position. */
	public Background(float centerX, float centerY)
	{
		//Creates a new matrix which will contain all of the tiles/sprites used to render the background.
		tiles = new Sprite[NUM_ROWS][NUM_COLS];
		
		//Creates the tiles so that the center of the background is at the given (x,y) position.
		createTiles(centerX, centerY);
	}
	
	/** Creates the sprite tiles which form the background. Called upon instantiation. Creates the tiles so that the background is centered at the given position. */
	private void createTiles(float centerX, float centerY) 
	{
		
		//The first tile in the row starts at the bottom-most position of the background.
		float y = centerY - BACKGROUND_HEIGHT/2;
		
		//Cycles through each row of tiles 
		for(int i = 0; i < NUM_ROWS; i++)
		{
			//The first tile in the column starts at the left-most position of the background.
			float x = centerX - BACKGROUND_WIDTH/2;
			
			//Cycles through each column in the row
			for(int j = 0; j < NUM_COLS; j++)
			{
				//Creates a new tile at the pre-computed bottom-left (x,y) position, and stores it in the correct position in the tile matrix.
				tiles[i][j] = newTile(x,y);
				
				//Place the next tile one 'TILE_WIDTH' apart.
				x += TILE_WIDTH;
			}
			
			//Place the next row of tiles one 'TILE_HEIGHT' higher.
			y += TILE_HEIGHT;
		}
	}

	/** Creates and returns a new Sprite displaying a tile in the background. Accepts the bottom-left position where the tile sprite is placed. */
	private Sprite newTile(float x, float y) 
	{
		//Stores the Sprite which will represent the created tile.
		Sprite tile = null;
	
		//Creates a random number between 0 and 3 to determine the look of the tile.
		int type = (int)(Math.random()*4);
		
		//Switches the type of the tile. Creates a tile depending on its type. Uses template sprites from the Assets singleton.
		switch(type)
		{
		case 0:
			tile = new Sprite(Assets.instance.snow1);
			break;
		case 1:
			tile = new Sprite(Assets.instance.snow2);
			break;
		case 2:
			tile = new Sprite(Assets.instance.snow3);
			break;
		case 3:
			tile = new Sprite(Assets.instance.snow4);
			break;
		default:
			tile = new Sprite(Assets.instance.snow4);
			break;
		}
		
		//Sets the bottom-left position of the tile at the given position.
		tile.setPosition(x,y);
		
		//Returns the created tile sprite.
		return tile;
	}
	
	/** Shifts the tiles so that the bottom-most tiles migrate to the top of the background. */
	public void shiftUp()
	{
		//Stores the bottom row of tiles, found in the first row of the tiles:Sprite[][] array.
		Sprite[] bottomTiles = tiles[0];
		
		//Computes the new y-position of the bottom tiles, which will be shifted to the top of the matrix.
		float newYPos = tiles[NUM_ROWS-1][0].getY() + TILE_HEIGHT;
		
		//Shifts the tiles down a row in the array so as to make way for the bottom tiles.
		for(int i = 0; i < tiles.length-1; i++)
			tiles[i] = tiles[i+1];
		
		//Update the last row of the tiles matrix to hold the bottom tiles.
		tiles[tiles.length-1] = bottomTiles;
		
		//Cycles through the tiles we just shifted to the top of the level
		for(int i = 0; i < bottomTiles.length; i++)
		{
			//Stores the tile that is being iterated through.
			Sprite tile = bottomTiles[i];
			
			//Re-position the tile so that it is on top of the matrix of tiles.
			tile.setY(newYPos);
		}
	}
	
	/** Shifts the tiles so that the top-most tiles migrate to the bottom of the background. */
	public void shiftDown()
	{
		//Stores the top row of tiles, found in the last row of the tiles:Sprite[][] array.
		Sprite[] topTiles = tiles[NUM_ROWS-1];
		
		//Computes the new y-position of the top tiles, which will soon be shifted to the bottom of the matrix.
		float newYPos = tiles[0][0].getY() - TILE_HEIGHT;
		
		//Shifts all of the tiles up a row in the matrix, so as to make way for the bottom tiles.
		for(int i = tiles.length-1; i > 0; i--)
			tiles[i] = tiles[i-1];
		
		//Update the last row of the tiles matrix to hold the top tiles.
		tiles[0] = topTiles;
		
		//Cycles through the tiles we just shifted to the bottom of the level
		for(int i = 0; i < topTiles.length; i++)
		{
			//Stores the tile that is being iterated through.
			Sprite tile = topTiles[i];
			
			//Re-position the tile so that it is on top of the matrix of tiles.
			tile.setY(newYPos);
		}
	}
	
	/** Shifts the tiles so that the left-most tiles migrate to the right of the background. */
	public void shiftRight()
	{
		//Computes the new x-position of the left-most tiles. They are placed to the right of the right-most tiles.
		float newXPos = tiles[0][NUM_COLS-1].getX() + TILE_WIDTH;
		
		//Shifts all of the tiles to the left, and inserts the left-most tiles to the right of the background.
		for(int i = 0; i < tiles.length; i++)
		{
			//Stores the left-most tile in the row, stored in the first column of the matrix.
			Sprite leftTile = tiles[i][0];
			
			//Shifts all of the tiles to the left, leaving an empty column at the end.
			for(int j = 0; j < tiles[i].length-1; j++)
			{
				//Shifts the tiles to the right
				tiles[i][j] = tiles[i][j+1];
			}
			
			//Re-position the left-most tile so that it is placed to the right of the matrix of tiles.
			leftTile.setX(newXPos);
			
			//Insert the left-most tile at the right-most column of the matrix, essentially moving it to the right of the matrix.
			tiles[i][NUM_COLS-1] = leftTile;
		}
	}
	
	/** Shifts the tiles so that the right-most tiles migrate to the left of the background. */
	public void shiftLeft()
	{
		//Finds the new x-position for the right-most layers. We choose the bottom-left layer's left-most x-position, minus the width of a tile.
		float newXPos = tiles[0][0].getX() - TILE_WIDTH;
		
		//Shifts the tiles to the right and inserts the right-most tiles to the left.
		for(int i = 0; i < tiles.length; i++)
		{
			//Retrieves the right-most tile in the row, which is stored in the last column of the matrix.
			Sprite rightTile = tiles[i][NUM_COLS-1];
			
			//Shifts all of the tiles to the right, leaving an empty column at the end.
			for(int j = tiles[i].length-1; j > 0; j--)
			{
				//Shifts the tiles to the right
				tiles[i][j] = tiles[i][j-1];
			}
			
			//Re-positions the right-most tile so that it is now at the left-most position of the background.
			rightTile.setX(newXPos);
			
			//Moves the left-most tile to the right-most column in the tiles matrix, so that the tile knows it is now at the left-most position in the matrix.
			tiles[i][0] = rightTile;
		}
	}

	/** Returns the tile in the center of the background. */
	public Sprite getCenterTile()
	{
		//Returns the sprite in the center of the matrix
		return tiles[NUM_ROWS/2][NUM_COLS/2];
	}

	/** Gets the matrix of tile sprites which represents and draws the background. */
	public Sprite[][] getTiles() {
		return tiles;
	}

	/** Sets the matrix of tile sprites which represents and draws the background. */
	public void setTiles(Sprite[][] tiles) {
		this.tiles = tiles;
	}
}
