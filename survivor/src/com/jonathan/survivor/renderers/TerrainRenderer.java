package com.jonathan.survivor.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jonathan.survivor.TerrainLayer;
import com.jonathan.survivor.TerrainLayer.TerrainType;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.math.Rectangle;
import com.jonathan.survivor.math.Vector2;

public class TerrainRenderer 
{
	
	/** Stores the default width of a line used to draw the geometry for the terrain. This is the width on the target resolution of the game. */
	private static final float DEFAULT_LINE_WIDTH = 0.05f;
	/** Stores the amount of segments used to draw a cosine function for a TerrainLayer. */
	private static final int COSINE_SEGMENTS = 75;
	
	private Rectangle lineBounds;
	
	/** Stores the camera where the terrain is drawn. In this case, the world camera. */
	private OrthographicCamera worldCamera;
	
	/** Stores the ShapeRenderer instance used to draw the level geometry. */
	private ShapeRenderer shapeRenderer;
	
	/** Holds renderer the used to place and render the terrain fragments individually. */
	private TerrainSpriteRenderer terrainSpriteRenderer;
	
	/** Accepts the camera where the terrain lines will be drawn. */
	public TerrainRenderer(OrthographicCamera worldCamera)
	{
		//Stores the camera where the terrain lines will be drawn.
		this.worldCamera = worldCamera;
		
		//Creates the ShapeRenderer instance used to draw the level geometry with lines.
		shapeRenderer = new ShapeRenderer();
		
		//Creates the TerrainRenderer instance used to draw the individual snow fragments which form the TerrainLevel.
		terrainSpriteRenderer = new TerrainSpriteRenderer();
		
		lineBounds = new Rectangle();
		
		//Enables OpenGL ES to draw smooth lines to ensure level geometry looks anti-aliased.
		Gdx.gl.glEnable(GL10.GL_LINE_SMOOTH);
	}
	
	/** Renders the given terrainLevel's geometry using OpenGL ES lines. */
	public void render(TerrainLevel level)
	{
		//Retrieves the TerrainLayers contained by the level. They are stored in a 2D array. These are the only layers that are visible to the user.
		TerrainLayer[][] layers = level.getTerrainLayers();
		
		//Sets the projection matrix of the ShapeRenderer to the world camera's, so that the shapes get rendered relative to world coordinates.
		shapeRenderer.setProjectionMatrix(worldCamera.combined);

		//Begins the shape rendering batch. We specify to draw lines.
		shapeRenderer.begin(ShapeType.Line);
		//Sets the line to be black.
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		
		//Cycles through the rows of TerrainLayers
		for(int i = 0; i < layers.length; i++)
		{
			//Cycles through the columns of the TerrainLayers array.
			for(int j = 0; j < layers[i].length; j++)
			{
				//Stores the TerrainLayer instance that is being cycled through
				TerrainLayer layer = layers[i][j];
				
				//Stores the bottom left and right end-points of the TerrainLayer using TerrainLayer.getLeft/RightPoint():Vector2.
				Vector2 leftEndPoint = layer.getLeftPoint();
				Vector2 rightEndPoint = layer.getRightPoint();
				
				//If the line is not inside the camera's viewable region, don't draw it.
				if(!isInCamera(layer))
				{
					continue;
				}
				
				//If the TerrainType of the layer is not COSINE, the layer type is either CONSTANT or LINEAR. That means that the layer's geometry can be modeled 
				//using a straight line. Thus, draw a straight line.
				if(layers[i][j].getTerrainType() != TerrainType.COSINE)
				{
					//Draw a straight line going from the right end point to the left end point of the TerrainLayer. This draws the bottom portion of the layer since
					//the end points specify the position for the bottom of the layer.
					shapeRenderer.line(rightEndPoint.x, rightEndPoint.y, leftEndPoint.x, leftEndPoint.y);
					
					//If we are cycling through the last row of the layers array, we have reached the top-most layer. Thus, draw the top portion of the layer. It wasn't
					//necessary to do so before because the top-portion of the previous layers were drawn by the bottom portions of the next layers.
					if(i == layers.length-1)
						//Draws the top portion of the layer by drawing a line from the left end point to the right end point and off-setting it up by the layer height.
						shapeRenderer.line(rightEndPoint.x, rightEndPoint.y + TerrainLayer.LAYER_HEIGHT, leftEndPoint.x, leftEndPoint.y + TerrainLayer.LAYER_HEIGHT);
				}
				//Else, if we are here, the TerrainLayer has the geometry of a cosine function. Thus, draw the layer using a series of lines to model a cosine function.
				else
				{
					//Finds the width of each line segment by taking the width of the layer, and dividing it by the amount of segments we want.
					float segmentWidth = TerrainLayer.LAYER_WIDTH / COSINE_SEGMENTS;
					
					//Cycles from zero to the amount of desired segments to draw the cosine function with.
					for(int segment = 0; segment < COSINE_SEGMENTS; segment++)
					{
						//Stores the x-position of left-end of the segment. Found by taking the left end point of the layer, plus a segment width for each segment.
						float x1 = leftEndPoint.x + segmentWidth*segment;
						//Stores the y-position of the left-end of the segment. Found by finding the height of the bottom of the layer at the point's x-position.
						float y1 = layers[i][j].getBottomLayerHeight(x1);
						
						//Finds the x-position of the right-end of the segment simply by adding a segment width to the left-end's x-position.
						float x2 = x1 + segmentWidth;
						
						//Cap the segment's right-end x-position to the x-position of the right end-point of the layer. Ensure we don't draw too much.
						x2 = (x2 > rightEndPoint.x)? rightEndPoint.x : x2;
						
						//Stores the y-position of the right-end of the segment. Found by finding the height of the bottom of the layer at the point's x-position.
						float y2 = layers[i][j].getBottomLayerHeight(x2);
						
						//If the line is not inside the camera's viewable region, don't draw it. The two-end points of the line are accepted as arguments.
						/*if(!isInCamera(x1, y1, x2, y2))
						{
							continue;
						}*/

						//Draws a segment of the bottom of the cosine function. Renders a line going from the left-end segment to the right-end. Note that the y-value
						//of the segment is found using 'TerrainLayer.getBottomLayerHeight()'. We take the bottom height since we are drawing the bottom layer portion.
						shapeRenderer.line(x1, y1, x2, y2);
						
						//If we are cycling through the last row of the layers array, we have reached the top-most layer. Thus, draw the top portion of cosine function.
						//It wasn't necessary to do so before because the top-portion of the previous layers were drawn by the bottom portions of the next layers.
						if(i == layers.length-1)
							//Draws the top portion of the layer by drawing the cosine line segment from the left-end point of the segment to its right end-point and.
							//Note that the y-value of the segment is found using 'TerrainLayer.getTopLayerHeight()'. We take the top height since we are drawing 
							//the top portion of the layer.
							shapeRenderer.line(x1, layers[i][j].getTopLayerHeight(x1), x2, layers[i][j].getTopLayerHeight(x2));
					}
				}
			}
		}
		
		//Commits the lines to the ShapeRenderer and draws them to the screen.
		shapeRenderer.end();
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
}
