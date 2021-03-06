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

public class KoAnimation
{
	/** Stores the Assets singleton of the game used to fetch assets to draw the Spine animations. */
	protected Assets assets = Assets.instance;
	
	/** Stores the world whose enterCombat() method we call when the versus animation is finished. */
	private World world;
	/** Stores the SpriteBatcher used to draw the animations. */
	private SpriteBatch batcher;
	
	/** Stores the OrthographicCamera used to view the world. */
	private OrthographicCamera worldCamera;
	
	/** Stores the Spine Skeleton used to show the KO animation when a character dies in COMBAT mode. */
	private Skeleton koSkeleton;
	
	/** Holds the amount of time in seconds that the animation has been playing. */
	private float playTime; 
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
	/** Accepts the SpriteBatch instance where Spine skeletons are drawn, and the camera used to view the world, which allows the animations to be centered on the screen.
	 *  Also accepts the World, which this class will signal when the animation is done. */
	public KoAnimation(World world, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the given arguments in their respective member variables.
		this.world = world;
		this.batcher = batcher;
		this.worldCamera = worldCamera;
		
		//Creates the skeleton used to display the ko animation.
		koSkeleton = new Skeleton(assets.koAnimSkeletonData);
		
		//Ensures that the ko animation is in its default position, so that its animations play normally.
		koSkeleton.setToSetupPose();
	}
	
	/** Draws the animation to the center of the screen. */
	public void draw(float deltaTime)
	{		
		//Sets the ko animation to play at the center of the screen.
		koSkeleton.setX(worldCamera.position.x);
		koSkeleton.setY(worldCamera.position.y);
		
		//Apply the 'Play' animation to the animation's skeleton. Second and third arguments specify how much time the animation has been playing, third indicates we want to 
		//loop the animation, and last is an array where any possible animation events are delegated.
		assets.koPlay.apply(koSkeleton, playTime, playTime, false, events);
		
		//Updates the Skeleton in the world.
		koSkeleton.updateWorldTransform();
		
		//Draws the box's skeleton using the universal SkeletonRenderer instance, along with the GameScreen's SpriteBatch.
		assets.skeletonRenderer.draw(batcher, koSkeleton);
		
		//Increment the amount of time that the versus animation has been playing.
		playTime += Gdx.graphics.getDeltaTime();
		
		//Checks if the animation has completed. If so, switch to the player back to exploration mode if the player beat the zombie.
		checkFinished();
	}

	/** Checks if the versus animation has finished playing. If so, the world's correct methods are delegated to switch the player to combat mode. */
	private void checkFinished() 
	{		
		//If the animation is done playing
		if(playTime > assets.koPlay.getDuration())
		{
			//Make the player and the zombie leave COMBAT mode and go back to EXPLORATION mode.
			world.exitCombat();
			
			//Reset playTime to zero for the next time that the animation plays. Like this, the animation will restart from the beginning.
			playTime = 0;
		}
	}
}
