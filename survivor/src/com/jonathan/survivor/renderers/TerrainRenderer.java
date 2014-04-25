package com.jonathan.survivor.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jonathan.survivor.TerrainLayer;
import com.jonathan.survivor.TerrainLayer.TerrainDirection;
import com.jonathan.survivor.TerrainLayer.TerrainType;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.math.Rectangle;

public class TerrainRenderer 
{		
	/** Stores the SpriteBatch instance used to draw the sprites which form the terrain geometry. */
	private SpriteBatch batcher;
	
	/** Stores the camera where the terrain is drawn. In this case, the world camera. */
	private OrthographicCamera worldCamera;
	
	/** Holds the TerrainLevel instance that the renderer is assigned to draw. */
	private TerrainLevel terrainLevel;
	
	/** Holds the layer renderers used to render each individual TerrainLayer. Note that the array is shaped exactly like the 'layers[][]' array in the TerrainLevel. */
	private TerrainLayerRenderer[][] layerRenderers;
	
	/** Helper Rectangle instance used to test whether or not a certain TerrainLayer is viewable by the world camera. */
	private Rectangle lineBounds;
	
	/** Accepts the camera where the terrain lines will be drawn. */
	public TerrainRenderer(SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the SpriteBatch instance used to draw a TerrainLevel instance.
		this.batcher = batcher;
		//Stores the camera where the terrain lines will be drawn.
		this.worldCamera = worldCamera;
		
		//Helper rectangle used to test if a TerrainLayer is viewable by the world camera.
		lineBounds = new Rectangle(); 
	}
	
	/** Creates a matrix of TerrainLayerRenderers, each of which is used to draw a specific TerrainLayer in the given level. */
	private void createLayerRenderers(TerrainLevel level) 
	{
		//Stores the TerrainLevel which is meant to be drawn by the TerrainRenderer. Allows the renderer to know which TerrainLevel it is currently rendering.
		terrainLevel = level;
		
		//Creates the TerrainLayerRenderers used to draw each individual TerrainLayer in the TerrainLevel. Note that its size is identical to the amount of layers in the TerrainLevel.
		layerRenderers = new TerrainLayerRenderer[TerrainLevel.NUM_LAYER_ROWS][TerrainLevel.NUM_LAYER_COLS];
		
		//Retrieves the TerrainLayers contained by the level. They are stored in a 2D array. These are the only layers that are visible to the user.
		TerrainLayer[][] layers = level.getTerrainLayers();

		//Cycles through the rows of TerrainLayers stored in the level.
		for(int i = 0; i < layers.length; i++)
		{
			//Cycles through the columns of the TerrainLayers array.
			for(int j = 0; j < layers[i].length; j++)
			{
				//Stores the TerrainLayer instance that is being cycled through
				TerrainLayer layer = layers[i][j];
				
				//Creates a new TerrainLayerRenderer which will be used to render the TerrainLayer we are cycling through. 
				layerRenderers[i][j] = new TerrainLayerRenderer(layer);
			}
		}
	}

	/** Renders the terrainLevel's geometry using lines of sprites. */
	public void render(TerrainLevel level)
	{
		//If the level that needs to be rendered is not the same level that the renderer is currently rendering
		if(level != terrainLevel)
		{
			//Update the TerrainLayerRenderers so that they render the TerrainLayers that belong to the given level.
			createLayerRenderers(level);
		}
		
		//Update the TerrainLayerRenderers in case the player switches layers.
		update();
		//Draws the TerrainLayers to the screen.
		draw();
		
		
	}
	
