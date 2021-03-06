package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Projectile;

public class ProjectileRenderer 
{
	/** Stores the SpriteBatch instance used to display the ItemObject. */
	private SpriteBatch batcher;
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the interactive GameObjects. */
	private Assets assets = Assets.instance;
	
	/** Helper Color instance used to color the projectiles and to avoid creating new color instances every draw call. */
	private Color workingColor;
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
	/** Accepts the SpriteBatch instance used to render the Projectiles passed to the draw() method. */
	public ProjectileRenderer(SpriteBatch batcher)
	{
		//Stores the arguments in their respective member variables.
		this.batcher = batcher;
		
		//Instantiates the helper Color object used to color the ItemObjects.
		workingColor = new Color(Color.WHITE);
	}
	
	/** Draws the given Projectile on-screen. */
	public void draw(Projectile projectile)
	{
		//Stores the Skeleton instance owned by the Projectile, which allows the object to be drawn to the screen.
		Skeleton skeleton = projectile.getSkeleton();
		
		//Updates the position of the skeleton to that of the Projectile GameObject. The position for both is denoted by the bottom-center.
		skeleton.setX(projectile.getX());
		skeleton.setY(projectile.getY());
		
		//Reset the working color instance to WHITE so that the helper color starts from a clean slate.
		workingColor.set(Color.WHITE);
		
		//Play the projectile's IDLE animation. Second and third arguments specify how much time the object has been in its state, third indicates we want to 
		//loop the animation, and last is an array which stores animation events that are delegated.
		assets.projectileIdle.apply(skeleton, projectile.getStateTime(), projectile.getStateTime(), true, events);
		
		//Set the Projectile's color to the workingColor instance, which stored the color that the Projectile should be.
		skeleton.getColor().set(workingColor);
		
		//Updates the world transform of the skeleton.
		skeleton.updateWorldTransform();
		
		//Draws the Projectile to the world using the universal SkeletonRenderer instance stored in the Assets singleton.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}
}
