package com.jonathan.survivor;

import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Human;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.managers.GameObjectManager;
import com.jonathan.survivor.math.Vector2;

public class World 
{
	/** Stores the acceleration due to gravity in the world in m/s^2 */
	public static final Vector2 GRAVITY = new Vector2(0, -15f);
	
	/** Stores the seed used to generate the geometry of the level and randomly place its GameObjects. */
	private int worldSeed;

	/** Stores the profile used to create the world from a save file. */
	private Profile profile;
	
	/** A state the GameScreen needs to know about to change the UI */
	public enum WorldState {
		EXPLORING, FIGHTING, GAME_OVER
	};
	
	/** Stores the state of the world, which simply dictates the GUI the GameScreen should display. */
	private WorldState worldState;

	/** Holds the GameObject Manager instance used to manage the GameObjects in the world. */
	private GameObjectManager goManager;
	
	/** Stores the currently-active level of the world that is being displayed. Determines the walkable area of the world. */
	private Level level;
	/** Stores an instance of a terrain level, used when the player is outside in the procedurally-generated terrain. */
	private TerrainLevel terrainLevel;
	
	/** Holds the Player GameObject that the user is guiding around the world. */
	private Player player;

	public World(int worldSeed, Profile profile)
	{
		//Stores the seed used to randomly generate the world.
		this.worldSeed = worldSeed;
		//Stores the profile used to generate the world from its save data.
		this.profile = profile;
		
		//Sets the world to its exploration state, since the user always starts off in exploration state. This ensures the correct UI is drawn.
		worldState = WorldState.EXPLORING;
		
		//Creates a GameObjectManager which stores and creates and manages all the instances of GameObjects.
		goManager = new GameObjectManager();
		
		//Creates a new TerrainLevel using the world seed. This level is a container of TerrainLayers that the user can traverse. The terrain row and
		//column offsets from the profile are specified so that the user begins in the same place he last left off in the terrain. The GameObjectManager
		//is passed to populate the TerrainLevel with GameObjects retrieved from pools.
		terrainLevel = new TerrainLevel(worldSeed, profile.getTerrainRowOffset(), profile.getTerrainColOffset(), goManager);
		//Sets the terrain level as the currently active level to be displayed by the world.
		setLevel(terrainLevel);
		
		//Stores the player created inside the GameObjectManager inside a member variable.
		player = goManager.getPlayer();
		//Sets up the player's initial values when being dropped into the world.
		setupPlayer();
	}
	
	/** Called every frame to update the world and its GameObjects. */
	public void update(float deltaTime)
	{
		//Updates the player, his movement, and his game logic.
		updatePlayer(deltaTime);
		//Updates the GameObjects contained by the world's level, such as trees.
		updateLevelObjects(deltaTime);
	}
	
	/**Updates the player, his movement, and his game logic. */
	private void updatePlayer(float deltaTime)
	{
		if(player.getState() == State.IDLE)
		{
			
		}
		//If the player is jumping
		if(player.getState() == State.JUMP || player.getState() == State.FALL)
		{
			//Set the player's acceleration to the acceleration gravity.
			player.setAcceleration(GRAVITY.x, GRAVITY.y);
			
			//If the player has collided with the ground
			if(checkGroundCollision(player))
			{
				//Set the player to IDLE state.
				player.setState(State.IDLE);
				//Take off any acceleration from the player.
				player.setAcceleration(0, 0);
				//Set The player's velocity to zero.
				player.setVelocity(0,0);
				//Set the player's bottom y-position to the height of the ground.
				player.setY(terrainLevel.getGroundHeight(player.getX()));
			}
		}
		//Else, if the player is walking
		else if(player.getState() == State.WALK)
		{
			//If the player is walking to the right
			if(player.getDirection() == Direction.RIGHT)
			{
				//Apply a positive x-velocity to the player using the predefined walking speed constant.
				player.setVelocity(Player.MAX_WALK_SPEED, 0);
			}
			//Else, if the player is walking to the left
			else
			{
				//Apply a negative x-velocity to the player using the predefined walking speed constant.
				player.setVelocity(-Player.MAX_WALK_SPEED, 0);
			}
			
			//Checks if the player switched layers by either moving to the right or left of his current layer. If so, we move the player over by a cell.
			checkForLayerSwitch(player);
			
			//Makes the player follow the level's ground.
			lockToGround(player);
		}
		
		//Updates the position of the player based on his velocity and updates his collider's position.
		player.update(deltaTime);
	}
	
	/** Updates the GameObjects contained by the currently active level of the world. */
	private void updateLevelObjects(float deltaTime)
	{
		//Retrieves all of the GameObjects stored inside the world's level
		Array<GameObject> gameObjects = level.getGameObjects();
		
		for(int i = 0; i < gameObjects.size; i++)
		{
			GameObject go = gameObjects.get(i);
			go.update(deltaTime);
		}
	}
	
	/** Locks the GameObject to the ground when it is moving. Makes it so that the GameObject follows the path of the ground. */
	public void lockToGround(GameObject gameObject)
	{
		//If the currently-active level is a TerrainLevel
		if(level instanceof TerrainLevel)
		{
			//Find the TerrainLayer where the GameObject resides using a layer lookup based on the GameObject's terrain cell.
			TerrainLayer terrainLayer = terrainLevel.getTerrainLayer(gameObject.getTerrainCell());
			//Sets the GameObject's bottom y-position to the y-position of the ground at the given x-position. This effectively sticks the object to the ground.
			gameObject.setY(terrainLayer.getGroundHeight(gameObject.getX()));
		}
	}
	
