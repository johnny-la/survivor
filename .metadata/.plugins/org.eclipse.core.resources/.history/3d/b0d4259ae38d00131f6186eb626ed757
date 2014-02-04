package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Wood extends Item
{
	/** Stores the properties of the Wood item. */
	private static final Type type = Type.CRAFTABLE;
	private static final String NAME = "Wood";	
	private static final String DESCRIPTION = "Basic tool of survival";
	private static final Sprite INVENTORY_SPRITE = Assets.instance.woodSprite;	//Stores the sprite instance used to to display the item in an inventory.
	
	private static final String ITEM_ATTACHMENT_NAME = "Wood";
	
	/** Creates a Wood item that can be displayed in an inventory slot. */
	public Wood()
	{
		super(Type.CRAFTABLE, NAME, DESCRIPTION, new Sprite(INVENTORY_SPRITE));	//A copy of the INVENTORY_SPRITE was created so that each wood item has an different sprite.
		
		//Sets the name of the attachment in Spine that will display the wood when it is dropped in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}
