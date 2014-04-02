package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Teleporter extends Craftable
{
	/** Stores the properties of the Teleporter item. */
	private static final String NAME = "Teleporter";	
	private static final String DESCRIPTION = "Basic tool of escape";
	
	private static final String ITEM_ATTACHMENT_NAME = "Teleporter";
	
	/** Creates a Teleporter item that can be displayed in an inventory slot. */
	public Teleporter()
	{
		super(NAME, DESCRIPTION);
		
		//Sets the name of the attachment in Spine that will display the teleporter as an item in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}