	public void checkForLayerSwitch(GameObject gameObject)
	{
		//If the currently-active level is a TerrainLevel
		if(level instanceof TerrainLevel)
		{
			//Find the TerrainLayer where the GameObject resides using a layer lookup based on the GameObject's terrain cell.
			TerrainLayer terrainLayer = terrainLevel.getTerrainLayer(gameObject.getTerrainCell());
			
			//If the the player has moved to the right of his current layer
			if(gameObject.getX() > terrainLayer.getRightPoint().x)
			{
				//Move the GameObject's cell to the right so that the GameObject knows that he has changed cells.
				gameObject.getTerrainCell().moveRight();
				
				//Shift the layers of the level to the right so that the player stays in the center cell.
				terrainLevel.shiftLayersRight();
			}
			//If the the player has moved to the left of his current layer
			else if(gameObject.getX() < terrainLayer.getLeftPoint().x)
			{
				//Move the GameObject's cell to the left so that the GameObject knows that he has changed cells.
				gameObject.getTerrainCell().moveLeft();
				
				//Shift the layers of the level to the left so that the player stays in the center cell.
				terrainLevel.shiftLayersLeft();
			}
		}
	}
	
	/** This method is called once to make the player move to the right. */
	public void movePlayerRight()
	{
		//If the player is jumping or falling, don't let him move left, or serious glitches will occur.
		if(player.getState() == State.JUMP || player.getState() == State.FALL)
			return;
		
		//Sets the player to the walking state so that the world can apply a velocity to it.
		player.setState(State.WALK);
		//Makes the player walk in the right direction. This world's update() method knows how to interpret this direction.
		player.setDirection(Direction.RIGHT);
	}
	
	/** This method is called once to make the player move to the left. */
	public void movePlayerLeft()
	{
		//If the player is jumping or falling, don't let him move left, or serious glitches will occur.
		if(player.getState() == State.JUMP || player.getState() == State.FALL)
			return;
		
		//Sets the player to the walking state so that the world can apply a velocity to it.
		player.setState(State.WALK);
		//Makes the player walk in the left direction.
		player.setDirection(Direction.LEFT);
	}
	
	/** Stops the given Human GameObject from moving. */
	public void stopMoving(Human human) 
	{
		//Stops the human from walking in the x-direction.
		human.setVelocityX(0);
		
		//Only set the human to idle state if he is not jumping or falling. Otherwise, he will be set to IDLE whilst jumping/falling, causing glitches.
		//Regardless, the human will be set to IDLE once he lands his jump/fall.
		if(human.getState() != State.JUMP && human.getState() != State.FALL)
			//Tells the human instance that it is in idle state so that it stops moving. 
			human.setState(State.IDLE);
	}
	
	/** Returns true if the player is jumping or falling and has fell past the ground. */
	public boolean checkGroundCollision(Player player)
	{
		//If the player is in a TerrainLevel (i.e., a layer level)
		if(level instanceof TerrainLevel)
		{
			//If the player is falling and has fallen below ground height
			if(player.getVelocity().y < 0 && player.getY() < terrainLevel.getGroundHeight(player.getX()))
			{
				//Return true, as the player has fallen below the ground.
				return true;
			}
		}
		
		//Return false if the player has not been found to be falling below ground height.
		return false;
	}	
	
	/** Sets up the player's initial variables to ensure that the player is placed at the right location. */
	public void setupPlayer()
	{
		//Sets the player's bottom-center position at the current level's starting point. 
		player.setPosition(level.getPlayerStartX(), level.getPlayerStartY());
		
		//If the currently-active level is a TerrainLevel, we must position him in the right cell coordinates.
		if(level instanceof TerrainLevel)
		{
			//Sets the terrain cell of the player to the center cell of the level. The player will always spawn in the center of a TerrainLevel.
			player.setTerrainCell(terrainLevel.getCenterRow(), terrainLevel.getCenterCol());
		}
	}
	
	/** Returns the currently active level of the world used to dictate the walkable area the world. */
	public Level getLevel()
	{
		return level;
	}
	
	/** Sets the level used to determine the walkable area of the world. Also changes the GameObjects on screen. */
	public void setLevel(Level level)
	{
		this.level = level;
	}
	
	/** Returns the state of the world, used to tell the GameScreen how to render its GUI. */
	public WorldState getWorldState() 
	{
		return worldState;
	}

	/** Sets the state of the world. This can modify the way the GameScreen displays its GUI. */
	public void setWorldState(WorldState worldState) 
	{
		this.worldState = worldState;
	}
	
	/** Gets the GameObjectManager used to create and manager GameObjects in the world. */
	public GameObjectManager getGOManager()
	{
		return goManager;
	}
	
	/** Sets the GameObjectManager used to create and manager GameObjects in the world. */
	public void setGOManager(GameObjectManager goManager)
	{
		this.goManager = goManager;
	}
	
	/** Gets the player the user is controlling in the world. */
	public Player getPlayer()
	{
		return player;
	}
	
	/** Sets the Player GameObject being controlled by the user. */
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
}
