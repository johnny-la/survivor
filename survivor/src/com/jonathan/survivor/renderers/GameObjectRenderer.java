package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.World;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Tree;
import com.jonathan.survivor.entity.Tree.TreeState;
import com.jonathan.survivor.math.Rectangle;

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
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
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
		
		//Creates a playerRenderer from the player GameObject, the SpriteBatch instance to draw the player, and the worldCamera, where the Player will be drawn.
		playerRenderer = new PlayerRenderer(world.getPlayer(), batcher, worldCamera);
		
	}
	
	public void render()
	{				
		//Sets the projection matrix of the SpriteBatch to the camera's combined matrix. Ensure everything is drawn with the camera's coordinate system.
		batcher.setProjectionMatrix(worldCamera.combined);
		//Starts batching sprites to be drawn to the camera.
		batcher.begin();

		//Renders the GameObjects stored inside the currently active level.
		renderLevelObjects();
		//Draws the player to the screen.
		playerRenderer.render();
		
		//Draws the sprites batched inside the SpriteBatcher.
		batcher.end();
	}

	/** Draws the GameObjects that are contained inside the world's level. */
	private void renderLevelObjects() 
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
				System.out.println(go.getCollider().toString());
				//System.out.println("GameObject within camera? " + go.getCollider().insideCamera(worldCamera));
				continue;
			}
			
			
			//If the GameObject is a tree
			if(go instanceof Tree)
				//Render the tree, casting it into its appropriate type.
				renderTree((Tree)go);
		}
		
	}
	
	/** Renders a Tree GameObject, which contains a Spine Skeleton instance which can be drawn to the screen. */
	private void renderTree(Tree tree)
	{
		//Stores the Spine skeleton of the tree which controls its appearance.
		Skeleton skeleton = tree.getSkeleton();
		
		//Sets the bottom-center position of the skeleton to the tree's bottom-center position. Note that Skeleton/GameObject.position hold the bottom-center of the tree.
		skeleton.setX(tree.getX());
		skeleton.setY(tree.getY());
		
		//If the tree has jump spawned
		if(tree.getTreeState() == TreeState.SPAWN)
		{
			//Reset the tree's skeleton to its setup pose to undo any changes previously done to the skeleton's bones.
			skeleton.setToSetupPose();
			
			//Sets the state time of the tree to a random time so that the idle animation starts playing at a random place for every tree.
			tree.setStateTime((float)Math.random() * 10);
			//Sets the tree's state to IDLE, indicating that the renderer has received the message that the tree has spawned.
			tree.setTreeState(TreeState.IDLE);
		}
		//Else, if the tree is in IDLE state
		else if(tree.getTreeState() == TreeState.IDLE)
		{
			//Apply the 'treeIdle' animation to the tree's skeleton. Second and third arguments specify how much time the tree has been idle, third indicates we want to 
			//loop the animation, and last is an array where any possible animation events are delegated.
			assets.treeIdle.apply(skeleton, tree.getStateTime(), tree.getStateTime(), true, events);
		}
		
		//Updates the Skeleton in the world.
		skeleton.updateWorldTransform();
		
		//Draws the tree's skeleton using the universal SkeletonRenderer instance, along with the GameScreen's SpriteBatch.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}

}
