package com.jonathan.survivor.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.entity.Zombie;

public class VersusAnimation
{
	/** Stores the Assets singleton of the game used to fetch assets to draw the Spine animations. */
	protected Assets assets = Assets.instance;
	
	/** Stores the world whose enterCombat() method we call when the versus animation is finished. */
	private World world;
	/** Stores the SpriteBatcher used to draw the animations. */
	private SpriteBatch batcher;
	
	/** Stores the OrthographicCamera used to view the world. */
	private OrthographicCamera worldCamera;
	
	/** Stores the Spine Skeleton instance used to display and render the Versus HUD Animation. */
	private Skeleton versusSkeleton;
	
	/** Holds the amount of time in seconds that the animation has been playing. */
	private float playTime; 
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
	/** Accepts the SpriteBatch instance where Spine skeletons are drawn, and the camera used to view the world, which allows the animations to be centered on the screen.
	 *  Also accepts the World, which this class will signal when the animation is done. */
	public VersusAnimation(World world, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the given arguments in their respective member variables.
		this.world = world;
		this.batcher = batcher;
		this.worldCamera = worldCamera;
		
		//Creates the skeleton used to display the animation of the versus animation.
		versusSkeleton = new Skeleton(assets.versusAnimSkeletonData);
		
		//Ensures that the versus animation is in its default position, so that its animations play well.
		versusSkeleton.setToSetupPose();
	}
	
	/** Draws the animation to the center of the screen. */
	public void draw(float deltaTime)
	{
		//Clears the screen with the overlay color so that the game is hidden behind the pause menu.
		//Gdx.gl.glClearColor(OVERLAY_COLOR.r, OVERLAY_COLOR.g, OVERLAY_COLOR.b, OVERLAY_COLOR.a);
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		//Sets the versus animation to play at the center of the screen.
		versusSkeleton.setX(worldCamera.position.x);
		versusSkeleton.setY(worldCamera.position.y);
		
		//Apply the 'Play' animation to the animation's skeleton. Second and third arguments specify how much time the animation has been playing, third indicates we want to 
		//loop the animation, and last is an array where any possible animation events are delegated.
		assets.versusPlay.apply(versusSkeleton, playTime, playTime, false, events);
		
		//Updates the Skeleton in the world.
		versusSkeleton.updateWorldTransform();
		
		//Draws the box's skeleton using the universal SkeletonRenderer instance, along with the GameScreen's SpriteBatch.
		assets.skeletonRenderer.draw(batcher, versusSkeleton);
		
		//Increment the amount of time that the versus animation has been playing.
		playTime += Gdx.graphics.getDeltaTime();
		
		//Checks if the animation has completed. If so, switch to the player to combat mode
		checkFinished();
	}

	/** Checks if the versus animation has finished playing. If so, the world's correct methods are delegated to switch the player to combat mode. */
	private void checkFinished() 
	{		
		//If the animation is done playing
		if(playTime > assets.versusPlay.getDuration())
		{
			//Make the player enter combat with the zombie he encountered before the versus animation was played.
			world.enterCombat();
			
			//Reset playTime to zero for the next time that the animation plays. Like this, the animation will restart from the beginning.
			playTime = 0;
		}
	}
}
