package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Wood extends Craftable
{
	/** Stores the properties of the Wood item. */
	private static final String NAME = "Wood";	
	private static final String DESCRIPTION = "Basic tool of survival";
	
	private static final String ITEM_ATTACHMENT_NAME = "Wood";
	
	/** Creates a Wood item that can be displayed in an inventory slot. */
	public Wood()
	{
		super(NAME, DESCRIPTION);
		
		//Sets the name of the attachment in Spine that will display the wood as an item in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}
