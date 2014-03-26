package com.jonathan.survivor;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.utils.Array;
import com.jonathan.survivor.entity.Box;
import com.jonathan.survivor.entity.Clickable;
import com.jonathan.survivor.entity.GameObject;
import com.jonathan.survivor.entity.Human;
import com.jonathan.survivor.entity.Human.Direction;
import com.jonathan.survivor.entity.Human.Mode;
import com.jonathan.survivor.entity.Human.State;
import com.jonathan.survivor.entity.InteractiveObject;
import com.jonathan.survivor.entity.ItemObject;
import com.jonathan.survivor.entity.ItemObject.ItemState;
import com.jonathan.survivor.entity.Player;
import com.jonathan.survivor.entity.PlayerListener;
import com.jonathan.survivor.entity.Tree;
import com.jonathan.survivor.entity.Zombie;
import com.jonathan.survivor.inventory.Item;
import com.jonathan.survivor.managers.GameObjectManager;
import com.jonathan.survivor.managers.ItemManager;
import com.jonathan.survivor.managers.ZombieManager;
import com.jonathan.survivor.math.Vector2;

public class World
{
	/** Stores the acceleration due to gravity in the world in m/s^2 when the player is in exploration state. */
	public static final Vector2 GRAVITY_EXPLORATION = new Vector2(0, -15f);
	
	/** Stores the acceleration due to gravity in the world in m/s^2 when the player is fighting a zombie in combat state. */
	public static final Vector2 GRAVITY_COMBAT = new Vector2(0, -45f);
	
	/** Stores the seed used to generate the geometry of the level and randomly place its GameObjects. */
	private int worldSeed;

	/** Stores the profile used to create the world from a save file. */
	private Profile profile;
	
	/** A state the GameScreen needs to know about to change the UI */
	public enum WorldState {
		EXPLORING, VERSUS_ANIMATION, COMBAT, GAME_OVER
	};
	
	/** Stores the state of the world, which simply dictates the GUI the GameScreen should display. */
	private WorldState worldState;

	/** Holds the GameObject Manager instance used to manage the GameObjects in the world. */
	private GameObjectManager goManager;
	
	/** Stores the ZombieManager which updates zombies every game tick and controls their AI. */
	private ZombieManager zombieManager;
	
	/** Holds the ItemManager instance. Used to pool and retrieve Item instances given to every ItemObject spawned in the world.. */
	private ItemManager itemManager;
	
	/** Stores the currently-active level of the world that is being displayed. Determines the walkable area of the world. */
	private Level level;
	/** Stores an instance of a terrain level, used when the player is outside in the procedurally-generated terrain. */
	private TerrainLevel terrainLevel;
	/** Holds an instance of a combat level, used when the player is fighting a zombie in combat mode. */
	private CombatLevel combatLevel;
	
	/** Holds the Player GameObject that the user is guiding around the world. */
	private Player player;
	
	/** Stores the WorldListener instance which delegates events from the World to the GameScreen. */
	private WorldListener worldListener;	
	
	/** Listens to events delegated by the player. */
	private EventListener eventListener;
	
	/** Helper Vector2 used to store the world coordinates of the last known touch. */
	private Vector2 touchPoint;

