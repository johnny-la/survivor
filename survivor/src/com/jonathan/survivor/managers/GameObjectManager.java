package com.jonathan.survivor.managers;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Profile;
import com.jonathan.survivor.entity.Box;
import com.jonathan.survivor.entity.Earthquake;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.ItemObject;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.entity.Tree;
import com.jonathan.survivor.entity.Zombie;

public class GameObjectManager
{
	/** Stores the player GameObject. */
	private Player player;
	
	/** Stores HashMap of GameObjectPools where every GameObject class is a key to a pool of its GameObjects. Used for easy management of pools. */
	private HashMap<Class, Pool> poolMap;
	
	/** Stores the Assets singleton used to access the visual assets used by the game. */
	Assets assets = Assets.instance;
	
	/** Accepts the player's profile to re-create some GameObjects using save data. */
	public GameObjectManager(Profile profile)
	{
		//Creates the player GameObject to be controlled by the user. Passes in the player's profile to recreate the player with loaded settings.
		createPlayer(profile);
		
		//Creates a new HashMap of pools where every GameObject class is a key to a pool of its GameObjects. Used for easy management of pools.
		poolMap = new HashMap<Class, Pool>();
		
		//Inserts a pool into the HashMap for each GameObject type which has a pool.
		poolMap.put(Tree.class, new TreePool());
		poolMap.put(Box.class, new BoxPool());
		poolMap.put(Zombie.class, new ZombiePool());
		poolMap.put(ItemObject.class, new ItemObjectPool());
		poolMap.put(Earthquake.class, new EarthquakePool());
	}
	
	/** Creates the player GameObject, along with his skeleton. Accepts profile to re-create the player with his old settings. */
	private void createPlayer(Profile profile)
	{
		//Creates a new Player GameObject instance.
		player = new Player();
		
		//Set the player's loadout to the one saved in the profile.
		player.setLoadout(profile.getLoadout());
		//Retrieves the inventory from the profile and gives it to the player.
		player.setInventory(profile.getInventory());
		
		//Ensures that the skeleton is in its setup pose by default.
		player.getSkeleton().setToSetupPose();	
	}
	
	
	/** Returns the Player GameObject being managed by the manager. */
	public Player getPlayer() {
		return player;
	}
	
	/** Spawns an ItemObject at the given position. The (x,y) position is the bottom-center of the position where the ItemObject is spawned. 
	 * @param velocityMultiplier Multiplier which allows certain items to fly further or closer when spawned.
	 * @param direction Specifies the direction in which the item will fly when spawned.
	 */
	public ItemObject spawnItemObject(float x, float y, float velocityMultiplier, Direction direction)
	{
		//Retrieves a free ItemObject in the internal pools of the GameObjectManager.
		ItemObject itemObject = getGameObject(ItemObject.class);
		
		//Shoots the ItemObject into the air at the given (x,y) position in the given direction, with the given velocityMultiplier.
		itemObject.spawn(x, y, velocityMultiplier, direction);
		
		//Returns the ItemObject which was spawned to represent the Item from the given class.
		return itemObject;
	}
	
	/** Spawns an Earthquake instance at the given bottom-center (x,y) position. The earthquake will move in the given direction passed as an argument. */
	public Earthquake spawnEarthquake(float x, float y, Direction direction) 
	{
		//Retrieves an Earthquake instance stored inside an internal pool.
		Earthquake earthquake = getGameObject(Earthquake.class);
		
		//Fire the earthquake, starting at the given bottom-center (x,y) position, going in the given direction.
		earthquake.fire(x, y, direction);
		
		//Returns the earthquake that was spawned and fired.
		return earthquake;	
	}
	
	/** Gets a tree GameObject of the given class cached inside one of the Manager's pools. No same GameObject will be returned twice until it is freed using freeGameObject(). 
	 * @param <T> The type of GameObject that wants to be retrieved. 
	 * @return A GameObject of the given class */
	public <T> T getGameObject(Class<T> goClass)
	{
		//Either returns a tree inside the pool that is free, or creates a new tree and returns it if no free ones are available.
		return (T) poolMap.get(goClass).obtain();
	}
	
	/** Frees a gameObject back inside the manager's internal GameObject pools. Tells the manager that the GameObject is no longer in use, and that the it can be
	 *  returned when getGameObject() is called. */
	public <T> void freeGameObject(GameObject gameObject, Class<T> goClass)
	{
		//Puts the gameObject back inside its respective pool for later use.
		poolMap.get(goClass).free(gameObject);
	}
}
