package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.TerrainLevel;
import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.InteractiveObject;
import com.jonathan.survivor.entity.ItemObject;
import com.jonathan.survivor.entity.Projectile;
import com.jonathan.survivor.entity.Zombie;

public class GameObjectRenderer 
{
	/** Stores the world whose level and gameObjects we render. */
	private World world;
	/** Stores the SpriteBatcher used to draw the GameObjects. */
	private SpriteBatch batcher;	
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the GameObjects. */
	private Assets assets = Assets.instance;
	
	/** Stores the OrthographicCamera where the GameObjects are drawn. */
	private OrthographicCamera worldCamera;
	
	/** Stores the PlayerRenderer instance used to render the Player GameObject. */
	private PlayerRenderer playerRenderer;
	
	/** Stores the InteractiveObjectRenderer instance used to render any InteractiveObjects contained in the world's current level. */
	private InteractiveObjectRenderer interactiveObjectRenderer;
	/** Holds the ZombieRenderer object used to render every Zombie instance. */
	private ZombieRenderer zombieRenderer;
	/** Holds the ItemObjectRenderer used to render any ItemObjects that have been dropped in the world by scavenging GameObjects. */
	private ItemObjectRenderer itemObjectRenderer;
	/** Stores the ProjectileRenderer instance used to draw projectiles to the screen. */
	private ProjectileRenderer projectileRenderer;
	
	/** Accepts the world from which we find the GameObjects to draw, the SpriteBatch used to draw the GameObjects, and the world camera
	 *  where the GameObjects are drawn. */
	public GameObjectRenderer(World world, SpriteBatch batcher, OrthographicCamera worldCamera)
	{
		//Stores the world where we find the GameObjects to draw.
		this.world = world;
		//Stores the SpriteBatch used to draw the GameObjects.
		this.batcher = batcher;
		//Stores the world camera where all the sprites are drawn.
		this.worldCamera = worldCamera;
		
		//Creates a playerRenderer from the player GameObject, the World instance, the SpriteBatch instance to draw the player, and the worldCamera, where the Player will be drawn.
		playerRenderer = new PlayerRenderer(world.getPlayer(), world, batcher, worldCamera);
		
		//Creates an InteractiveObjectRenderer, passing the SpriteBatch it will use to draw the InteractiveObjects.
		interactiveObjectRenderer = new InteractiveObjectRenderer(batcher);
		//Instantiates the zombieRenderer used to draw all zombies to the screen. The zombies will be drawn using the given SpriteBatch argument.
		zombieRenderer = new ZombieRenderer(world, batcher);
		//Instantiates the ItemObjectRenderer which will be used to draw the dropped ItemObjects. The renderer will draw the objects using the passed SpriteBatch.
		itemObjectRenderer = new ItemObjectRenderer(batcher);
		//Creates the ProjectileRenderer used to draw the projectiles to the screen. The projectiles will be drawn using the SpriteBatch passed as a constructor argument.
		projectileRenderer = new ProjectileRenderer(batcher);
		
	}
	
	public void render(float deltaTime)
	{				
		//Sets the projection matrix of the SpriteBatch to the camera's combined matrix. Ensure everything is drawn with the camera's coordinate system.
		batcher.setProjectionMatrix(worldCamera.combined);
		//Starts batching sprites to be drawn to the camera.
		batcher.begin();
				
		//Renders the GameObjects stored inside the currently active level.
		renderLevelObjects(deltaTime);
		//Draws the player to the screen.
		playerRenderer.render(deltaTime);
		
		//Draws the sprites batched inside the SpriteBatcher.
		batcher.end();
	}

	/** Draws the GameObjects that are contained inside the world's level. */
	private void renderLevelObjects(float deltaTime) 
	{
		//Retrieves the currently active level's contained GameObjects.
		Array<GameObject> gameObjects = world.getLevel().getGameObjects();
		
		//Cycles through the level's GameObjects.
		for(int i = 0; i < gameObjects.size; i++)
		{
			//Stores the GameObject
			GameObject go = gameObjects.get(i);
			
			//If the GameObject in the array is null, skip this iteration to avoid NullPointerExceptions.
			if(go == null || !go.getCollider().insideCamera(worldCamera))
			{
				continue;
			}
			
			//Stores whether or not to draw the GameObject with transparency.
			boolean drawTransparent = false;
			
			//If the world's level is a TerrainLevel
			if(world.getLevel() instanceof TerrainLevel)
			{
				//If the GameObject is not in the center row, it is not in the Player's current row. Thus, draw the object with transparency.
				if(go.getTerrainCell().getRow() != ((TerrainLevel)world.getLevel()).getCenterRow())
				{
					//Draw the GameObject transparently.
					drawTransparent = true;
				}
			}
			
			//If the GameObject to draw is a Projectile
			if(go instanceof Projectile)
				//Delegate the draw call to the ProjectileRenderer class.
				projectileRenderer.draw((Projectile) go);
			//Else, if the GameObject is an InteractiveObject
			else if(go instanceof InteractiveObject)
				//Pass the drawing call to the InteractiveObjectRenderer, specifying whether or not it should be drawn transparent.
				interactiveObjectRenderer.draw((InteractiveObject) go, drawTransparent);
			//Else, if the GameObject that is being cycled through is a zombie.
			else if(go instanceof Zombie)
				//Pass the rendering call to the ZombieRenderer, which will draw the zombie to the screen.
				zombieRenderer.draw((Zombie) go, drawTransparent, deltaTime);
			//Else, if the GameObject is an item that has been dropped in the world
			else if(go instanceof ItemObject)
				//Pass the rendering to the ItemObjectRenderer, and tell it whether or not the item should be drawn transparent.
				itemObjectRenderer.draw((ItemObject) go, drawTransparent);
		}
		
	}

}
