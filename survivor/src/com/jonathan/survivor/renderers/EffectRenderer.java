package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.World;
import com.jonathan.survivor.World.WorldState;
import com.jonathan.survivor.animations.VersusAnimation;

/*
 * Renders the game's FX displayed on screen, such as the crosshairs.
 */

public class EffectRenderer 
{
	/** Stores the world, whose methods we call when certain animations are finished. */
	private World world;
	/** Stores the SpriteBatcher used to draw the Spine animations. */
	private SpriteBatch batcher;	
	
	/** Stores the OrthographicCamera where the Spine animations are drawn. */
	private OrthographicCamera worldCamera;
	
	/** Holds the CrosshairRenderer instance used to draw all of the gun crosshairs to the screen. */
	private CrosshairRenderer crosshairRenderer;
	
	/** Accepts the world from which we find the effects to draw, the SpriteBatch used to draw the effects, and the world camera
	 *  where the effects are rendered. */
	public EffectRenderer(World world, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the world where we find the GameObjects to draw.
		this.world = world;
		//Stores the SpriteBatch used to draw the GameObjects.
		this.batcher = batcher;
		//Stores the world camera where all the sprites are drawn.
		this.worldCamera = worldCamera;
		
		//Creates the CrosshairRenderer used to draw the crosshairs or trajectory lines of the ranged weapons. Accepts the world, which contains information about the crosshairs.
		crosshairRenderer = new CrosshairRenderer(world, worldCamera);
	}
	
	public void render(float deltaTime)
	{				
		//Render the crosshairs to the screen.
		crosshairRenderer.render(deltaTime);
	}
	
}
