package com.jonathan.survivor.managers;

import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.Mode;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Zombie;

/*
 * Updates a zombie, along with his artificial intelligence. Helper class to separate zombie updating from the World class.
 */

public class ZombieManager 
{
	/** Stores the amount of time the zombie stays idle before starting to move again. */
	private static final float IDLE_TIME = 3;
	
	/** Holds the World instance that the zombies are a part of. Used to access the convenience methods created for the player and needed for the zombies. */
	private World world;
	
	/** Creates a ZombieManager which updates zombies and their AI. Accepts the World instance used by the game in order to access the convenience methods
	 *  used by the player and similarly needed to update zombies.s
	 */
	public ZombieManager(World world)
	{
		//Stores the given constructor arguments in their respective member variables.
		this.world = world;
	}
	
	/** Updates the game logic for the given zombie. */
	public void update(Zombie zombie, float deltaTime) 
	{
		//Updates the zombie's AI before updating its game logic. In essence, this method determines the zombie's next move.
		updateAI(zombie, deltaTime);
		
		//If the zombie is in idle state
		if(zombie.getState() == State.IDLE)
		{
			
		}
		//Else, if the player is walking
		else if(zombie.getState() == State.WALK)
		{
			//If the player is walking to the right
			if(zombie.getDirection() == Direction.RIGHT)
			{
				//Apply a positive x-velocity to the player using the predefined walking speed constant.
				zombie.setVelocity(Zombie.MAX_WALK_SPEED, 0);
			}
			//Else, if the player is walking to the left
			else
			{
				//Apply a negative x-velocity to the player using the predefined walking speed constant.
				zombie.setVelocity(-Zombie.MAX_WALK_SPEED, 0);
			}
			
			//Checks if the zombie switched layers by either moving to the right or left of his current layer. If so, we move the zombie over by a cell.
			world.checkForLayerSwitch(zombie);
			
			//Ensures the zombie is always at ground height.
			world.lockToGround(zombie);
		}
		
		//Updates the position of the zombie based on his velocity. Also updates its collider's position.
		zombie.update(deltaTime);
	}

	/** Updates the zombie's state according to the principles of his artifical intelligence. */
	private void updateAI(Zombie zombie, float deltaTime) 
	{
		//If deltaTime is zero, the game is paused. Thus, don't compute AI for this render call.
		if(deltaTime == 0)
			return;
		
		//If the zombie is in exploration mode, update its AI accordingly.
		if(zombie.getMode() == Mode.EXPLORING)
		{
			//If the zombie is in IDLE state (i.e., he is just standing there)
			if(zombie.getState() == State.IDLE)
			{
				//If the zombie has been in IDLE state for long enough, make him move again.
				if(zombie.getStateTime() > IDLE_TIME)
				{
					//If the zombie was facing the left when idle
					if(zombie.getDirection() == Direction.LEFT)
					{
						//Make the zombie walk to the right
						world.walk(zombie, Direction.RIGHT);
					}
					//Else, if the zombie was facing to the right when idle
					else 
					{
						//Make the zombie walk left.
						world.walk(zombie, Direction.LEFT);
					}
				}
			}
			//If the zombie is currently walking
			if(zombie.getState() == State.WALK)
			{
				//If the zombie is close to the edge of his TerrainLayer, and has been walking for more than one second
				if(world.closeToLayerEdge(zombie) && zombie.getStateTime() > 1f)
				{
					//Tell the zombie to stop walking. We want the zombie to stay within the confines of his designated layer.
					world.stopMoving(zombie);
				}
			}
		}
		
	}
}