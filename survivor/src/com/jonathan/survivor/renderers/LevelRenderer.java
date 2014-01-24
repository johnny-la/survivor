package com.jonathan.survivor.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.jonathan.survivor.Level;
import com.jonathan.survivor.TerrainLevel;

/*
 * Helper class used to draw level geometry. Level instances are passed to their designated Renderer objects to be drawn.
 */

public class LevelRenderer 
{	
	/** Stores the camera where the terrain is drawn. In this case, the world camera. */
	private OrthographicCamera worldCamera;
	
	/** Stores the renderer used to draw terrain. */
	private TerrainRenderer terrainRenderer;
	
	public LevelRenderer(OrthographicCamera worldCamera)
	{
		//Stores the camera where the level should be drawn.
		this.worldCamera = worldCamera;
		
		//Creates a TerrainRenderer to draw terrain geometry. Passes the camera where the lines should be drawn.
		terrainRenderer = new TerrainRenderer(worldCamera);
	}
	
	/** Renders a level's geometry. This method is a helper method which delegates the level instance to a more specific renderer. */
	public void render(Level level)
	{
		//If the level we want to draw is a TerrainLevel
		if(level instanceof TerrainLevel)
			//Delegate the rendering to the TerrainRenderer.
			terrainRenderer.render((TerrainLevel)level);
	}
	
	/** Called whenever the screen is resized. The argument contains the factor by which the screen had to be scaled compared to the target
	 *  resolution. We have to rescale our lines by this factor for the lines to look the same size no matter the screen. */
	public void resize(float screenScale)
	{
		//Resizes the terrain lines by the amount we had to scale the default frustum to fit the target device.
		terrainRenderer.resize(screenScale);
	}
}
