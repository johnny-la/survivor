package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jonathan.survivor.World;
import com.jonathan.survivor.World.WorldState;
import com.jonathan.survivor.animations.KoAnimation;
import com.jonathan.survivor.animations.VersusAnimation;

/*
 * Renders the video-like animations created in Spine
 */

public class AnimationRenderer 
{
	/** Stores the world, whose methods we call when certain animations are finished. */
	private World world;
	/** Stores the SpriteBatcher used to draw the Spine animations. */
	private SpriteBatch batcher;	
	
	/** Stores the OrthographicCamera where the Spine animations are drawn. */
	private OrthographicCamera worldCamera;
	
	/** Holds the VersusAnimation instance used to display the animation of the player and the zombie brawling before entering combat mode. */
	private VersusAnimation versusAnimation;
	
	/** Stores the KoAnimation instance, which plays the Ko animation when a character dies in COMBAT mode. */
	private KoAnimation koAnimation;
	
	/** Accepts the world from which we find the GameObjects to draw, the SpriteBatch used to draw the Spine animations, and the world camera
	 *  where the Spine animations are rendered. */
	public AnimationRenderer(World world, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the world where we find the GameObjects to draw.
		this.world = world;
		//Stores the SpriteBatch used to draw the GameObjects.
		this.batcher = batcher;
		//Stores the world camera where all the sprites are drawn.
		this.worldCamera = worldCamera;
		
		//Creates a versusAnimation instance to draw the versus animation. Accepts the SpriteBatch instance needed to draw the animations, the worldCamera in order
		//to center the animation on the camera, and the world, to signal when the animation is finished..
		versusAnimation = new VersusAnimation(world, batcher, worldCamera);
		
		//Instantiates a KoAnimation instance which will play the KO Animation once a character dies in COMBAT mode.
		koAnimation = new KoAnimation(world, batcher, worldCamera);
	}
	
	public void render(float deltaTime)
	{				
		//If the world is in an ANIMATION state, begin batching sprites and draw the correct animation
		if(world.getWorldState() == WorldState.VERSUS_ANIMATION || world.getWorldState() == WorldState.KO_ANIMATION)
		{
			//Sets the projection matrix of the SpriteBatch to the camera's combined matrix. Ensure everything is drawn with the camera's coordinate system.
			batcher.setProjectionMatrix(worldCamera.combined);
			//Starts batching sprites to be drawn to the camera.
			batcher.begin();

			//Switches the WorldState to decide which animation to play
			switch(world.getWorldState())
			{
			//Case the versus animation should be playing
			case VERSUS_ANIMATION:
				versusAnimation.draw(deltaTime);	//Draws the versus animation to the screen.
				break;
			//Case the ko animation should be playing
			case KO_ANIMATION:
				koAnimation.draw(deltaTime);	//Draws the versus animation to the screen.
				break;
			}
			
			//Draws the sprites batched inside the SpriteBatcher.
			batcher.end();
		}
	}
	
}
