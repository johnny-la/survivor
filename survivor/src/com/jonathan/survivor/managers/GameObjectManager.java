package com.jonathan.survivor.managers;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.Profile;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.ItemObject;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.entity.Tree;
import com.jonathan.survivor.inventory.Item;

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
		poolMap.put(ItemObject.class, new ItemObjectPool());
	}
	
	/** Creates the player GameObject, along with his skeleton. Accepts profile to re-create the player with his old settings. */
	private void createPlayer(Profile profile)
	{
		//Creates a new Player GameObject instance.
		player = new Player();
		
		//Set the player's loadout to the one saved in the profile.
		player.setLoadout(profile.getLoadout());
		
		//Ensures that the skeleton is in its setup pose by default.
		player.getSkeleton().setToSetupPose();	
	}
	
	
	/** Returns the Player GameObject being managed by the manager. */
	public Player getPlayer() {
		return player;
	}
	
	/** Spawns an ItemObject representing the given Item class. The (x,y) position is the bottom-center of the position where the ItemObject is spawned. */
	public <T> T spawnItemObject(Class<T> itemClass, float x, float y)
	{
		//Retrieves a free ItemObject in the internal pools of the GameObjectManager.
		ItemObject itemObject = getGameObject(ItemObject.class);
		
		//Shoots the ItemObject into the air at the given (x,y) position. Also sets the object to display the given item.
		itemObject.spawn(null, x, y);
		
		//Returns the ItemObject which was spawned to represent the Item from the given class.
		return (T) itemObject;
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
