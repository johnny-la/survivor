package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Water extends Craftable
{
	/** Stores the properties of the Water item. */
	private static final String NAME = "Water";	
	private static final String DESCRIPTION = "Basic resource for survival";
	
	private static final String ITEM_ATTACHMENT_NAME = "Water";
	
	/** Creates a Water item that can be displayed in an inventory slot. */
	public Water()
	{
		super(NAME, DESCRIPTION);
		
		//Sets the name of the attachment in Spine that will display the wood as an item in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}
