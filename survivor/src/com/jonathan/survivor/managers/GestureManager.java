package com.jonathan.survivor.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.World;
import com.jonathan.survivor.World.WorldState;
import com.jonathan.survivor.entity.Human.State;

public class GestureManager extends GestureAdapter 
{
	/** Stores the minimum speed the user has to fling to make the player jump or fall. */
	public static final float JUMP_FLING_SPEED = 300;
	
	/** Stores the world that the manager will modify according to user input. */
	private World world;
	
	/** True if the game is paused, so that no gestures should be registered nor handled. */
	private boolean paused;
	
	/** Creates an InputManager with the given world. This manager receives all touch events and reacts by calling the appropriate methods for the World. */
	public GestureManager(World world)
	{
		//Stores the world instance the InputManager will control.
		this.world = world;
	}
	
	/** Called when the user flicks the screen. Velocity is in pixels/second. This method is also called on Desktop. */
	@Override
	public boolean fling(float velocityX, float velocityY, int button)
	{
		//Return the method if the gesture manager is paused. Disallows swipe events from being registered and handled.
		if(paused)
			return false;
		
		//If the player is currently in exploration state
		if(world.getWorldState() == WorldState.EXPLORING)
		{
			//If the y-velocity of the swipe is less than the minimum fling speed, make the player jump. Note that an upward swipe has negative y-velocity.
			if(velocityY < -JUMP_FLING_SPEED)
			{
				if(world.getPlayer().getState() != State.JUMP && world.getPlayer().getState() != State.FALL)
				{
					//Make the player jump.
					world.getPlayer().jump();
					
					//If the player is on a terrain level
					if(world.getLevel() instanceof TerrainLevel)
					{
						//Shift the layers in the level up, so that the top of the level is seen.
						((TerrainLevel)world.getLevel()).shiftLayersUp();
					}
				}
			}
			//If the y-velocity of the swipe is greater than the minimum fling speed, make the player fall. Note that a downward swipe has positive y-velocity.
			else if(velocityY > JUMP_FLING_SPEED)
			{
				//If the world's level is a terrain level, and the player is not falling already, make the player fall.
				if(world.getLevel() instanceof TerrainLevel && world.getPlayer().getState() != State.FALL)
				{
					//Make the player fall down a layer.
					world.getPlayer().fall();
					
					//Shift the layers in the level down, so that the bottom of the level is seen.
					((TerrainLevel)world.getLevel()).shiftLayersDown();
						
				}
			}
		}
		
		return false;
	}
	
	/** Pauses the GestureManager so that it doesn't call any of the world's methods. Effectively pauses gesture handling. */
	public void pause()
	{
		//Set the paused flag to true.
		paused = true;
	}
	
	/** Resumes the GestureManager so that it can call the world's methods based on touch gestures. Effectively resumes gesture handling. */
	public void resume()
	{
		paused = false;
	}
}