	/** Updates the TerrainLayerRenderers to match the TerrainLayer matrix of the TerrainLevel. Essentially ensures that the TerrainLayerRenderers 
	 *  draw the correct set of TerrainLayers. */
	private void update() 
	{
		//Retrieves the bottom-left-most TerrainLayer in the level to test if the player has switched layers.
		TerrainLayer bottomLeftLayer = terrainLevel.getBottomLeftLayer();
		
		//Gets the TerrainLayer that the bottom-left-most TerrainLayerRenderer is drawing. Used to test if the player switched layers
		TerrainLayer currentLayer = layerRenderers[0][0].getTerrainLayer();
		
		//If the player has moved one layer up from the previous time this update() method was called
		if(bottomLeftLayer.getRow() < currentLayer.getRow())
		{
			//Shift the TerrainLayerRenderers up by one row so that they correspond to the TerrainLevel's layer matrix
			shiftLayersDown();
		}
		//Else, if the player has moved down a layer since the last time this update() method was called
		else if(bottomLeftLayer.getRow() < currentLayer.getRow())
		{
			//Shift the TerrainLayerRenderers down a row so they correspond to the TerrainLevel's layer matrix
			shiftLayersUp();
		}
		
	}

	/** Shift the TerrainLayerRenderers up a row, so that they draw TerrainLayers one row higher. Called when the player jumps. */
	private void shiftLayersUp() 
	{
		//Stores the bottom TerrainLayerRenderers, stored in the first row of the layerRenderers:TerrainRendererLayer[][] array.
		TerrainLayerRenderer[] bottomRenderers = layerRenderers[0];
		
		//Shifts the layer renderers down a row in the array to make way for the bottom layer renderers.
		for(int i = 0; i < layerRenderers.length-1; i++)
			layerRenderers[i] = layerRenderers[i+1];
		
		//Update the last row of the layers:TerrainRendererLayer[][] array to hold the bottom layer renderers.
		layerRenderers[layerRenderers.length-1] = bottomRenderers;
		
		//Stores the TerrainLayers at the top of the TerrainLevel. The bottom-most renderers must be updated to render these new layers.
		TerrainLayer[] newTopLayers = terrainLevel.getTerrainLayers()[TerrainLevel.NUM_LAYER_COLS-1];
		
		//Cycles through the TerrainLayerRenderers we just shifted to the top of the level
		for(int i = 0; i < bottomRenderers.length; i++)
		{
			//Stores the renderer we are cycling through
			TerrainLayerRenderer layerRenderer = bottomRenderers[i];
			//The renderer will be updated to draw the top-most TerrainLayer in the level with column i 
			TerrainLayer layerToRender = newTopLayers[i];
			
			//Updates the renderer to draw the new layer.
			layerRenderer.update(layerToRender);
		}		
	}
	
	/** Called when the player falls down a layer. Moves the TerrainLayerRenderers down a row in order to draw the TerrainLayers currently in the level. */
	private void shiftLayersDown() 
	{
		//Stores the top TerrainLayerRenderers which model the top-most TerrainLayers. These top renderers are stored in the first row of the layerRenderers:TerrainRendererLayer[][] array.
		TerrainLayerRenderer[] topRenderers = layerRenderers[0];
		
		//Shifts the layer renderers up a row in the array to make way for the top layer renderers to move to the bottom-most row.
		for(int i = layerRenderers.length-1; i > 0; i--)
			layerRenderers[i] = layerRenderers[i-1];
		
		//Update the first row of the layers:TerrainRendererLayer[][] array to hold the top layer renderers.
		layerRenderers[0] = topRenderers;
		
		//Stores the TerrainLayers at the bottom of the TerrainLevel. The top-most renderers must be updated to render these new layers.
		TerrainLayer[] newBottomLayers = terrainLevel.getTerrainLayers()[0];
		
		//Cycles through the TerrainLayerRenderers we just shifted to the top of the level
		for(int i = 0; i < topRenderers.length; i++)
		{
			//Stores the renderer we are cycling through
			TerrainLayerRenderer layerRenderer = topRenderers[i];
			//The renderer will be updated to draw the bottom-most TerrainLayer in the level with column i 
			TerrainLayer layerToRender = newBottomLayers[i];
			
			//Updates the renderer to draw the new layer.
			layerRenderer.update();
		}	
	}

