package com.jonathan.survivor.inventory;

public abstract class Item 
{
	/** The different types of possible items. */
	public enum Type {
		WEAPON, CRAFTABLE
	}
	
	/** Stores the item's type. */
	private Type type;
	
	/** Stores the name of the item. */
	protected String name;
	/** Stores the description of the item. */
	protected String description;
	
	/** Creates an item with the given name and description. */
	public Item(Type type, String name, String description)
	{
		//Populates the member variables with the given arguments.
		this.type = type;
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
	
	
}
