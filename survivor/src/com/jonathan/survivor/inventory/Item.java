package com.jonathan.survivor.inventory;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.jonathan.survivor.Assets;

public abstract class Item 
{
	/** Holds the name of the slot where the item's image is attached for the Item skeleton in Spine. Allows to change an ItemObject's appearance. */
	public static final String SLOT_NAME = "Item";
	
	/** Stores the name of the item. */
	private String name;
	/** Holds the description of the item. */
	private String description;
	
	/** Stores the name of the image which displays the item's image in Spine. This is the image that will be displayed on the ItemObject containing this item. */
	private String itemAttachment;
	
	/** Creates an item with the given name and description. */
	public Item(String name, String description)
	{
		//Populates the member variables with the given arguments.
		this.name = name;
		this.description = description;
	}
	
	/** Gets the item's name. */
	public String getName() {
		return name;
	}
	
	/** Sets the item's name. */
	public void setName(String name) {
		this.name = name;
	}
	
	/** Gets the item's description. */
	public String getDescription() {
		return description;
	}
	
	/** Sets the item's description. */
	public void setDescription(String description) {
		this.description = description;
	}

	/** Retrieves the name of the attachment used in Spine to display this Item when it is an object in the world. */
	public String getItemAttachment() {
		return itemAttachment;
	}

	/** Sets the name of the attachment used in Spine to display this Item when it is an object in the world. */
	public void setItemAttachment(String itemAttachment) {
		this.itemAttachment = itemAttachment;
	}
	
	
}
