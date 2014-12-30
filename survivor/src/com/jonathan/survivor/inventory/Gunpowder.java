package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Gunpowder extends Craftable
{
	/** Stores the properties of the Gunpowder item. */
	private static final String NAME = "Gunpowder";	
	private static final String DESCRIPTION = "Basic tool of survival";
	
	private static final String ITEM_ATTACHMENT_NAME = "Gunpowder";
	
	/** Creates a Gunpowder item that can be displayed in an inventory slot. */
	public Gunpowder()
	{
		super(NAME, DESCRIPTION);
		
		//Sets the name of the attachment in Spine that will display the wood as an item in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}
