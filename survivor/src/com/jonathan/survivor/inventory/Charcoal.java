package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public class Charcoal extends Craftable
{
	/** Stores the properties of the Charcoal item. */
	private static final String NAME = "Charcoal";	
	private static final String DESCRIPTION = "Basic tool of survival";
	
	private static final String ITEM_ATTACHMENT_NAME = "Charcoal";
	
	/** Creates a Charcoal item that can be displayed in an inventory slot. */
	public Charcoal()
	{
		super(NAME, DESCRIPTION);
		
		//Sets the name of the attachment in Spine that will display the wood as an item in the world.
		setItemAttachment(ITEM_ATTACHMENT_NAME);
	}
}