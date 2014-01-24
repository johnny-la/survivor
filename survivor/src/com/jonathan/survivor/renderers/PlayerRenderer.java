package com.jonathan.survivor.renderers;


import java.util.LinkedList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.Mode;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Player;

public class PlayerRenderer 
{	
	/** Stores the SpriteBatcher used to draw the player's sprites. */
	private SpriteBatch batcher;	
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the player. */
	private Assets assets = Assets.instance;
	
	/** Stores the OrthographicCamera where the player is drawn. */
	private OrthographicCamera worldCamera;
	
	/** Stores the Player GameObject we draw to the screen by extracting its position and state. */
	private Player player;
	/** Stores the Spine skeleton instance used to display the player and play his animations. */
	private Skeleton playerSkeleton;
	
	//Helper Array that's passed to the Animation.apply() method.
	private Array events = new Array<Event>();

	
	/** Accepts the player GameObject to render, the Spine skeleton used to to play his animations, the SpriteBatch used to draw the player, and the world 
	 * camera where the player is drawn. */
	public PlayerRenderer(Player player, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the SpriteBatch used to draw the player.
		this.batcher = batcher;
		//Stores the world camera where the player's sprites are drawn.
		this.worldCamera = worldCamera;
		
		//Stores the Player GameObject we draw to the screen by extracting its position and state.
		this.player = player;
		//Stores the Spine skeleton instance used to display the player and play his animations.
		this.playerSkeleton = player.getSkeleton();
	}
	
	/** Draws the player using his Spine skeleton, which stores his animations, sprites, and everything needed to draw the player. */
	public void render()
	{
		//If the player is looking left
		if(player.getDirection() == Direction.LEFT)
			//Flip his skeleton to look the other way. The skeleton looks right by default.
			playerSkeleton.setFlipX(true);
		//Else, if the player is facing right
		else
			//Don't flip his skeleton, since his skeleton is made to look to the right by default.
			playerSkeleton.setFlipX(false);
		
		//Set the player skeleton's bottom-center position to the bottom-center position of the Player GameObject.
		playerSkeleton.setX(player.getX());
		playerSkeleton.setY(player.getY());
		
		//If the player has just spawned
		if(player.getState() == State.SPAWN)
		{
			//Set the player's skeleton back to default pose.
			playerSkeleton.setToSetupPose();
			
			//Sets the character to IDLE state, indicating that the renderer has received the message that the player spawned.
			player.setState(State.IDLE);
		}
		//If the player is jumping
		if(player.getState() == State.JUMP)
		{
			//If the player is in exploration mode
			if(player.getMode() == Mode.EXPLORING)
			{
				//Play the player jump animation on the player's skeleton. The second and third floats specify how long the player has been in the jump
				//animation, the false boolean tells the animation only to play once, and the last is an array receiving the animation's events.
				assets.playerJump.apply(playerSkeleton, player.getStateTime(), player.getStateTime(), false, events);
			}
		}
		//Else, if the player is falling from one layer to the next
		else if(player.getState() == State.FALL)
		{
			//Play the player fall animation on the player's skeleton. The second and third floats specify how long the player has been in the fall
			//animation, the false boolean tells the animation only to play once, and the last is an array receiving the animation's events.
			assets.playerFall.apply(playerSkeleton, player.getStateTime(), player.getStateTime(), false, events);
		}
		
		//Updates the skeleton world transform. Must be done to reset SRT coordinates (or something).
		playerSkeleton.updateWorldTransform();
		
		//Draws the skeleton using the universal SkeletonRenderer instance used by the game.
		assets.skeletonRenderer.draw(batcher, playerSkeleton);
	}
}