	/** Accepts the world seed from which terrain is generated, the profile from which save data is retrieved, and the ItemManager from which 
	 *  Item instances are retrieved and given to ItemObjects which are spawned in the world.
	 */
	public World(int worldSeed, Profile profile, ItemManager itemManager)
	{
		//Stores the seed used to randomly generate the world.
		this.worldSeed = worldSeed;
		//Stores the profile used to generate the world from its save data.
		this.profile = profile;
		
		//Sets the world to its exploration state, since the user always starts off in exploration state. This ensures the correct UI is drawn.
		worldState = WorldState.EXPLORING;
		
		//Creates a GameObjectManager which stores and creates and manages all the instances of GameObjects.
		goManager = new GameObjectManager(profile);
		
		//Instantiates the ZombieManager, which updates every zombie instance and manipulates their artificial intelligence.
		zombieManager = new ZombieManager(this);
		
		//Stores the ItemManager instance, which provide Item instances to item GameObjects which are spawned in the world.
		this.itemManager = itemManager;
		
		//Creates a new TerrainLevel using the profile, which specifies the world seed of the level. Also permits the level to be created where
		//the user left off. This level is a container of TerrainLayers that the user can traverse.
		terrainLevel = new TerrainLevel(profile, goManager);
		//Instantiates a CombatLevel, used when the player fight against a zombie.
		combatLevel = new CombatLevel();
		
		//The TerrainLevel is always the default level for the player upon world instantiation.
		setLevel(terrainLevel);
		
		//Stores the player created inside the GameObjectManager inside a member variable.
		player = goManager.getPlayer();		
		//Sets up the player's initial values when being dropped into the world.
		setupPlayer();
		
		//Creates a new listener which will have its methods delegated by GameObjects.
		eventListener = new EventListener();
		
		//Sets the eventListener to listen for the player's events.
		player.setListener(eventListener);
		
		//Creates a Vector2 instance to hold the coordinates of the latest touch.
		touchPoint = new Vector2();
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
		//If the player is in exploration state
		if(player.getMode() == Mode.EXPLORING)
		{
			//Delegate the update call to the updatePlayerExploring() method.
			updatePlayerExploring();
		}
		else if(player.getMode() == Mode.COMBAT)
		{
			//Delegate the update call to the updatePlayerCombat() method.
			updatePlayerCombat();
		}
		
		//Updates the position of the player based on his velocity and updates his collider's position.
		player.update(deltaTime);
	}

