package com.jonathan.survivor.managers;

import java.util.HashMap;

import com.badlogic.gdx.utils.Pool;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.entity.Tree;

public class GameObjectManager
{
	/** Stores the player GameObject. */
	private Player player;
	
	/** Stores HashMap of GameObjectPools where every GameObject class is a key to a pool of its GameObjects. Used for easy management of pools. */
	private HashMap<Class, Pool> poolMap;
	
	/** Stores the Assets singleton used to access the visual assets used by the game. */
	Assets assets = Assets.instance;
	
	public GameObjectManager()
	{
		//Creates the player GameObject to be controlled by the user.
		createPlayer();
		
		//Creates a new HashMap of pools where every GameObject class is a key to a pool of its GameObjects. Used for easy management of pools.
		poolMap = new HashMap<Class, Pool>();
		
		//Inserts a pool into the HashMap for each GameObject type which requires a pool.
		poolMap.put(Tree.class, new TreePool());
	}
	
	/** Creates the player GameObject, along with his skeleton. */
	private void createPlayer()
	{
		//Creates a new Player GameObject instance.
		player = new Player();
		
		//Ensures that the skeleton is in its setup pose by default.
		player.getSkeleton().setToSetupPose();	
	}
	
	
	/** Returns the Player GameObject being managed by the manager. */
	public Player getPlayer() {
		return player;
	}
	
	/** Gets a tree GameObject of the given class cached inside one of the Manager's pools. No same GameObject will be returned twice until it is freed using freeGameObject(). 
	 * @param <T> The type of GameObject that wants to be retrieved. 
	 * @return A GameObject of the given class */
	public <T> T getGameObject(Class<T> goClass)
	{
		System.out.println("Amount of free objects in the pool: " + poolMap.get(goClass).getFree());
		//Either returns a tree inside the pool that is free, or creates a new tree and returns it if no free ones are available.
		return (T) poolMap.get(goClass).obtain();
		
	}
	
	/** Frees a tree back inside the manager's internal TreePool. Tells the manager that the tree is no longer in use, and that the tree can be
	 *  returned when getTree() is called. */
	public <T> void freeGameObject(GameObject gameObject, Class<T> goClass)
	{
		//Puts the tree back inside the pool for later use.
		poolMap.get(goClass).free(gameObject);
	}
}