	/** Draws the TerrainLevel stored as a member variable to the screen. */
	private void draw() 
	{
		//Sets the projection matrix of the SpriteBatch to the world camera's, so that the sprites get rendered relative to world coordinates.
		batcher.setProjectionMatrix(worldCamera.combined);

		//Begins the sprite rendering batch.
		batcher.begin();
		
		//Cycles through the rows of TerrainLayerRenderers
		for(int i = 0; i < layerRenderers.length; i++)
		{
			//Cycles through the columns of the TerrainLayerRenderers array.
			for(int j = 0; j < layerRenderers[i].length; j++)
			{
				//Stores the TerrainLayerRenderer instance that is being cycled through
				TerrainLayerRenderer layerRenderer = layerRenderers[i][j];
				
				//Retrieves the TerrainLayer that this TerrainLayerRenderer is assigned to draw.
				TerrainLayer layer = layerRenderer.getTerrainLayer();
				
				//If the TerrainLayer that the renderer draws is not visible
				if(!isInCamera(layer))
				{
					//Skip rendering this TerrainLayer, since it cannot be seen.
					continue;
				}
				
				//If the TerrainLayer is not on the same row as the player, draw the TerrainLayer transparent to differentiate between the active and inactive TerrainLayers.
				boolean transparent = layer.getRow() != terrainLevel.getCenterRow();
				
				//Draws the TerrainLayer represented by the TerrainLayerRenderer. Accepts whether or not the TerrainLayer should be drawn transparently.
				layerRenderer.draw(batcher, transparent);
			}
		}
		
		//Commits the sprites to the SpriteBatch instance and draws them to the screen.
		batcher.end();
		
	}
	
	/** Returns true if the given layer is inside the viewable region of the world's camera. */
	public boolean isInCamera(TerrainLayer layer)
	{	
		//Stores the end-points of the rectangle which encompasses the TerrainLayer, found using the right and left end points of the layer.
		float x1 = layer.getLeftPoint().x;
		float y1 = layer.getLeftPoint().y;
		float x2 = layer.getRightPoint().x;
		float y2 = layer.getRightPoint().y;

		//If the given TerrainLayer is a cosine function, the height of the center of the wave has to be taken into account.
		if(layer.getTerrainType() == TerrainType.COSINE)
		{
			//Re-define the second y-value of the rectangle encompassing the layer to be the center y-value of the cosine function.
			y2 = layer.getBottomLayerHeight(layer.getCenterX());
		}
		
		//Sets the bottom left-point of the rectangle to the bottom-left point of the line.
		lineBounds.setPosition(Math.min(x1, x2), Math.min(y1, y2));
		//Finds the size the line would encapsulate as a rectangle. We denote the points as the diagonal intersector of the rectangle.
		lineBounds.setSize(Math.abs(x2 - x1), Math.abs(y2 - y1));
		
		//If the bounds of the line are inside the viewable region of the camera, return true
		if(lineBounds.insideCamera(worldCamera))
			return true;
		
		//Returns false if the line denoted as the diagonal intersector of a rectangle does not fall inside the camera.
		return false;
	}
	
	/** Called whenever the screen is resized. The argument contains the factor by which the screen had to be scaled to fit the device's screen
	 *  We have to re-scale our lines by this factor for the lines to look the same size no matter the screen. */
	public void resize(float screenScale)
	{
		//Sets the width of the OpenGL ES lines that will draw the level geometry. We take the default width at target resolution, and multiply it by the screen's scale.
		//Gdx.gl10.glLineWidth(DEFAULT_LINE_WIDTH * screenScale);
	}

	/** Returns the TerrainLevel instance that this renderer is assigned to draw. */
	public TerrainLevel getTerrainLevel() {
		return terrainLevel;
	}

	/** Sets the TerrainLevel instance that this renderer will be assigned to draw. */
	public void setTerrainLevel(TerrainLevel terrainLevel) 
	{
		//Stores the TerrainLevel that this renderer is assigned to draw.
		this.terrainLevel = terrainLevel;
		
		//Creates the matrix of TerrainLayerRenderers, each of which is used to draw a specific TerrainLayer in the level. 
		createLayerRenderers(terrainLevel);
	}
}
