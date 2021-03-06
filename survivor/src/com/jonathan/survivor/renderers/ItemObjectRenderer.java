package com.jonathan.survivor.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.Event;
import com.esotericsoftware.spine.Skeleton;
import com.jonathan.survivor.Assets;
import com.jonathan.survivor.entity.ItemObject;
import com.jonathan.survivor.entity.ItemObject.ItemState;
import com.jonathan.survivor.inventory.Item;

public class ItemObjectRenderer 
{
	/** Stores the SpriteBatch instance used to display the ItemObject. */
	private SpriteBatch batcher;
	
	/** Stores the Assets singleton which stores all of the visual assets needed to draw the interactive GameObjects. */
	private Assets assets = Assets.instance;
	
	/** Stores the color of transparent GameObjects. */
	private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0.4f);
	
	/** Helper Color instance used to color GameObjects and avoid creating new color instances. */
	private Color workingColor;
	
	//Helper Array that's passed to the Animation.set() method.
	private Array<Event> events = new Array<Event>();
	
	/** Accepts the SpriteBatch instance used to render the ItemObjects passed to render(). */
	public ItemObjectRenderer(SpriteBatch batcher)
	{
		//Stores the arguments in their respective member variables.
		this.batcher = batcher;
		
		//Instantiates the helper Color object used to color the ItemObjects.
		workingColor = new Color(Color.WHITE);
	}
	
	/** Accepts the ItemObject to draw, and whether or not to draw it transparent. */
	public void draw(ItemObject itemObject, boolean transparent)
	{
		//Stores the Skeleton instance owned by the ItemObject, which allows the object to be drawn to the screen.
		Skeleton skeleton = itemObject.getSkeleton();
		
		//Sets the skeleton to display the image of the inventory item held by the ItemObject.
		skeleton.setAttachment(Item.SLOT_NAME, itemObject.getItem().getItemAttachment());
		
		//Updates the position of the skeleton to that of the Item GameObject. The position for both is denoted by the bottom-center.
		skeleton.setX(itemObject.getX());
		skeleton.setY(itemObject.getY());
		
		//Reset the working color instance to WHITE so that the helper color starts from a clean slate.
		workingColor.set(Color.WHITE);
		
		//If the ItemObject has just spawned
		if(itemObject.getItemState() == ItemState.SPAWN)
		{
			//Ensure that the Skeleton is set to default pose to avoid animation errors
			skeleton.setToSetupPose();
			
			//The renderer has received the SPAWN message, so we switch the item to its FLY state.
			itemObject.setItemState(ItemState.FLY);
		}
		//Else, if the ItemObject is flying
		else if(itemObject.getItemState() == ItemState.FLY)
		{
			//Play the item's FLY animation. Second and third arguments specify how much time the object has been in its state, third indicates we want to 
			//loop the animation, and last is an array which stores animation events that are delegated.
			assets.itemFly.apply(skeleton, itemObject.getStateTime(), itemObject.getStateTime(), true, events);
		}
		//Else, if the ItemObject is current in IDLE state
		else if(itemObject.getItemState() == ItemState.GROUNDED)
		{
			//Play the item's GROUNDED animation. Second and third arguments specify how much time the object has been in its state, third indicates we want to 
			//loop the animation, and last is an array which stores animation events that are delegated.
			assets.itemGrounded.apply(skeleton, itemObject.getStateTime(), itemObject.getStateTime(), true, events);
		}
		//Else, if the ItemObject has been clicked and should be collected
		else if(itemObject.getItemState() == ItemState.CLICKED)
		{
			//Play the item's CLICKED animation. Second and third arguments specify how much time the object has been in its state, third indicates we want to 
			//play the animation only once, and last is an array which stores animation events that are delegated.
			assets.itemClicked.apply(skeleton, itemObject.getStateTime(), itemObject.getStateTime(), false, events);
		}
		
		//If the ItemObject is supposed to be drawn transparent
		if(transparent)
			//Multiply the working color by the TRANSPARENT_COLOR constant so that the ItemObject is drawn transparent.
			workingColor.mul(TRANSPARENT_COLOR);
		
		//Set the Item GameObject's color to the workingColor instance, which stored the color that the ItemObject should be.
		skeleton.getColor().set(workingColor);
		
		//Updates the world transform of the skeleton.
		skeleton.updateWorldTransform();
		
		//Draws the ItemObject to the world using the universal SkeletonRenderer instance.
		assets.skeletonRenderer.draw(batcher, skeleton);
	}
}