	/** Updates the player in the world when he's in EXPLORATION state, and is traversing the world. */
	private void updatePlayerExploring() 
	{
		//Check if the player has collided with anything of importance.
		checkPlayerCollisions();
		
		//If the player is in IDLE state
		if(player.getState() == State.IDLE)
		{
			
		}
		//If the player is jumping
		if(player.getState() == State.JUMP || player.getState() == State.FALL)
		{
			//Set the player's acceleration to the acceleration gravity.
			player.setAcceleration(GRAVITY_EXPLORATION.x, GRAVITY_EXPLORATION.y);
			
			//FAILSAFE. Checks if the player switched layers by either moving to the right or left of his current layer. If so, we move the player over by a cell. 
			checkForLayerSwitch(player);
			
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
				//Apply a positive x-velocity to the player using the predefined player's walking speed.
				player.setVelocity(player.getWalkSpeed(), 0);
			}
			//Else, if the player is walking to the left
			else
			{
				//Apply a negative x-velocity to the player using the predefined walking speed constant.
				player.setVelocity(-player.getWalkSpeed(), 0);
			}
			
			//Checks if the player switched layers by either moving to the right or left of his current layer. If so, we move the player over by a cell.
			checkForLayerSwitch(player);
			
			//Makes the player follow the level's ground.
			lockToGround(player);
		}
	}
	
	/** Updates the player when he is in COMBAT mode, fighting another zombie */
	private void updatePlayerCombat() 
	{
		//Check if the player has collided with anything of importance.
		checkPlayerCollisions();
		
		//If the player is in IDLE state, just standing there
		if(player.getState() == State.IDLE)
		{
			
		}
		//Else, if the player is currently jumping
		else if(player.getState() == State.JUMP)
		{
			//Set the player's acceleration to the acceleration gravity.
			player.setAcceleration(GRAVITY_COMBAT.x, GRAVITY_COMBAT.y);
			
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
				player.setY(combatLevel.getGroundHeight(player.getX()));
			}
		}
		
	}

	/** Updates the GameObjects contained by the currently active level of the world. */
	private void updateLevelObjects(float deltaTime)
	{
		//Retrieves all of the GameObjects stored inside the world's level
		Array<GameObject> gameObjects = level.getGameObjects();
		
		//Cycle through all of the GameObjects contained in the level
		for(int i = 0; i < gameObjects.size; i++)
		{
			//Store the GameObject
			GameObject go = gameObjects.get(i);
			
			//If the GameObject in the level is an ItemObject that is waiting to be picked up
			if(go instanceof ItemObject)
			{
				//Update the ItemObject.
				updateItemObject((ItemObject) go, deltaTime);
			}
			//If the GameObject contained in the level is a Zombie
			else if(go instanceof Zombie)
			{
				//Update the zombie's game logic.
				zombieManager.update((Zombie) go, deltaTime);
			}
			//Else, for any other generic GameObject, simply update the GameObject.
			else
			{	
				//Update the GameObject.
				go.update(deltaTime);
			}
		}
	}

	/** Updates the Item Object's game logic. Note that an ItemObject is an item on the ground that can be looted. */
	private void updateItemObject(ItemObject itemObject, float deltaTime)
	{
		//If the ItemObject has spawned and is flying through the air
		if(itemObject.getItemState() == ItemState.FLY)
		{
			//Apply gravity to the Item GameObject so that it falls to the ground.
			itemObject.setAcceleration(GRAVITY_EXPLORATION.x, GRAVITY_EXPLORATION.y);
			
			//Modify the ItemObject's cell if the object flies into a different cell. Ensures that the object is on the right cell.
			//MAY CAUSE ERRORS -- The incorrect TerrainLayer will hold the ItemObject. Could add a case to change the correct TerrainLayer to hold the ItemObject.
			checkForLayerSwitch(itemObject);
			
			//Check for a ground collision, and abruptly stop the GameObject if it has landed.
			if(checkGroundCollision(itemObject))
			{	
				//Set the Item GameObject's velocity and acceleration to zero to stop it from falling.
				itemObject.setVelocity(0, 0);
				itemObject.setAcceleration(0, 0);
				
				//Tell the ItemObject that it is on the ground.
				itemObject.setItemState(ItemState.GROUNDED);
			}
		}
		
		//Update the ItemObject, along with its collider and its position.
		itemObject.update(deltaTime);
	}
	
	/** Listens for events delegated by the GameObjects of the world. */
	class EventListener implements PlayerListener 
	{
		/** Delegates when the Player scavenges an Interactive GameObject. Scavenging means to destroy a target, such as destroying a tree. */
		@Override
		public void scavengedObject(InteractiveObject object)
		{
			//Plays the SCAVENGED animation of the tree, and spawns items from it.
			scavengeObject(object);
		}
	}
	
	/** This method is called once to make the human move in the given direction. */
	public void walk(Human human, Direction direction)
	{
		//If the player is jumping or falling, don't let him move left, or serious glitches will occur.
		if(human.getState() == State.JUMP || human.getState() == State.FALL)
			return;
		
		//Sets the player to the walking state so that the world can apply a velocity to it.
		human.setState(State.WALK);
		//Makes the player walk in the given direction. This world's update() method knows how to interpret this direction and make the human walk in the right direction.
		human.setDirection(direction);
		
		//Make the player lose his target once he has started to walk using the arrow buttons.
		human.loseTarget();
	}
	
	/** Stops the given Human GameObject from moving. */
	public void stopMoving(Human human) 
	{
		//Stops the human from walking in the x-direction.
		human.setVelocityX(0);
		
		//Only set the human to idle state if he is not jumping or falling. Otherwise, he will be set to IDLE whilst jumping/falling, causing glitches.
		//Regardless, the human will be set to IDLE once he lands his jump/fall. Or, if the human is in COMBAT mode, the JUMP/FALL state can be interrupted.
		if((human.getState() != State.JUMP && human.getState() != State.FALL) || human.getMode() == Mode.COMBAT)
		{
			//Set the human's y-velocity to zero in the case that he was jumping or falling. Ensures that the human completely stops moving.
			human.setVelocityY(0);
			
			//Tells the human instance that it is in idle state so that it stops moving. 
			human.setState(State.IDLE);
		}
	}
	
	/** Makes the player walk to the specified GameObject. */
	public void setTarget(Human human, GameObject target)
	{
		//If the Human is jumping or falling, don't let him walk to the target. If the GameObject can't be targetted, return this method.
		if(human.getState() == State.JUMP || human.getState() == State.FALL || !target.canTarget())
			return;
		
		//If the target is a tree, and the player wants to target the tree
		if(target instanceof Tree && human instanceof Player)
		{
			//If the player does not have a melee weapon, return the method to prevent the user from targetting or chopping the tree.
			if(!((Player)human).hasMeleeWeapon())
				return;
		}
		
		//Set the human's target to the given GameObject
		human.setTarget(target);
		
		//Set the human to WALK state
		human.setState(State.WALK);
		
		//If the target is to the right of the human
		if(target.getX() > human.getX())
		{
			//Make the human walk right.
			human.setDirection(Direction.RIGHT);
		}
		//Else, if the target is to the left of the human
		else
		{
			//Make the human walk left.
			human.setDirection(Direction.LEFT);
		}
		
		//If the clicked GameObject is an Interactive GameObject.
		if(target instanceof InteractiveObject)
		{
			//Tell the GameObject it has been clicked and is being targetted.
			((InteractiveObject)target).targetted();
		}
	}
	
	/** Called when a GameObject in the level was touched. */
	private void gameObjectClicked(GameObject gameObject)
	{
		//If the GameObject is clickable
		if(gameObject instanceof Clickable)
		{
			//Make the player walk to the Clickable target
			setTarget(player, gameObject);
		}
		//Else, if an ItemObject on the ground was clicked
		else if(gameObject instanceof ItemObject)
		{
			//Collect the clicked ItemObject and add it to the player's inventory.
			collectItemObject((ItemObject) gameObject);
		}
	}
	
	/** Makes the versus animation play. When finished the player switches to combat mode. */
	public void playVersusAnimation()
	{
		//Tells the AnimationRenderer to play the versus animation. When finished, the world is switched to Combat mode.
		setWorldState(WorldState.VERSUS_ANIMATION);
		
		//Tells the GameScreen to pause the game until the versus animation stops playing.
		worldListener.onPlayAnimation();		
	}
	
	/** Makes the player enter combat with the zombie he has collided with. Called from the VersusAnimation class when the versus animation is finished playing. */
	public void enterCombat() 
	{
		//Tells the GameScreen to resume the game since the versus animation is complete and we would like to switch to combat mode.
		worldListener.onAnimationComplete();
		
		//Informs the GameScreen that it should switch to the combat HUD. Like this, the right UI widgets will be shown for combat.
		worldListener.switchToCombat();
		
		//Sets the world to COMBAT state. As such, the world's game logic will be handled differently, and the Combat Hud will be displayed instead of the Exploration one.
		setWorldState(WorldState.COMBAT);
		
		//Inform the combat level which player and zombie are fighting. Positions the humans at the right place on the level.
		combatLevel.startFighting(player, player.getZombieToFight());
		
		//Makes sure the player and the zombie stop moving before entering combat.
		stopMoving(player);
		stopMoving(player.getZombieToFight());
		
		//Tell the world to use the combat level. This level will now be rendered and used as the playing surface for all GameObjects.
		setLevel(combatLevel);
	}
	
	/** Checks if the player is colliding with any GameObjects. */
	private void checkPlayerCollisions()
	{
		//If the player is in EXPLORATION mode, check collisions accordingly.
		if(player.getMode() == Mode.EXPLORING)
		{
			//Checks if the player has collided with his target.
			checkTargetCollisions();
		}
		//Else, if the player is in COMBAT mode, fighting a zombie
		else if(player.getMode() == Mode.COMBAT)
		{
			//Check if the player has intersected with anything in combat
			checkCombatCollisions();
		}
	}
	
	/** Checks if the player has made any collisions whilst in COMBAT mode with another zombie. */
	private void checkCombatCollisions() 
	{
		//Retrieves the zombie that the player is fighting.
		Zombie zombie = player.getZombieToFight();
		
		//If the player is jumping
		if(player.getState() == State.JUMP)
		{
			//If the player hit the zombie
			if(player.getCollider().intersects(zombie.getCollider()))
			{
				//If the player was falling down when hitting the zombie, he hit the zombie's head
				if(player.getVelocity().y < 0)
				{
					//Make the player hit the zombie's head, making him jump after the hit.
					player.hitHead(zombie);
				}
			}
			
		}
		//Else, if the player wasn't jumping, but the zombie hit the player while charging.
		else if(zombie.getState() == State.CHARGE && player.getCollider().intersects(zombie.getCollider()))
		{
			//Make the zombie charge hit the player.
			zombie.chargeHit(player);
		}
		
	}

	/** Checks if the player has collided with his target. */
	private void checkTargetCollisions()
	{
		//Stores the target where the player wants to walk to, if it exists
		GameObject target = player.getTarget();
		//If the player has a target, and the player has touched his target
		if(target != null && !player.isTargetReached() && player.getCollider().intersects(target.getCollider()))
		{
			//Tell the player he has reached his target.
			player.setTargetReached(true);
			
			//Stops the player from moving.
			stopMoving(player);
			
			//If the player's target was a tree.
			if(target instanceof Tree)
			{
				//Start chopping the tree. The player knows his target is the tree to chop.
				player.chopTree();
			}
			//Else, if the player's target was a box, open the box.
			else if(target instanceof Box)
			{
				//Open the box and spring out items from it.
				scavengeObject((InteractiveObject)target);
			}
			//Else, if the player's target was a zombie, act accordingly.
			else if(target instanceof Zombie)
			{
				//If the zombie was touched by the player, tell the zombie it is no longer being targetted, since the zombie was reached.
				((Zombie) target).setTargetted(false);
			}
		}
	}
	
	/** Scavenges the given object and spawns items from it. Called when a box is opened or a tree is destroyed. */
	private void scavengeObject(InteractiveObject scavengedObject) 
	{
		//Tell the InteractiveObject it was scavenged. Switches its state to SCAVENGED so that its correct animation plays.
		scavengedObject.scavenged();
		
		//Adds the scavenged GameObject to the profile. Like this, if, say, a tree was just scavenged, it will never re-appear in the same TerrainLayer.
		profile.addScavengedLayerObject(scavengedObject);
		
		//Spawn items from the given scavenged object. Spawns them according to the InteractiveObject's itemProbabilityMap:HashMap.
		spawnItems(scavengedObject);
		
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
				//If a Zombie just switched layers
				if(gameObject instanceof Zombie)
					//Remove the zombie from its current TerrainLayer
					terrainLevel.removeGameObject(gameObject);
				
				//Move the GameObject's cell to the right so that the GameObject knows that he has changed cells.
				gameObject.getTerrainCell().moveRight();
				
				//If the GameObject which switched layers is the player
				if(gameObject instanceof Player)
					//Shift the layers of the level to the right so that the player stays in the center cell.
					terrainLevel.shiftLayersRight();
				//If a Zombie just switched layers
				if(gameObject instanceof Zombie)
					//Add the zombie to the TerrainLayer it just moved to.
					terrainLevel.addGameObject(gameObject);
			}
			//If the the player has moved to the left of his current layer
			else if(gameObject.getX() < terrainLayer.getLeftPoint().x)
			{
				//If a Zombie just switched layers
				if(gameObject instanceof Zombie)
					//Remove the zombie from its current TerrainLayer
					terrainLevel.removeGameObject(gameObject);
				
				//Move the GameObject's cell to the left so that the GameObject knows that he has changed cells.
				gameObject.getTerrainCell().moveLeft();
				
				//If the GameObject which switched layers is the player
				if(gameObject instanceof Player)
					//Shift the layers of the level to the left so that the player stays in the center cell.
					terrainLevel.shiftLayersLeft();
				//Else, if a Zombie just switched layers
				else if(gameObject instanceof Zombie)
					//Add the zombie to the TerrainLayer it just moved to.
					terrainLevel.addGameObject(gameObject);
			}
		}
	}
	
	/** Returns true if the GameObject is jumping or falling and has fell past the ground. */
	public boolean checkGroundCollision(GameObject gameObject)
	{
		//If the GameObject is in a TerrainLevel (i.e., a layer level)
		if(level instanceof TerrainLevel)
		{
			//If the GameObject is falling and has fallen below ground height
			if(gameObject.getVelocity().y < 0 && gameObject.getY() < terrainLevel.getTerrainLayer(gameObject.getTerrainCell()).getGroundHeight(gameObject.getX()))
			{
				//Return true, as the gameObject has fallen below the ground.
				return true;
			}
		}
		//Else, if the currently active level is a combat level, the player is fighting a zombie
		else if(level instanceof CombatLevel)
		{
			//If the GameObject is falling and has fallen below ground height
			if(gameObject.getVelocity().y < 0 && gameObject.getY() < combatLevel.getGroundHeight(gameObject.getX()))
			{
				//Return true, as the gameObject has fallen below the ground.
				return true;
			}
		}
		
		//Return false if the GameObject has not been found to be falling below ground height.
		return false;
	}	
	
	/** Spawns items at the GameObject's location. Called when a tree is chopped down or any other GameObject is scavenged/destroyed. */
	public void spawnItems(GameObject gameObject)
	{
		//Stores the HashMap of the InteractiveGameObject, which indicates which items can be dropped once the object is scavenged.
		HashMap<Class, Float> itemProbabilityMap = ((InteractiveObject)gameObject).getItemProbabilityMap();
		
		//Stores the amount of items that have been spawned. Allows items to fly further if items have already been spawned.
		int itemsSpawned = 0;
		
		//Creates a set out of each key of the probability map. Each key is an Item subclass. Each key is an item that has a probability of being dropped from the 
		//Interactive GameObject.
		Set<Class> keys = itemProbabilityMap.keySet();
		
		//Cycles through each possible item type that can be dropped from the scavenged GameObject.
		for(Class key:keys)
		{						
			//Check if a random number is less than the probability of the item dropping. The probability of the item to drop is stored in the key of the HashMap,
			//where the key is the Item subclass that has a probability of being dropped. Note that the value of the key can be between 0 and 1, where 1 means that
			//the item will be dropped no matter the circumstances.
			if(Math.random() < itemProbabilityMap.get(key))
			{
				//Stores the ItemObject spawned in the world.
				ItemObject itemObject = null;
				
				//The Class.newInstance() method may cause an exception.
				try 
				{
					//Spawns an ItemObject at the position of the destroyed GameObject. The first two arguments indicate the (x,y) position where the items will be
					//spawned. Third argument is a velocity multiplier, allowing items to fly further depending on how many items have already been spawned. Last 
					//argument specifies that the items should fly the same direction that the player is facing.
					itemObject = goManager.spawnItemObject(gameObject.getPosition().x, gameObject.getPosition().y, 1 + itemsSpawned*0.6f, player.getDirection());
					
					//Stores the previous Item instance held by the ItemObject. This is because ItemObjects are pooled, and thus may have an old Item instance.
					Item previousItem = itemObject.getItem();
					
					//If the ItemObject's previous item is not null
					if(previousItem != null)
					{
						//Free the item back into the itemManager's pools for later reuse. Prevents the previous item from being garbage collected.
						itemManager.freeItem(previousItem);
					}
					
					//Obtains a new item of the given class from the itemManager, and sets it as the item the ItemObject represents.
					itemObject.setItem(itemManager.obtainItem(key));
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
				
				//Tells the ItemObject that it is on the same TerrainCell as the GameObject which dropped this item. Allows the object to know which TerrainLayer it 
				//belongs to.
				itemObject.setTerrainCell(gameObject.getTerrainCell().getRow(), gameObject.getTerrainCell().getCol());
				
				//Adds the spawned ItemObject to the list of ItemObjects inside the level. It is added to the correct TerrainLayer if the user is on a TerrainLevel.
				level.addGameObject(itemObject);
				
				//Increments the amount of items that have been spawned at the GameObject.
				itemsSpawned++;
			}
		}
	}
	
	/** Makes the user pick up the given Item GameObject, removing the GameObject from the world and adding it to the inventory. */
	public void collectItemObject(ItemObject itemObject)
	{
		//Tells the ItemObject that it has been clicked so that the correct animation plays.
		itemObject.setItemState(ItemState.CLICKED);
		//Moves the item to the player's center position in the given amount of seconds, specified by the last parameter.
		itemObject.moveTo(player.getX(), player.getY() + player.COLLIDER_WIDTH/2, 0.4f);
		
		//Increments the amount of this item contained in the inventory by one.
		player.getInventory().addItem(itemObject.getItem().getClass(), 1);
	}
	
	/** Called when a touch was registered on the screen. Coordinates given in world units. O(n**2) OPTIMIZE THIS. */
	public void touchUp(float x, float y)
	{
		//Store the coordinates of the touch point inside a Vector2.
		touchPoint.set(x, y);
		
		//If the currently-active level is a TerrainLevel
		if(level instanceof TerrainLevel)
		{
			//Store the middle layers, where the player resides, and where objects can be touched.
			TerrainLayer[] middleLayers = terrainLevel.getMiddleLayers();
			
			//Cycle through the middle layers of the level
			for(int i = 0; i < middleLayers.length; i++)
			{
				//Store the GameObjects residing in the middle layer
				Array<GameObject> gameObjects = middleLayers[i].getGameObjects();
				
				//Cycle through the GameObjects of the middle layer. Starts from the end of the list since those are the gameObjects at the front of the screen.
				for(int j = gameObjects.size-1; j >= 0; j--)
				{
					//Store the GameObject
					GameObject go = gameObjects.get(j);
					
					//If the touch point is inside the GameObject's collider, report a touched GameObject.
					if(go.getCollider().intersects(touchPoint))
					{
						//The GameObject has been clicked.
						gameObjectClicked(go);
						
						//Only allow one object to be touched with one click.
						return;
					}
				}
			}
		}
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
	
	/** Returns true if the GameObject is close to the left or right edges of his TerrainLayer. */
	public boolean closeToLayerEdge(GameObject gameObject) 
	{
		//Stores the TerrainLayer where the GameObject is located.
		TerrainLayer terrainLayer = terrainLevel.getTerrainLayer(gameObject);
		
		//If the given GameObject is close to the layer's edge, return true.
		if(terrainLayer.closeToEdge(gameObject))
			return true;
		//Else, if the GameObject is far from its layer's edge, return false.
		else
			return false;
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
	
	/** Gets the TerrainLevel used by the world. */
	public TerrainLevel getTerrainLevel() 
	{
		//Returns the TerrainLevel of the world.
		return terrainLevel;
	}
	
	/** Sets the TerrainLevel used by the world. */
	public void setTerrainLevel(TerrainLevel terrainLevel) 
	{
		//Updates the TerrainLevel of the world.
		this.terrainLevel = terrainLevel;
	}
	
	/** Retrieves the combat level of the world. */
	public CombatLevel getCombatLevel() {

		//Returns the world's combat level.
		return combatLevel;
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

	/** Retrieves the WorldListener which delegates World events to the GameScreen. */
	public WorldListener getWorldListener() {
		return worldListener;
	}

	/** Sets the WorldListener which delegates World events to the GameScreen. */
	public void setWorldListener(WorldListener worldListener) {
		this.worldListener = worldListener;
	}
	
}
